package com.ergo.Springserver.model.team;

import com.ergo.Springserver.model.task.Task;
import com.ergo.Springserver.model.user.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    // Relationship for users in the group
    @ManyToMany(mappedBy = "groups")
    private Set<User> users = new HashSet<>();

    // Relationship for tasks associated with the team
    @ManyToMany
    @JoinTable(
            name = "Team_Task", // Name of the join table
            joinColumns = @JoinColumn(name = "team_id"), // Foreign key to the Team
            inverseJoinColumns = @JoinColumn(name = "task_id") // Foreign key to the Task
    )
    private Set<Task> tasks = new HashSet<>();


    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter for users
    public Set<User> getUsers() {
        return users;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", users=" + users +
                ", tasks=" + tasks +
                '}';
    }
}
