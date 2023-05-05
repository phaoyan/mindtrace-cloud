package pers.juumii.service.impl.v2;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.service.UserService;
import pers.juumii.service.impl.v2.utils.Cypher;
import pers.juumii.service.impl.v2.utils.Neo4jUtils;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImplV2 implements UserService {

    private final Neo4jUtils neo4j;

    @Autowired
    public UserServiceImplV2(Neo4jUtils neo4j) {
        this.neo4j = neo4j;
    }

    @Override
    public SaResult register(Long userId) {
        Knode root = Knode.prototype("ROOT", null, userId);
        root.setIndex(0);
        Cypher registerUser = Cypher.cypher("""
                CREATE (user: User {id: $userId})
                """, Map.of("userId", userId));
        Cypher createRoot = KnodeServiceImplV2.createBasic(root);
        Cypher connect = Cypher.cypher("""
                MATCH (user: User {id:$userId}), (root: Knode {id:$knodeId})
                CREATE (user)-[p:POSSESS]->(root)
                """,Map.of("userId", userId, "knodeId", root.getId()));
        neo4j.transaction(List.of(registerUser, createRoot, connect));
        return SaResult.ok();
    }

    @Override
    public SaResult unregister(Long userId) {
        return null;
    }

    @Override
    public Long checkRootId(Long userId) {
        return null;
    }
}
