package com.neo4j.repository;

import com.neo4j.model.Teacher;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * 教师节点Repository
 */
public interface TeacherRepository extends Neo4jRepository<Teacher, String> {
}
