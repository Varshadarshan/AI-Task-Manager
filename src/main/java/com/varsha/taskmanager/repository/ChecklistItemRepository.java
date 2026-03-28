package com.varsha.taskmanager.repository;

import com.varsha.taskmanager.model.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Integer> {

    // ✅ Get all checklist items for a task ordered by day
    List<ChecklistItem> findByTaskIdOrderByDayNumberAsc(Integer taskId);

    // ✅ Count total items for a task
    long countByTaskId(Integer taskId);

    // ✅ Count completed items for a task
    long countByTaskIdAndIsDoneTrue(Integer taskId);

    // ✅ Delete all checklist items for a task
    @Modifying
    @Transactional
    @Query("DELETE FROM ChecklistItem c WHERE c.task.id = :taskId")
    void deleteByTaskId(Integer taskId);
}