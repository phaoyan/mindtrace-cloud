package pers.juumii.repo;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pers.juumii.data.Label;

public interface LabelRepository extends Neo4jRepository<Label, Long> { }
