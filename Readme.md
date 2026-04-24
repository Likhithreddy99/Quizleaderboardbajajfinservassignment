# Bajaj Finserv Quiz Leaderboard Assignment

This is the backend implementation for the Bajaj Finserv SRM Quiz task. 

## The Problem Explained

The objective is to manage a live quiz show environment where external systems constantly broadcast participant scores across various rounds. The core problem lies in data inconsistency. Network issues or system retries can cause the exact same score data to reach our backend multiple times. 

If a backend system indiscriminately aggregates all incoming score data, participants will receive duplicated points. This inflates their overall scores and generates an entirely inaccurate leaderboard. Our backend must act as a reliable filter that can identify and discard redundant score data before computing the final rankings.

To successfully do this, the system must follow a strict sequential process.
1. Connect to the data feed and poll the API ten separate times to retrieve all possible score updates.
2. Intercept and evaluate every single score record.
3. Detect identical score records and drop any duplicates to ensure each unique event is only counted once.
4. Combine the verified scores to determine the true total points earned by each participant.
5. Generate a final leaderboard sorting all participants from the highest score down to the lowest, and securely transmit this back to the validator system.

---

## Technical Implementation Details

The backend logic solves the challenge using standard Java data structures and Spring Boot mechanisms. Here is an exact breakdown of the architectural flow and system design.

### Data Polling and Thread Pausing
The application utilizes a standard iterative loop to make ten consecutive GET requests to the external messaging API. To prevent overloading the external server and to adhere to the strict assignment requirements, the application forcefully halts the execution thread for exactly five seconds before initiating the next polling cycle. This ensures paced and consistent data retrieval over a fifty-second window.

### Deduplication Logic Using HashSet
To guarantee no duplicate data is processed, the system assigns a unique composite identifier to every score event by concatenating the round ID and the participant's name into a single string. The system attempts to insert this identifier into a Java HashSet. Because HashSet data structures mathematically reject duplicate entries based on hashing principles, any identical identifier from a duplicated payload is instantly blocked. The system only processes the score if the identifier successfully enters the HashSet, proving it is a genuinely new event.

### Score Aggregation Using HashMap
For the actual score calculation, the system employs a HashMap mapping the participant's name to their accumulating point total. Whenever a genuinely new score event passes the HashSet filter, the system accesses the HashMap, retrieves that specific participant's current score, adds the newly verified points, and updates the map.

### Sorting the Leaderboard
After the fifty-second data gathering phase concludes, the system transfers the aggregated totals from the HashMap into an Array List. It then utilizes Java's built-in comparator functions to sort the objects in descending order. This algorithm calculates the numeric difference between point values to organize the list, ensuring the participant with the largest total score is securely placed at the top of the leaderboard.

### Final Submission
Once the data is fully processed and sorted, the system packages the leaderboard into a JSON object format. It executes a single HTTP POST request to transmit the final array to the external submission endpoint for validation.

## Project Structure

```text
Quizleaderboardbajajfinservassignment
├── Pom.xml
├── Readme.md
└── Src
    └── Main
        ├── Java
        │   └── Com
        │       └── App
        │           ├── Application.java
        │           ├── Configuration
        │           │   └── Setup.java
        │           ├── Controller
        │           │   └── Api.java
        │           ├── Model
        │           │   └── Item.java
        │           ├── Repository
        │           │   └── Store.java
        │           └── Service
        │               └── Logic.java
        └── Resources
            └── Application.properties
```

## Technology Stack
- Java 17
- Spring Boot 3.2.4
- Maven 

## Execution Instructions
1. Clone this repository to your local development environment.
2. Open your command line interface in the root project directory and start the Spring Boot server by executing the command: mvn spring-boot:run
3. Wait for the server to initialize on your local machine, then open a web browser and navigate to the endpoint: http://localhost:8080/run
4. The backend process will execute automatically. Monitor the terminal console to observe the iterative data fetching, the total aggregation logic, and the final submission status.

Ensure you update the registration number variable inside the Logic.java file to your actual assigned registration sequence before executing the code.

## Requirements Checklist

| Requirement | Status |
|---|---|
| Poll API exactly 10 times (index 0-9) | Done |
| 5-second delay between polls | Done |
| Deduplicate using roundId + participant | Done |
| Aggregate scores per participant | Done |
| Sort leaderboard by total score | Done |
| Submit leaderboard exactly once | Done |

## API Reference

The backend exposes a single, internal routing endpoint to manually trigger the sequence for evaluation.

### Initialization Endpoint

- **HTTP Method:** `GET`
- **Route:** `/run`
- **Controller Action:** Activates the application's core assignment logic. When routing to this endpoint, the backend will initiate the ten polling requests sequentially, enforce the mandatory five-second delay between each external validator request, dynamically tabulate the array results, and execute a post request of the final leaderboard payload to the `quiz/submit` gateway.
- **Expected Return:** Returns a plain text confirmation containing the raw stringified JSON response explicitly captured from the external validator system post-submission.
---

## Author

**Name:** Dodda Likhith Reddy  
**Registration No:** RA2311003020454  
**Institution:** SRM Institute of Science and Technology  
**Assignment:** Bajaj Finserv Health Java Qualifier, April 2026
