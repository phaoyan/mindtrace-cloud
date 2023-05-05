package pers.juumii.dto.share;

import lombok.Data;
import pers.juumii.dto.KnodeDTO;

@Data
public class KnodeShareDTO {
    private String id;
    private String knodeId;
    private String userId;
    // 资源质量评分
    private Double rate;
    // 访问次数
    private Long visits;
    // 点赞次数
    private Long likes;
    // 收藏次数
    private Long favorites;
    private KnodeDTO knode;
}
