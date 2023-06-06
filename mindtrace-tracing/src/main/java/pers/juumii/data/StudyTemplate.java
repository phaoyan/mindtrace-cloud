package pers.juumii.data;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.StudyTemplateDTO;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StudyTemplate {

    @TableId
    private Long id;
    private Long userId;
    private String description;
    private LocalDateTime createTime;
    @TableLogic
    private Boolean deleted;

    public static StudyTemplate prototype(StudyTemplateDTO data) {
        StudyTemplate res = new StudyTemplate();
        res.setId(data.getId() == null ? IdUtil.getSnowflakeNextId() : Convert.toLong(data.getId()));
        if(data.getUserId() != null) res.setUserId(Convert.toLong(data.getUserId()));
        if(data.getDescription() != null) res.setDescription(data.getDescription());
        if(data.getCreateTime() != null) res.setCreateTime(TimeUtils.parse(data.getCreateTime()));
        res.setDeleted(false);
        return res;
    }

    public static StudyTemplateDTO transfer(StudyTemplate template) {
        StudyTemplateDTO res = new StudyTemplateDTO();
        res.setId(template.getId().toString());
        res.setUserId(template.getUserId().toString());
        res.setDescription(template.getDescription());
        res.setCreateTime(TimeUtils.format(template.getCreateTime()));
        return res;
    }

    public static List<StudyTemplateDTO> transfer(List<StudyTemplate> templates){
        return templates.stream().map(StudyTemplate::transfer).toList();
    }
}
