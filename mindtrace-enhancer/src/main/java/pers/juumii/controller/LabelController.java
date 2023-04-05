package pers.juumii.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Label;
import pers.juumii.service.LabelService;

@RestController
@RequestMapping("/label")
public class LabelController {

    private final LabelService labelService;

    @Autowired
    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    //返回所有注册的标签
    @GetMapping
    public Object queryAll(){
        return labelService.queryAll();
    }

    @PutMapping
    public Object create(@RequestBody Label label){
        return labelService.create(label.getName(), label);
    }

    @PostMapping
    public Object update(
            @RequestParam("name") String name,
            @RequestBody Label label){
        return labelService.update(name, label);
    }

    @DeleteMapping
    public Object delete(@RequestParam("name") String name){
        return labelService.delete(name);
    }

}