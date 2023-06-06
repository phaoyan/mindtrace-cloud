package pers.juumii.data.persistent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.data.temp.Exam;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.dto.mastery.ExamResultDTO;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamResult {

    @TableId
    private Long id;
    private Long rootId;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @TableField(exist = false)
    private List<ExamInteract> interacts;
    private String examStrategy;
    private String cache;
    @TableLogic
    private Boolean deleted;

    public static ExamResultDTO transfer(ExamResult result) {
        ExamResultDTO res = new ExamResultDTO();
        res.setId(result.getId().toString());
        res.setUserId(result.getUserId().toString());
        res.setRootId(result.getRootId().toString());
        res.setStartTime(TimeUtils.format(result.getStartTime()));
        res.setEndTime(TimeUtils.format(result.getEndTime()));
        res.setExamStrategy(result.getExamStrategy());
        res.setInteracts(ExamInteract.transfer(result.getInteracts()));
        return res;
    }

    public static List<ExamResultDTO> transfer(List<ExamResult> examResults){
        return examResults.stream().map(ExamResult::transfer).toList();
    }

    public static ExamSession toSession(ExamResult examResult) {
        ExamSession res = new ExamSession();
        res.setId(examResult.getId());
        res.setStartTime(examResult.getStartTime());
        res.setEndTime(examResult.getEndTime());
        res.setInteracts(examResult.getInteracts());
        Exam exam = new Exam();
        exam.setId(examResult.getId());
        exam.setRootId(examResult.getRootId());
        exam.setUserId(examResult.getUserId());
        exam.setExamStrategy(examResult.getExamStrategy());
        res.setExam(exam);
        res.setCache(examResult.getCache());
        return res;
    }
}
