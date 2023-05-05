package pers.juumii.dto.share;

import lombok.Data;

@Data
public class UserShareDTO {
    private String id;
    private String userId;
    private Long visits;
    private Long likes;
    private Long favorites;
}
