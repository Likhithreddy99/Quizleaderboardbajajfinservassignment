package Com.App.Controller;
import Com.App.Service.Logic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class Api{
@Autowired
Logic myLogic;
@GetMapping("/run")
public String go(){
return myLogic.doLogic();
}
}
