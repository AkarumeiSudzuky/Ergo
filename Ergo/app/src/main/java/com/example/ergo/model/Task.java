package com.example.ergo.model;

import java.util.Date;


public class Task {
    private int id;
    private String title;
    private String description;
    private Date startDate;
    private Date stopDate;
    private int priority;
    private int status;

    //friends
    //unsure about this
//    private List<String> selectedFriends;
//    public List<String> getSelectedFriends() {
//        return selectedFriends;
//    }
//
//    public void setSelectedFriends(List<String> selectedFriends) {
//        this.selectedFriends = selectedFriends;
//    }
//

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = true) // 'task_id' will be the foreign key column in the database
//    private User user; // Reference to the Task entity


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", status=" + status +
//                ", user=" + user +
                '}';
    }

}
