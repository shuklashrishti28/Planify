package com.planify.model;

import java.sql.Date;

public class Task {
    private int taskId;
    private int projectId;
    private int assignedTo;
    private String title;
    private String description;
    private String status;
    private Date dueDate;

    // Additional UI Display Fields
    private String projectTitle;
    private String assignedToName;

    public Task() {}

    public Task(int taskId, int projectId, int assignedTo, String title, String description, String status, Date dueDate) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.assignedTo = assignedTo;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public int getAssignedTo() { return assignedTo; }
    public void setAssignedTo(int assignedTo) { this.assignedTo = assignedTo; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }
}
