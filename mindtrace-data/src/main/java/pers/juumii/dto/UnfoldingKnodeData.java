package pers.juumii.dto;

import cn.hutool.core.convert.Convert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.juumii.feign.CoreClient;
import pers.juumii.utils.SpringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnfoldingKnodeData {
    private String knodeId;
    private String stemId;
    private String title;
    private List<String> chainStyleTitle;
    private Boolean unfolded;
    private Boolean tag;


    public static UnfoldingKnodeData transfer(KnodeDTO knode) {
        List<String> chainStyleTitle =
                SpringUtils.getBean(CoreClient.class).chainStyleTitle(Convert.toLong(knode.getId()));
        return new UnfoldingKnodeData(
                knode.getId(), knode.getStemId(),
                knode.getTitle(), chainStyleTitle,
                false, false);
    }
    public static List<UnfoldingKnodeData> transfer(List<KnodeDTO> knodes) {
        return knodes.stream().map(UnfoldingKnodeData::transfer).toList();
    }

}
