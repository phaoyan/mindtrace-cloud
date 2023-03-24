package pers.juumii.data;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Data
@Node
public class User {

    @Id
    private Long id;
    @Property("name")
    private String name;
    @Relationship(type = "POSSESS", direction = Relationship.Direction.OUTGOING)
    private List<Knode> roots;

    public static User prototype(Long id, String name){
        User res = new User();
        res.setId(id);
        res.setName(name);
        res.setRoots(new ArrayList<>());
        return res;
    }

}
