package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.hub.MetadataDTO;

import java.time.LocalDateTime;

/**
 * 存储服务对提供的是url而不是直接的数据原文件，而url封装在这个metadata数据类中
 * 除了url外还存一些统计数据
 */
@Data
public class Metadata {

    @TableId
    private Long id;
    private Long userId;
    private String url;
    private String title;
    private String contentType;
    private LocalDateTime createTime;
    private Long visits;
    private Long likes;
    private Long pulls;
    @TableLogic
    private Boolean deleted;


    public static Metadata prototype(Long userId, String title, String url, String contentType) {
        Metadata res = new Metadata();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setUserId(userId);
        res.setUrl(url);
        res.setTitle(title);
        res.setContentType(contentType);
        res.setCreateTime(LocalDateTime.now());
        res.setVisits(0L);
        res.setLikes(0L);
        res.setPulls(0L);
        return res;
    }

    public static MetadataDTO transfer(Metadata meta) {
        MetadataDTO res = new MetadataDTO();
        res.setId(meta.getId().toString());
        res.setUserId(meta.getUserId().toString());
        res.setUrl(meta.getUrl());
        res.setTitle(meta.getTitle());
        res.setContentType(meta.getContentType());
        res.setVisits(meta.getVisits());
        res.setLikes(meta.getLikes());
        res.setPulls(meta.getPulls());
        return res;
    }
}
