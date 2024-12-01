package com.ergo.Springserver.model.team;

import com.ergo.Springserver.model.task.Task;
import com.ergo.Springserver.model.user.User;
import jakarta.persistence.*;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    private String name;

    // Relationship for users in the group
    @ManyToMany(mappedBy = "groups")
    private Set<User> users = new HashSet<>();

    // Relationship for tasks associated with the team
    @Setter
    @ManyToMany
    @JoinTable(
            name = "Team_Task",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private Set<Task> tasks = new HashSet<>();


    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    // Getter for users
    public Set<User> getUsers() {
        return users;
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
