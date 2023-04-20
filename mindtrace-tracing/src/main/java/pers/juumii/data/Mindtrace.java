package pers.juumii.data;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pers.juumii.dto.MindtraceDTO;

import java.time.LocalDateTime;

@Data
@TableName("mindtrace")
public class Mindtrace {

    @TableId
    private Long id;
    private Long enhancerId;
    private Long knodeId;
    private Long createBy;
    private Double retentionAfter;
    private Double retentionBefore;
    private Integer reviewLayer = 1;
    private LocalDateTime createTime;
    private String remark;
    @TableLogic
    private Boolean deleted;

    public static Mindtrace prototype(
            Long enhancerId,
            Long knodeId,
            Long userId,
            Double RA,
            Double RB,
            String remark){
        Mindtrace res = new Mindtrace();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setEnhancerId(enhancerId);
        res.setKnodeId(knodeId);
        res.setCreateBy(userId);
        res.setCreateTime(LocalDateTime.now());
        res.setRetentionAfter(RA);
        res.setRetentionBefore(RB);
        res.setRemark(remark);
        res.setDeleted(false);
        return res;
    }

    public static Mindtrace prototype(MindtraceDTO dto){
        return prototype(
                Convert.toLong(dto.getEnhancerId()),
                Convert.toLong(dto.getKnodeId()),
                Convert.toLong(dto.getCreateBy()),
                dto.getRetentionAfter(),
                dto.getRetentionBefore(),
                dto.getRemark());
    }

}