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
    @Relationship(type = "POSSESS", direction = Relationship.Direction.OUTGOING)
    private Knode root;

    public static User prototype(Long id, Knode root){
        User res = new User();
        res.setId(id);
        res.setRoot(root);
        return res;
    }

}
