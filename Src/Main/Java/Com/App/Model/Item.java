package Com.App.Model;
import java.util.List;
public class Item{
public String regNo;
public String setId;
public int pollIndex;
public List<Event> events;
public static class Event{
public String roundId;
public String participant;
public int score;
}
}
