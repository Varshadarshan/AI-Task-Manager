package com.varsha.taskmanager.service;

import com.varsha.taskmanager.dto.TaskProgressDTO;
import com.varsha.taskmanager.dto.TaskRequestDTO;
import com.varsha.taskmanager.dto.TaskResponseDTO;
import com.varsha.taskmanager.exception.TaskNotFoundException;
import com.varsha.taskmanager.model.ChecklistItem;
import com.varsha.taskmanager.model.Task;
import com.varsha.taskmanager.model.TaskStatus;
import com.varsha.taskmanager.model.User;
import com.varsha.taskmanager.repository.ChecklistItemRepository;
import com.varsha.taskmanager.repository.TaskRepository;
import com.varsha.taskmanager.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChecklistItemRepository checklistItemRepository;

    @Autowired
    private AiService aiService;

    // ✅ CREATE TASK + auto generate AI learning plan
    public TaskResponseDTO saveTask(TaskRequestDTO request) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.PENDING);
        task.setDeadline(request.getDeadline());
        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        // ✅ Auto-generate AI learning plan if deadline is provided
        if (request.getDeadline() != null) {
            generateAndSaveAiPlan(savedTask);
        }

        return mapToResponse(savedTask);
    }

    // ✅ AI: Generate day-wise plan and save as checklist items
    private void generateAndSaveAiPlan(Task task) {
        List<String> topics = aiService.generateLearningPlan(
                task.getTitle(),
                task.getDescription(),
                task.getDeadline()
        );

        // Delete old checklist if exists
        checklistItemRepository.deleteByTaskId(task.getId());

        // Save each topic as a checklist item
        for (int i = 0; i < topics.size(); i++) {
            ChecklistItem item = new ChecklistItem(i + 1, topics.get(i), task);
            checklistItemRepository.save(item);
        }
    }

    // ✅ AI: Mark a specific day as done / undone
    public String markDayDone(Integer taskId, Integer checklistItemId, boolean isDone) {

        // Verify task exists
        taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + taskId));

        ChecklistItem item = checklistItemRepository.findById(checklistItemId)
                .orElseThrow(() -> new RuntimeException("Checklist item not found with id " + checklistItemId));

        item.setDone(isDone);
        checklistItemRepository.save(item);

        return isDone
                ? "✅ Day " + item.getDayNumber() + " marked as completed!"
                : "🔄 Day " + item.getDayNumber() + " marked as incomplete.";
    }

    // ✅ AI: Get full progress analytics for a task
    public TaskProgressDTO getTaskProgress(Integer taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + taskId));

        List<ChecklistItem> checklist = checklistItemRepository
                .findByTaskIdOrderByDayNumberAsc(taskId);

        long totalDays = checklistItemRepository.countByTaskId(taskId);
        long completedDays = checklistItemRepository.countByTaskIdAndIsDoneTrue(taskId);

        return new TaskProgressDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline(),
                totalDays,
                completedDays,
                checklist
        );
    }

    // ✅ GET ALL TASKS
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // ✅ Get only logged-in user's tasks
    public List<TaskResponseDTO> getAllTasksAsDTO() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return taskRepository.findByUserUsername(username)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Get only logged-in user's paged tasks
    public Page<TaskResponseDTO> getAllTasksPagedAsDTO(Pageable pageable) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return taskRepository.findByUserUsername(username, pageable)
                .map(this::mapToResponse);
    }

    // ✅ GET TASK BY ID
    public Task getTaskById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
    }

    // ✅ CONVERT TASK TO DTO
    public TaskResponseDTO convertToDTO(Task task) {
        return mapToResponse(task);
    }

    // ✅ UPDATE TASK
    public Task updateTask(Integer id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setPriority(taskDetails.getPriority());
        task.setStatus(taskDetails.getStatus());
        task.setDeadline(taskDetails.getDeadline());

        return taskRepository.save(task);
    }

    // ✅ UPDATE TASK USING DTO
    public TaskResponseDTO updateTaskAsDTO(Integer id, TaskRequestDTO request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus() != null ? request.getStatus() : task.getStatus());
        task.setDeadline(request.getDeadline());

        return mapToResponse(taskRepository.save(task));
    }

    // ✅ DELETE TASK
    public void deleteTask(Integer id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
        taskRepository.delete(task);
    }

    // ✅ PAGINATION
    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    // ✅ HELPER: map Task to TaskResponseDTO
    private TaskResponseDTO mapToResponse(Task task) {
        TaskResponseDTO response = new TaskResponseDTO();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setPriority(task.getPriority());
        response.setStatus(task.getStatus());
        response.setDeadline(task.getDeadline());
        response.setCreatedAt(task.getCreatedAt());
        if (task.getUser() != null) {
            response.setUserId(task.getUser().getId());
        }
        return response;
    }
}