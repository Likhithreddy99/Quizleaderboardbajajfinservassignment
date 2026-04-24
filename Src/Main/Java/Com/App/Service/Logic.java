package Com.App.Service;
import Com.App.Model.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;
@Service
public class Logic{
public String doLogic(){
String r="2024CS101"; 
System.out.println("fetching data now");
RestTemplate temp=new RestTemplate();
Set<String> set1=new HashSet<>();
Map<String,Integer> map=new HashMap<>();
for(int i=0;i<10;i++){
String u="https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/messages?regNo="+r+"&poll="+i;
try{
Item val=temp.getForObject(u,Item.class);
if(val!=null&&val.events!=null){
for(Item.Event x:val.events){
String str=x.roundId+"_"+x.participant; 
if(!set1.contains(str)){
set1.add(str);
int s=map.getOrDefault(x.participant,0);
map.put(x.participant,s+x.score);
}
}
}
Thread.sleep(5000); 
}catch(Exception e){
e.printStackTrace();
}
}
int total=0;
List<Map<String,Object>> list=new ArrayList<>();
for(Map.Entry<String,Integer> e:map.entrySet()){
Map<String,Object> m=new HashMap<>();
m.put("participant",e.getKey());
m.put("totalScore",e.getValue());
list.add(m);
total+=e.getValue(); 
}
list.sort((x,y)->((Integer)y.get("totalScore")).compareTo((Integer)x.get("totalScore")));
System.out.println("calc total="+total);
Map<String,Object> pay=new HashMap<>();
pay.put("regNo",r);
pay.put("leaderboard",list);
try{
HttpHeaders h=new HttpHeaders();
h.setContentType(MediaType.APPLICATION_JSON);
HttpEntity<Map<String,Object>> ent=new HttpEntity<>(pay,h);
String url2="https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/submit";
ResponseEntity<String> res=temp.postForEntity(url2,ent,String.class);
return res.getBody();
}catch(Exception z){
z.printStackTrace();
return "error submitting";
}
}
}
