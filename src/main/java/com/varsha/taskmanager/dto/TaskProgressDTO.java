package com.varsha.taskmanager.dto;

import com.varsha.taskmanager.model.ChecklistItem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TaskProgressDTO {

    private Integer taskId;
    private String title;
    private String description;
    private LocalDate deadline;

    // ✅ Progress analytics
    private long totalDays;
    private long completedDays;
    private long remainingDays;
    private double progressPercentage;

    // ✅ Days remaining until deadline
    private long daysUntilDeadline;

    // ✅ Full checklist
    private List<ChecklistItem> checklist;

    // ✅ Motivational status message
    private String statusMessage;

    public TaskProgressDTO() {}

    public TaskProgressDTO(Integer taskId, String title, String description,
                           LocalDate deadline, long totalDays, long completedDays,
                           List<ChecklistItem> checklist) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.totalDays = totalDays;
        this.completedDays = completedDays;
        this.remainingDays = totalDays - completedDays;
        this.progressPercentage = totalDays > 0
                ? Math.round((completedDays * 100.0 / totalDays) * 10.0) / 10.0
                : 0.0;
        this.daysUntilDeadline = deadline != null
                ? ChronoUnit.DAYS.between(LocalDate.now(), deadline)
                : 0;
        this.checklist = checklist;
        this.statusMessage = generateStatusMessage();
    }

    // ✅ Generate motivational message based on progress
    private String generateStatusMessage() {
        if (progressPercentage == 0) {
            return "🚀 You haven't started yet. Begin today!";
        } else if (progressPercentage < 25) {
            return "💪 Great start! Keep the momentum going!";
        } else if (progressPercentage < 50) {
            return "🔥 You're making solid progress!";
        } else if (progressPercentage < 75) {
            return "⚡ Halfway there! You're doing amazing!";
        } else if (progressPercentage < 100) {
            return "🎯 Almost there! Final push!";
        } else {
            return "🎉 Completed! Congratulations!";
        }
    }

    public Integer getTaskId() { return taskId; }
    public void setTaskId(Integer taskId) { this.taskId = taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public long getTotalDays() { return totalDays; }
    public void setTotalDays(long totalDays) { this.totalDays = totalDays; }

    public long getCompletedDays() { return completedDays; }
    public void setCompletedDays(long completedDays) { this.completedDays = completedDays; }

    public long getRemainingDays() { return remainingDays; }
    public void setRemainingDays(long remainingDays) { this.remainingDays = remainingDays; }

    public double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(double progressPercentage) { this.progressPercentage = progressPercentage; }

    public long getDaysUntilDeadline() { return daysUntilDeadline; }
    public void setDaysUntilDeadline(long daysUntilDeadline) { this.daysUntilDeadline = daysUntilDeadline; }

    public List<ChecklistItem> getChecklist() { return checklist; }
    public void setChecklist(List<ChecklistItem> checklist) { this.checklist = checklist; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
}