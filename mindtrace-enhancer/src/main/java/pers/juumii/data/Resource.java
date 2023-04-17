package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import pers.juumii.utils.Constants;

import java.time.LocalDateTime;

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
    private String privacy;

    public static Resource prototype(Resource meta) {
        Resource res = new Resource();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setTitle(meta.getTitle());
        res.setCreateBy(meta.getCreateBy());
        res.setCreateTime(LocalDateTime.now());
        res.setType(meta.getType());
        res.setPrivacy(Constants.PRIVACY_PRIVATE);
        return res;
    }

    public static Resource prototype(String type, Long createBy){
        Resource resource = new Resource();
        resource.setType(type);
        resource.setCreateBy(createBy);
        return prototype(resource);
    }
}
