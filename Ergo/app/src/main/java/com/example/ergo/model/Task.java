package com.example.ergo.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class Task implements Serializable {
    private int id;
    private String description;
    private int priority;
    @SerializedName("startDate")
    private String startDateIso;

    @SerializedName("stopDate")
    private String stopDateIso;
    private int status;
    private String title;

    private Long groupId;


    //    @ManyToOne(cascade = CascadeType.PERSIST)
//    @JoinColumn(name = "user_id", nullable = true)
    private User user;
    private Team team;

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


    public void setStartDate(Date startDate) {
        this.startDateIso = formatToIso8601(startDate);
    }

    public void setStopDate(Date stopDate) {
        this.stopDateIso = formatToIso8601(stopDate);
    }

    private String formatToIso8601(Date date) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return isoFormat.format(date);
    }

    public String getStartDate() {
        return startDateIso;
    }

    public String getStopDate() {
        return stopDateIso;
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", startDate=" + startDateIso +
                ", stopDate=" + stopDateIso +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", user=" + user +
                '}';
    }
}