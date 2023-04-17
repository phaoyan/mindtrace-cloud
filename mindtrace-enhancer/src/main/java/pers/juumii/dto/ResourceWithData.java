package pers.juumii.dto;

import lombok.Data;
import pers.juumii.data.Resource;

import java.util.Map;

@Data
public class ResourceWithData {

    private Resource meta;
    private Map<String, Object> data;
}
