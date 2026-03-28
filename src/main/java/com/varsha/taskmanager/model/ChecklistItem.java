package com.varsha.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "checklist_items")
public class ChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ✅ Day number (Day 1, Day 2 ... Day 30)
    @Column(nullable = false)
    private Integer dayNumber;

    // ✅ AI generated topic for this day
    @Column(nullable = false)
    private String topic;

    // ✅ false = not done, true = completed
    @Column(nullable = false)
    private boolean isDone = false;

    // ✅ @JsonIgnore prevents infinite loop in JSON response
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    public ChecklistItem() {}

    public ChecklistItem(Integer dayNumber, String topic, Task task) {
        this.dayNumber = dayNumber;
        this.topic = topic;
        this.isDone = false;
        this.task = task;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getDayNumber() { return dayNumber; }
    public void setDayNumber(Integer dayNumber) { this.dayNumber = dayNumber; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { isDone = done; }

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
}