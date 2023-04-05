package pers.juumii.service.impl.serializer;

import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceSerializer;

import java.util.Map;

@Service
@ResourceType(ResourceType.MARKDOWN)
public class MarkdownSerializer implements ResourceSerializer {
    @Override
    public void serialize(Resource meta, Map<String, Object> data) {

    }
}
