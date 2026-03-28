package com.varsha.taskmanager.controller;

import com.varsha.taskmanager.dto.TaskProgressDTO;
import com.varsha.taskmanager.dto.TaskRequestDTO;
import com.varsha.taskmanager.dto.TaskResponseDTO;
import com.varsha.taskmanager.model.Task;
import com.varsha.taskmanager.service.TaskService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // ✅ CREATE TASK — auto generates AI learning plan if deadline provided
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskRequestDTO request) {
        TaskResponseDTO response = taskService.saveTask(request);
        return ResponseEntity.ok(response);
    }

    // ✅ GET ALL TASKS
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasksAsDTO());
    }

    // ✅ GET ALL TASKS WITH PAGINATION
    @GetMapping("/paged")
    public ResponseEntity<Page<TaskResponseDTO>> getAllTasksPaged(Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTasksPagedAsDTO(pageable));
    }

    // ✅ GET TASK BY ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Integer id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(taskService.convertToDTO(task));
    }

    // ✅ UPDATE TASK
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Integer id,
                                                      @RequestBody TaskRequestDTO request) {
        return ResponseEntity.ok(taskService.updateTaskAsDTO(id, request));
    }

    // ✅ DELETE TASK
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted successfully");
    }

    // ✅ AI: Get full progress analytics + checklist for a task
    @GetMapping("/{id}/progress")
    public ResponseEntity<TaskProgressDTO> getTaskProgress(@PathVariable Integer id) {
        return ResponseEntity.ok(taskService.getTaskProgress(id));
    }

    // ✅ AI: Mark a checklist day as done or undone
    // isDone = true → mark complete, isDone = false → mark incomplete
    @PatchMapping("/{taskId}/checklist/{checklistItemId}")
    public ResponseEntity<String> markDayDone(
            @PathVariable Integer taskId,
            @PathVariable Integer checklistItemId,
            @RequestParam boolean isDone) {
        return ResponseEntity.ok(taskService.markDayDone(taskId, checklistItemId, isDone));
    }
}