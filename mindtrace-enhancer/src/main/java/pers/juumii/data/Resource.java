package pers.juumii.data;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import pers.juumii.dto.enhancer.ResourceDTO;
import pers.juumii.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Resource是综合性的资源对象，其不只由一个文件组成
 * 一个Resource以文件夹的方式存储
 * Resource文件夹的名称就是这个Resource对象的id
 * Resource文件夹内部的文件有各自的id
 *
 */

@Data
public class Resource {

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String title;
    // Resource的type用于资源处理，写对应resolver的类名，如QuizcardResolver.class.getSimpleName()
    private String type;
    private LocalDateTime createTime;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createBy;

    public static Resource prototype(ResourceDTO dto){
        Resource res = new Resource();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setTitle(dto.getTitle());
        res.setCreateBy(Convert.toLong(dto.getCreateBy()));
        res.setCreateTime(LocalDateTime.now());
        res.setType(dto.getType());
        return res;
    }

    public static Resource prototype(Long userId){
        Resource res = new Resource();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setCreateBy(userId);
        res.setCreateTime(LocalDateTime.now());
        return res;
    }

    public static ResourceDTO transfer(Resource resource){
        if(resource == null) return null;

        ResourceDTO res = new ResourceDTO();
        if(resource.getId() != null) res.setId(resource.getId().toString());
        if(resource.getCreateBy() != null) res.setCreateBy(resource.getCreateBy().toString());
        if(resource.getCreateTime() != null) res.setCreateTime(TimeUtils.format(resource.getCreateTime()));
        if(resource.getTitle() != null) res.setTitle(resource.getTitle());
        if(resource.getType() != null) res.setType(resource.getType());
        return res;
    }

    public static List<ResourceDTO> transfer(List<Resource> resources){
        return resources.stream().map(Resource::transfer).toList();
    }
}
