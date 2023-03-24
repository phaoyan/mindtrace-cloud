package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.ResourceDTO;

import java.time.LocalDateTime;

@Data
public class Resource {

    @TableId
    private Long id;
    private String url;
    // Resource的type用于资源处理
    private String type;
    private LocalDateTime createTime;
    private Long createBy;
    private String privacy;
    @TableLogic
    private Boolean deleted;

    public static Resource prototype(ResourceDTO resourceDTO) {
        Resource res = new Resource();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setDeleted(false);
        res.setCreateBy(resourceDTO.getCreateBy());
        res.setCreateTime(resourceDTO.getCreateTime());
        res.setType(resourceDTO.getType());
        res.setPrivacy(resourceDTO.getPrivacy());
        res.setUrl(resourceDTO.getUrl());
        return res;
    }
}
