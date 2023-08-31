package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.service.ReviewService;

import java.util.List;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PutMapping("/review/schedule")
    public void ackReview(
            @RequestParam Long knodeId,
            @RequestParam Long next){
        reviewService.ackReview(knodeId, next);
    }

    @DeleteMapping("/review/schedule")
    public void removeReviewSchedule(
            @RequestParam Long knodeId,
            @RequestParam String date){
        reviewService.removeReviewSchedule(knodeId, date);
    }

    @GetMapping("/review")
    public List<String> getReviewKnodeIds(
            @RequestParam Long rootId,
            @RequestParam String date){
        return reviewService.getReviewKnodeIds(rootId, date);
    }

    @GetMapping("/review/monitor")
    public Boolean isKnodeMonitored(@RequestParam Long knodeId){
        return reviewService.isKnodeMonitored(knodeId);
    }

    @PutMapping("/review")
    public void startReviewMonitor(@RequestParam Long rootId){
        reviewService.startReviewMonitor(rootId);
    }

    @DeleteMapping("/review")
    public void finishReviewMonitor(@RequestParam Long rootId){
        reviewService.finishReviewMonitor(rootId);
    }

}
