package pers.juumii.dto.mastery;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExamAnalysis {
    private ExamResultDTO result;
    private String analysis;
}
