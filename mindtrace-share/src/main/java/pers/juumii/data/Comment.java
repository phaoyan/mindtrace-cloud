package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论：用户可以对一个共享的资源进行评论
 */
@Data
public class Comment {
    @TableId
    private Long id;
    // 支持楼中楼
    private Long stem;
    private Long userId;
    private LocalDateTime createTime;
    private String content;
    private Long likes;
    @TableLogic
    private Boolean deleted;
}
