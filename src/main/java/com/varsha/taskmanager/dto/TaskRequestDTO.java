package com.varsha.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.varsha.taskmanager.model.TaskStatus;
import java.time.LocalDate;  // ✅ LocalDate not LocalDateTime

public class TaskRequestDTO {

    private String title;
    private String description;

    @JsonFormat(pattern = "dd-MM-yyyy")  // ✅ user inputs like: 12-05-2025
    private LocalDate deadline;           // ✅ LocalDate

    private TaskStatus status;
    private String priority;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }      // ✅ LocalDate
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}