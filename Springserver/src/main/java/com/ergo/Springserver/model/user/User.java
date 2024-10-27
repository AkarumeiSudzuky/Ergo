package com.ergo.Springserver.model.user;

import com.ergo.Springserver.model.team.Team;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;

    // Relationship for friends
    @ManyToMany
    @JoinTable(
            name = "Friends", // Name of the join table
            joinColumns = @JoinColumn(name = "user_id"), // Foreign key to the User
            inverseJoinColumns = @JoinColumn(name = "friend_id") // Foreign key to the Friend (another User)
    )
    @JsonIgnore
    private Set<User> friends;

    // Getters and Setters for friends
    public Set<User> getFriends() {
        return friends;
    }

    // Relationship for groups
    @ManyToMany
    @JoinTable(
            name = "User_Team", // Name of the join table
            joinColumns = @JoinColumn(name = "user_id"), // Foreign key to the User
            inverseJoinColumns = @JoinColumn(name = "team_id") // Foreign key to the Group
    )
    @JsonIgnore
    private Set<Team> groups = new HashSet<>();

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        return "user{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public void addFriend(User friend) {
        this.friends.add(friend);
    }

    public void removeFriend(User friend) {
        this.friends.remove(friend);
    }

    public Set<Team> getGroups() {
        return groups;
    }
}
