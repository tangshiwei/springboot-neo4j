package com.neo4j.repository;

import com.neo4j.model.Classes;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

/**
 * 班级节点Repository
 */
public interface ClassRepository extends Neo4jRepository<Classes, String> {
    /**
     * 根据班级名称查询班级信息
     *
     * @param name 班级名称
     * @return 班级信息
     */
    Optional<Classes> findByName(String name);
}
