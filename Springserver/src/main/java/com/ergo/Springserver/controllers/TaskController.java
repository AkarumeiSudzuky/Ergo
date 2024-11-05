package com.ergo.Springserver.controllers;

import com.ergo.Springserver.model.task.Task;
import com.ergo.Springserver.model.task.TaskDao;
import com.ergo.Springserver.model.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController {
    @Autowired
    private TaskDao taskDao;

    //==============Get=================

    @GetMapping("/task/get-tasks-for-user")
    public List<Task> getTasksForUser(Long userId) {
        return taskDao.getAllTasksForUser(userId);
    }


    //=================Post===============

    @PostMapping("/task/save")
    public void saveTask(@RequestBody Task task) {

        taskDao.saveTask(task);
    }




}
