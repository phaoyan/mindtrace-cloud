package pers.juumii.data.temp;

import cn.hutool.core.util.IdUtil;
import lombok.Data;
import pers.juumii.dto.mastery.QuizResultDTO;

import java.time.Duration;
import java.util.List;

@Data
public class QuizResult {
    private Long id;
    private Long quizId;
    private Long knodeId;
    private Duration duration;
    private Double completion;
    private String message;

    public static QuizResult prototype(Long knodeId, Long quizId, Duration duration, Double completion){
        QuizResult res = new QuizResult();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setQuizId(quizId);
        res.setKnodeId(knodeId);
        res.setDuration(duration);
        res.setCompletion(completion);
        return res;
    }

    public static List<QuizResult> prototype(Long knodeId, List<Long> quizIds, Duration duration, Double completion){
        return quizIds.stream().map(quizId->prototype(knodeId, quizId, duration, completion)).toList();
    }

    public static QuizResultDTO transfer(QuizResult quizResult){
        QuizResultDTO res = new QuizResultDTO();
        res.setId(quizResult.getId().toString());
        res.setQuizId(quizResult.getQuizId().toString());
        res.setKnodeId(quizResult.getKnodeId().toString());
        res.setDuration(quizResult.getDuration().getSeconds());
        res.setCompletion(quizResult.getCompletion());
        res.setMessage(quizResult.getMessage());
        return res;
    }

}
