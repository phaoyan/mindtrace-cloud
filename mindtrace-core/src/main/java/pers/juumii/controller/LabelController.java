package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Label;
import pers.juumii.dto.LabelDTO;
import pers.juumii.service.LabelService;
import pers.juumii.utils.SaResult;

import java.util.List;

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
    public List<Label> checkAll(){
        return labelService.checkAll();
    }

    @PutMapping
    public SaResult create(@RequestParam("name") String name){
        return labelService.create(name);
    }

    @PostMapping
    public SaResult update(
            @RequestParam("name") String name,
            @RequestBody LabelDTO dto){
        return labelService.update(name, dto);
    }

    @DeleteMapping
    public SaResult remove(@RequestParam("name") String name){
        return labelService.remove(name);
    }

}
