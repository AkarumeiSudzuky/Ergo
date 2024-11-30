package com.example.ergo.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Parcelable {

    private Long id;
    private String username;
    private String password;
    private String email;

    public User (String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }



//    // Relationship for friends
//    @ManyToMany
//    @JoinTable(
//            name = "Friends", // Name of the join table
//            joinColumns = @JoinColumn(name = "user_id"), // Foreign key to the User
//            inverseJoinColumns = @JoinColumn(name = "friend_id") // Foreign key to the Friend (another User)
//    )
//
//    private Set<User> friends;
//
//    // Getters and Setters for friends
//    public Set<User> getFriends() {
//        return friends;
//    }
//
//    // Relationship for groups
//    @ManyToMany
//    @JoinTable(
//            name = "User_Team", // Name of the join table
//            joinColumns = @JoinColumn(name = "user_id"), // Foreign key to the User
//            inverseJoinColumns = @JoinColumn(name = "team_id") // Foreign key to the Group
//    )
//
//    private Set<Team> groups = new HashSet<>();

//    public void setFriends(Set<User> friends) {
//        this.friends = friends;
//    }

    protected User(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        username = in.readString();
        password = in.readString();
        email = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(email);
    }

//    public void addFriend(User friend) {
//        this.friends.add(friend);
//    }
//
//    public void removeFriend(User friend) {
//        this.friends.remove(friend);
//    }
//
//    public Set<Team> getGroups() {
//        return groups;
//    }
}