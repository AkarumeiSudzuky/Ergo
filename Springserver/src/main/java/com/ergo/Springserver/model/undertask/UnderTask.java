package com.ergo.Springserver.model.undertask;

import com.ergo.Springserver.model.task.Task;
import com.ergo.Springserver.model.user.User;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class UnderTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;
    private int priority;
    private Date startDate;
    private Date stopDate;
    private int status;
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // Foreign key to User table
    private User user;

    // Foreign key relationship to Task (parent task)
    @ManyToOne
    @JoinColumn(name = "parent_task_id", nullable = false) // 'parent_task_id' as the foreign key in the database
    private Task parentTask; // Reference to the parent Task entity

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    @Override
    public String toString() {
        return "UnderTask{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", startDate=" + startDate +
                ", stopDate=" + stopDate +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", user=" + user +
                ", parentTask=" + parentTask +
                '}';
    }
}
