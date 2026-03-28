package com.varsha.taskmanager.repository;

import com.varsha.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByUserId(Integer userId);
    List<Task> findByUserUsername(String username);                    // ✅
    Page<Task> findByUserUsername(String username, Pageable pageable); // ✅
}