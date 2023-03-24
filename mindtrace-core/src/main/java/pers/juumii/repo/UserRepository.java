package pers.juumii.repo;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import pers.juumii.data.User;

public interface UserRepository extends Neo4jRepository<User, Long> {

    @Query(   "MATCH (user:User)-[r:POSSESS]->(branch:Knode) "
            + "WHERE user.id = $userId AND branch.id = $branchId "
            + "DELETE r")
    void dropBranch(@Param("userId") Long userId, @Param("branchId") Long branchId);
}
