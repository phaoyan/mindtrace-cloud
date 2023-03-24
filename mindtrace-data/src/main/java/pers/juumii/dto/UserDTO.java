package pers.juumii.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    private Long id;
    private List<Long> rootIds;    // knode root id

}
