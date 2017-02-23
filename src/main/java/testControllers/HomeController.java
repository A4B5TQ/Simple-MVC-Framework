package testControllers;

import mvc.framework.annotations.controllers.Controller;
import mvc.framework.annotations.requests.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home/{1}/edit")
    public String getHomePage(){
        return "home";
    }
}
