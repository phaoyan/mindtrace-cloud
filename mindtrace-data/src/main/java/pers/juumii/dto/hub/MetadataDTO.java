package pers.juumii.dto.hub;

import lombok.Data;

@Data
public class MetadataDTO {

    private String id;
    private String userId;
    private String url;
    private String title;
    private String contentType;
    private String createTime;
    private Long visits;
    private Long likes;
    private Long pulls;

}
