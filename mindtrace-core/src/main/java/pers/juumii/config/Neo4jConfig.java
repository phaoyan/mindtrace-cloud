package pers.juumii.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {

    @Value("${spring.neo4j.uri}")
    private String NEO4J_URI;
    @Value("${spring.neo4j.authentication.username}")
    private String USERNAME;
    @Value("${spring.neo4j.authentication.password}")
    private String PASSWORD;

    @Bean
    public Driver neo4jDriver(){
        return GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(USERNAME, PASSWORD));
    }

}
