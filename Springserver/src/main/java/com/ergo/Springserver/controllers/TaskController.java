package com.ergo.Springserver.controllers;

import com.ergo.Springserver.model.task.Task;
import com.ergo.Springserver.model.task.TaskDao;
import com.ergo.Springserver.model.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {
    @Autowired
    private TaskDao taskDao;

    //==============Get=================

    @GetMapping("/task/get-tasks-for-user")
    public List<Task> getTasksForUser(@RequestParam Long userId) {
        return taskDao.getAllTasksForUser(userId);
    }

    @GetMapping("/task/get-tasks-for-team")
    public List<Task> getTasksForTeam(@RequestParam int teamId) {
        return taskDao.getAllTasksForTeam(teamId);
    }

    //
    @GetMapping("/task/get-task-by-id")
    public Task getTaskById(@RequestParam int taskId) {
        return taskDao.getTaskById(taskId);
    }


    //=================Post===============

    @PostMapping("/task/save")
    public void saveTask(@RequestBody Task task) {

        taskDao.saveTask(task);
    }


    //========================Put==========
    @PutMapping("/task/update-task-status")
    public void updateTaskStatus(@RequestParam int taskId, @RequestParam int status) {
        taskDao.updateTaskStatus(taskId, status);
    }



    //================Delete================
    @DeleteMapping("/task/delete")
    public void deleteTask(@RequestParam int taskId) {
        taskDao.deleteTaskById(taskId);
    }




}
