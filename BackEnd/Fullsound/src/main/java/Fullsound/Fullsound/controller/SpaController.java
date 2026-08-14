package Fullsound.Fullsound.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class SpaController {
    @GetMapping(value = {"/{path:[^\\.]*}", "/{path1}/{path2:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}
