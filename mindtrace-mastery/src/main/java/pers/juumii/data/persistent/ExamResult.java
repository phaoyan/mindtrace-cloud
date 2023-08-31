package pers.juumii.data.persistent;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObjectInputStream;
import lombok.Data;
import pers.juumii.config.COSConfig;
import pers.juumii.data.temp.Exam;
import pers.juumii.data.temp.ExamSession;
import pers.juumii.dto.mastery.ExamResultDTO;
import pers.juumii.utils.SpringUtils;
import pers.juumii.utils.TimeUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private String examStrategy;
    @TableLogic
    private Boolean deleted;

    public ExamSession toSession(){
        ExamSession res = new ExamSession();
        res.setId(getId());
        res.setStartTime(getStartTime());
        res.setEndTime(getEndTime());
        Exam exam = new Exam();
        exam.setId(getId());
        exam.setRootId(getRootId());
        exam.setUserId(getUserId());
        exam.setExamStrategy(getExamStrategy());
        res.setExam(exam);
        try{
            COSClient cosClient = SpringUtils.getBean(COSClient.class);
            COSConfig cosConfig = SpringUtils.getBean(COSConfig.class);
            String bucket = cosConfig.getExamResultBucketName();
            String key = "exam/result/cache/" + id.toString();
            COSObjectInputStream stream = cosClient.getObject(bucket, key).getObjectContent();
            String str = StrUtil.str(stream.readAllBytes(), StandardCharsets.UTF_8);
            res.setCache(str);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static ExamResultDTO transfer(ExamResult result) {
        ExamResultDTO res = new ExamResultDTO();
        res.setId(result.getId().toString());
        res.setUserId(result.getUserId().toString());
        res.setRootId(result.getRootId().toString());
        res.setStartTime(TimeUtils.format(result.getStartTime()));
        res.setEndTime(TimeUtils.format(result.getEndTime()));
        res.setExamStrategy(result.getExamStrategy());
        return res;
    }

    public static List<ExamResultDTO> transfer(List<ExamResult> examResults){
        return examResults.stream().map(ExamResult::transfer).toList();
    }
}
