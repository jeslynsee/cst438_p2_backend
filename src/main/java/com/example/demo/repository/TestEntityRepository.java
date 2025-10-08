package com.example.demo.repository;

import com.example.demo.model.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository gives you CRUD operations automatically
public interface TestEntityRepository extends JpaRepository<TestEntity, Long> {
}
