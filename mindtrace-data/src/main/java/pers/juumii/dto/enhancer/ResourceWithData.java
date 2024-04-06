package pers.juumii.dto.enhancer;

import lombok.Data;

import java.util.Map;

@Data
public class ResourceWithData {

    private ResourceDTO meta;
    private Map<String, byte[]> data;
}
