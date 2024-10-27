package com.ergo.Springserver.model.undertask;

import com.ergo.Springserver.model.task.Task;
import com.ergo.Springserver.model.user.User;
import jakarta.persistence.*;

@Entity
public class UnderTask {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String description;
    private int priority;
    private int status;

    // Foreign key relationship to Task
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false) // 'task_id' will be the foreign key column in the database
    private Task task; // Reference to the Task entity

    // Foreign key relationship to Task
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // 'task_id' will be the foreign key column in the database
    private User user; // Reference to the Task entity

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UnderTask{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", task=" + task +
                ", user=" + user +
                '}';
    }
}
