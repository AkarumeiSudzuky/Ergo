package com.example.ergo.model;


import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class Task {
    private int id;
    private String description;
    private int priority;
    @SerializedName("startDate")
    private String startDateIso;

    @SerializedName("stopDate")
    private String stopDateIso;
    private int status;
    private String title;

    //    @ManyToOne(cascade = CascadeType.PERSIST)
//    @JoinColumn(name = "user_id", nullable = true)
    private User user;


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

//    public Date getStartDate() {
//        return startDate;
//    }


//    public Date getStopDate() {
//        return stopDate;
//    }

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