package com.ergo.Springserver.model.undertask;

import com.ergo.Springserver.model.task.Task;
import com.ergo.Springserver.model.user.User;
import jakarta.persistence.*;

@Entity
public class UnderTask extends Task {

    // Foreign key relationship to Task (parent task)
    @ManyToOne
    @JoinColumn(name = "parent_task_id", nullable = false) // 'parent_task_id' as the foreign key in the database
    private Task parentTask; // Reference to the parent Task entity

    // Foreign key relationship to User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // 'user_id' as the foreign key in the database
    private User user; // Reference to the User entity associated with this UnderTask

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    @Override
    public String toString() {
        return "UnderTask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", priority=" + getPriority() +
                ", status=" + getStatus() +
                ", parentTask=" + parentTask +
                ", user=" + user +
                '}';
    }
}
