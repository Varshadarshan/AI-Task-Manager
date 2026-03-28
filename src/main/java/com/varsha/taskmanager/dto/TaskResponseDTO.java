package com.varsha.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.varsha.taskmanager.model.TaskStatus;
import java.time.LocalDate;      // ✅ LocalDate not LocalDateTime
import java.time.LocalDateTime;

@JsonPropertyOrder({"id", "title", "description", "priority", "status", "deadline", "createdAt", "userId"})
public class TaskResponseDTO {

    private Integer id;
    private String title;
    private String description;
    private String priority;
    private TaskStatus status;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate deadline;    // ✅ LocalDate

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate createdAt;   // ✅ LocalDate

    private Integer userId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDate getDeadline() { return deadline; }       // ✅ LocalDate
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public LocalDate getCreatedAt() { return createdAt; }     // ✅ LocalDate
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}