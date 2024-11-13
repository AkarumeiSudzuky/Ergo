package com.example.ergo.model;

import java.util.HashSet;
import java.util.Set;


public class Team {

    private int id;

    private String name;

    private Set<User> users = new HashSet<>();

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
