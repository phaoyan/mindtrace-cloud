package pers.juumii.repo;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.data.User;

@Service
public interface KnodeRepository extends Neo4jRepository<Knode, Long> {

    Knode findByTitle(String title);

    @Query(  "MATCH (knode: Knode)-[tag: TAG]->(label: Label) "
           + "WHERE knode.id = $knodeId AND label.name = $labelName "
           + "DELETE tag")
    void unlabel(@Param("knodeId") Long knodeId, @Param("labelName") String labelName);

    @Query(  "MATCH (branch:Knode)-[r1:STEM_FROM]->(ori:Knode) "
            +"MATCH (ori:Knode)-[r2:BRANCH_TO]->(branch:Knode) "
            +"MATCH (tar:Knode) "
            +"WHERE branch.id = $branchId AND tar.id = $stemId "
            +"DELETE r1, r2 "
            +"MERGE (tar)-[:BRANCH_TO]->(branch) "
            +"MERGE (branch)-[:STEM_FROM]->(tar)")
    void shift(@Param("stemId") Long stemId, @Param("branchId") Long branchId);

    //TODO: 查找任意Knode的User
    @Query("")
    User findUser(@Param("id") Long id);
}
