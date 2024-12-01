package com.ergo.Springserver.controllers;

import com.ergo.Springserver.model.task.Task;
import com.ergo.Springserver.model.task.TaskDao;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing tasks.
 */
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskDao taskDao;

    //============== Get Endpoints ==============

    /**
     * Retrieves all tasks for a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of tasks associated with the user.
     */
    @GetMapping("/get-tasks-for-user")
    public List<Task> getTasksForUser(@RequestParam Long userId) {
        return taskDao.getAllTasksForUser(userId);
    }

    /**
     * Retrieves all tasks for a specific team.
     *
     * @param teamId the ID of the team.
     * @return a list of tasks associated with the team.
     */
    @GetMapping("/get-tasks-for-team")
    public List<Task> getTasksForTeam(@RequestParam int teamId) {
        return taskDao.getAllTasksForTeam(teamId);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param taskId the ID of the task.
     * @return the task with the specified ID.
     */
    @GetMapping("/get-task-by-id")
    public Task getTaskById(@RequestParam int taskId) {
        return taskDao.getTaskById(taskId);
    }

    //============== Post Endpoint ==============

    /**
     * Saves a new task to the database.
     *
     * @param task the task to be saved.
     */
    @PostMapping("/save")
    public void saveTask(@RequestBody Task task) {
        taskDao.saveTask(task);
    }

    //============== Put Endpoint ==============

    /**
     * Updates the status of a specific task.
     *
     * @param taskId the ID of the task.
     * @param status the new status to set.
     */
    @PutMapping("/update-task-status")
    public void updateTaskStatus(@RequestParam int taskId, @RequestParam int status) {
        taskDao.updateTaskStatus(taskId, status);
    }

    //============== Delete Endpoint ==============

    /**
     * Deletes a task by its ID.
     *
     * @param taskId the ID of the task to be deleted.
     */
    @DeleteMapping("/delete")
    public void deleteTask(@RequestParam int taskId) {
        taskDao.deleteTaskById(taskId);
    }
}
