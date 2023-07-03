package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.temp.Exam;
import pers.juumii.data.temp.ExamInteract;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.dto.mastery.ExamAnalysis;
import pers.juumii.dto.mastery.ExamInteractDTO;
import pers.juumii.dto.mastery.ExamSessionDTO;
import pers.juumii.service.SessionService;

import java.util.List;

@RestController
public class ExamSessionController {

    private final SessionService sessionService;

    @Autowired
    public ExamSessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/session/{sessionId}")
    public ExamInteractDTO interact(
            @PathVariable Long sessionId,
            @RequestBody ExamInteractDTO dto){
        return ExamInteract.transfer(sessionService.interact(sessionId, ExamInteract.fromDTO(dto)));
    }

    @GetMapping("/session/{sessionId}")
    public ExamSessionDTO getSession(@PathVariable Long sessionId){
        return ExamSession.transfer(sessionService.getSession(sessionId));
    }

    @GetMapping("/session")
    public List<ExamSessionDTO> getCurrentSession(@RequestParam(value = "userId", required = false) Long userId){
        return ExamSession.transfer(sessionService.getCurrentSession(userId));
    }

    @PostMapping("/session")
    public ExamSessionDTO start(@RequestBody Exam exam){
        return ExamSession.transfer(sessionService.start(exam));
    }

    @PostMapping("/session/{sessionId}/finish")
    public ExamAnalysis finish(@PathVariable Long sessionId){
        return sessionService.finish(sessionId);
    }

    @DeleteMapping("/session/{sessionId}")
    public void interrupt(@PathVariable Long sessionId){
        sessionService.interrupt(sessionId);
    }
}