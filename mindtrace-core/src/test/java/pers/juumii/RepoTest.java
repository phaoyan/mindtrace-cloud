package pers.juumii;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import pers.juumii.data.Knode;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class RepoTest {

    @Autowired
    private Neo4jTemplate template;
    @Autowired
    private Neo4jClient client;

    @Test
    void test(){
        String cypher = """
                        MATCH (user:User)-[:POSSESS]->(root)-[:BRANCH_TO]->(knode) WHERE user.id=$userId AND knode.title=$title
                        OPTIONAL MATCH (knode)-[br:BRANCH_TO]->(branch)
                        OPTIONAL MATCH (knode)-[st:STEM_FROM]->(stem)
                        OPTIONAL MATCH (knode)-[con:CONNECT_TO]->(connection)
                        RETURN  knode AS knode,
                                collect(branch) AS branches,
                                collect(stem) AS stems,
                                collect(connection) AS connections
                        """;
        Map<String, Object> params = Map.of("userId", 0, "title", "微积分");
        Collection<Knode> all = client.query(cypher).bindAll(params).fetchAs(Knode.class).mappedBy(((typeSystem, record) -> {
            System.out.println(record);
            Entity stem = record.get("stems").asList(Value::asEntity).get(0);
            List<Entity> branches = record.get("branches").asList(Value::asEntity);
            List<Entity> connections = record.get("connections").asList(Value::asEntity);

            return new Knode();
        })).all();
    }
}
