package pers.juumii.service.impl.v2.utils;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class Neo4jUtils {

    private final Driver driver;

    @Autowired
    public Neo4jUtils(Driver driver) {
        this.driver = driver;
    }

    public <T> List<T> session(Cypher cypher, Function<Record, T> recordResolver){
        try(Session session = driver.session()){
            List<T> res = new ArrayList<>();
            Result result = session.run(cypher.getCypher(), cypher.getParams());
            for(Record record: result.list())
                res.add(recordResolver.apply(record));
            return res;
        }
    }

    // 事务操作一般无返回值，为了方便这里不给返回值
    public void transaction(List<Cypher> cyphers){
        try(Session session = driver.session()){
            try(Transaction tx = session.beginTransaction()){
                for(Cypher cypher: cyphers)
                    tx.run(cypher.getCypher(), cypher.getParams());
                tx.commit();
            }
        }
    }

}
