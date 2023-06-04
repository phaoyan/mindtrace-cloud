package pers.juumii.dto.mastery;


import lombok.Data;

@Data
public class QuizResultDTO {
    private String id;
    private String quizId;
    private String knodeId;
    private Long duration;
    private Double completion;
    private String message;
}
