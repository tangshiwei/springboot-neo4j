package com.neo4j.repository;

import com.neo4j.model.Lesson;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * 课程节点Repository
 */
public interface LessonRepository extends Neo4jRepository<Lesson, String> {
}
