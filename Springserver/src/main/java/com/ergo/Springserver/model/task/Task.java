package com.ergo.Springserver.model.task;

import com.ergo.Springserver.model.team.Team;
import com.ergo.Springserver.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.Group;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Setter
    @Getter
    private String description;
    @Setter
    @Getter
    private int priority;
    @Getter
    @Setter
    private Date startDate;
    @Getter
    @Setter
    private Date stopDate;
    @Getter
    @Setter
    private int status;
    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Getter
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "group_id", nullable = true)
    private Team team;

    public Task() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", startDate=" + startDate +
                ", stopDate=" + stopDate +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", user=" + user +
                ", team=" + team +
                '}';
    }

    public Task(Integer id, String description, int priority, Date startDate, Date stopDate, int status, String title, User user) {
        this.id = id;
        this.description = description;
        this.priority = priority;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.status = status;
        this.title = title;
        this.user = user;
    }
}
