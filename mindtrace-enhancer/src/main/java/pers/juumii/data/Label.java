package pers.juumii.data;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Label {

    @TableId
    private String name;
    @TableLogic
    private Boolean deleted;
    private LocalDateTime createTime;
    private Long createBy;


}
