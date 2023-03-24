package pers.juumii.data;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import pers.juumii.dto.LabelDTO;

@Data
@Node
public class Label {

    @Id
    @Property("name")
    private String name;
    @Property("deleted")
    private Boolean deleted;

    public static Label prototype(String name){
        Label res = new Label();
        res.setName(name);
        res.setDeleted(false);
        return res;
    }

    public static LabelDTO transfer(Label Label){
        LabelDTO res = new LabelDTO();
        res.setName(Label.getName());
        res.setDeleted(Label.getDeleted());
        return res;
    }

}
