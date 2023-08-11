package pers.juumii.dto;

import lombok.Data;

@Data
public class IdPair {
    private String leftId;
    private String rightId;

    public static IdPair of(String leftId, String rightId) {
        IdPair res = new IdPair();
        res.setLeftId(leftId);
        res.setRightId(rightId);
        return res;
    }

    public static IdPair of(Long leftId, Long rightId){
        return of(leftId.toString(), rightId.toString());
    }
}
