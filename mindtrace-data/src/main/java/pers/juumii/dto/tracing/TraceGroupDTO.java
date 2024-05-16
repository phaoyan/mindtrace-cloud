package pers.juumii.dto.tracing;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TraceGroupDTO {

    private String id;
    private String userId;
    private String title;
    private String createTime;


}
