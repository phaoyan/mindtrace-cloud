package pers.juumii.data.temp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.juumii.dto.mastery.ExamDTO;

/**
 * Exam 是即时生成的数据，不需要持久化到mysql，可以缓存到redis
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exam {
    private Long id;
    private Long userId;
    // root knode id
    private Long rootId;
    /**
     * json字符串，大致的规则为：
     * {
     *     type: xxx,
     *     config: { ... }
     * }
     * 以实际的解析方式为准
     */
    private String examStrategy;

    public static ExamDTO transfer(Exam exam) {
        ExamDTO res = new ExamDTO();
        res.setId(exam.getId().toString());
        res.setUserId(exam.getUserId().toString());
        res.setRootId(exam.getRootId().toString());
        res.setExamStrategy(exam.getExamStrategy());
        return res;
    }
}
