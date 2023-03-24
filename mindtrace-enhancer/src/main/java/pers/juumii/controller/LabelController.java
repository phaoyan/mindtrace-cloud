package pers.juumii.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.dto.LabelDTO;
import pers.juumii.service.LabelService;
import pers.juumii.utils.SaResult;

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
    public SaResult queryAll(){
        return labelService.queryAll();
    }

    @PutMapping
    public SaResult create(@RequestBody LabelDTO dto){
        return labelService.create(dto);
    }

    @PostMapping
    public SaResult update(
            @RequestParam("name") String name,
            @RequestBody LabelDTO dto){
        return labelService.update(name, dto);
    }

    @DeleteMapping
    public SaResult delete(@RequestParam("name") String name){
        return labelService.delete(name);
    }

}