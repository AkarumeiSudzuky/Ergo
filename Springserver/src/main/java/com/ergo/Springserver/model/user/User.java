package com.ergo.Springserver.model.user;

import com.ergo.Springserver.model.team.Team;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;
    private String username;
    private String password;
    private String email;

    // Getters and Setters for friends
    // Relationship for friends
    @Getter
    @Setter
    @ManyToMany
    @JoinTable(
            name = "Friends", // Name of the join table
            joinColumns = @JoinColumn(name = "user_id"), // Foreign key to the User
            inverseJoinColumns = @JoinColumn(name = "friend_id") // Foreign key to the Friend (another User)
    )
    @JsonIgnore
    private Set<User> friends;

    // Relationship for groups
    @Getter
    @ManyToMany
    @JoinTable(
            name = "User_Team", // Name of the join table
            joinColumns = @JoinColumn(name = "user_id"), // Foreign key to the User
            inverseJoinColumns = @JoinColumn(name = "team_id") // Foreign key to the Group
    )
    @JsonIgnore
    private Set<Team> groups = new HashSet<>();


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }


    @Override
    public String getUsername() {
        return username;
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


}
