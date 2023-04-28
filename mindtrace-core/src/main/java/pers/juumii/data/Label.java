package pers.juumii.data;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.Map;

@Data
@Node
public class Label {

    @Id
    private Long id;
    @Property("name")
    private String name;
    @Property("deleted")
    private Boolean deleted;

    // Label的继承机制：待定
    @Relationship(type = "PARENT")
    private Label parent;
    @Relationship(type = "CHILD")
    private List<Label> children;

    public static Label prototype(String name){
        Label res = new Label();
        res.setId(IdUtil.getSnowflakeNextId());
        res.setName(name);
        res.setDeleted(false);
        return res;
    }

    public static Label prototype(Map<String, Object> params) {
        Label res = new Label();
        res.setId(Convert.toLong(params.get("id")));
        res.setName(Convert.toStr(params.get("name")));
        res.setDeleted(Convert.toBool(params.get("deleted")));
        return res;
    }


}
