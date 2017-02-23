package testControllers;


import mvc.framework.annotations.controllers.Controller;
import mvc.framework.annotations.parameters.PathVariable;
import mvc.framework.annotations.parameters.RequestParam;
import mvc.framework.annotations.requests.GetMapping;
import mvc.framework.annotations.requests.PostMapping;
import mvc.framework.models.Model;

@Controller
public class BeerController {

    @GetMapping("/beer/{id}")
    public String getBeerId(@PathVariable("id") Integer id){
        System.out.println(id);
        return "beer";
    }

    @GetMapping("/beer")
    public String getBeer(){
        return "beer";
    }

    @PostMapping("/beer")
    public String getBeerResults(@RequestParam("username") String username, @RequestParam("password") long password){
        System.out.println(username);
        System.out.println(password);
        return "redirect:/zagorka";
    }

    @GetMapping("/zagorka")
    public String getZagorka(Model model){
        model.addAttribute("beer", "I am a model beer");
        return "zagorka";
    }
}
