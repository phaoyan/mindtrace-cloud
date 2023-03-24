package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.LabelDTO;
import pers.juumii.dto.ResourceDTO;

import java.time.LocalDateTime;

@Data
public class Label {

    @TableId
    private String name;
    @TableLogic
    private Boolean deleted;
    private LocalDateTime createTime;
    private Long createBy;

    public static Label prototype(LabelDTO labelDTO) {
        Label res = new Label();
        res.setName(labelDTO.getName());
        res.setCreateBy(labelDTO.getCreateBy());
        res.setCreateTime(LocalDateTime.now());
        res.setDeleted(false);
        return res;
    }
}
