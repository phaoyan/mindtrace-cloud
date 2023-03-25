package pers.juumii.repo;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import pers.juumii.data.User;

public interface UserRepository extends Neo4jRepository<User, Long> {

    @Query("MATCH(user:User)-[:POSSESS]->(root:Knode) WHERE user.id=$userId RETURN root.id")
    Long checkRootId(@Param("userId") Long userId);


    // 仅从root查找user
    @Query( "MATCH (user:User)-[:POSSESS]->(root:Knode) WHERE root.id=$knodeId RETURN user.id")
    Long findUserId(@Param("knodeId") Long knodeId);
}
