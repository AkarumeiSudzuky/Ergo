package com.ergo.Springserver.model.task;

import com.ergo.Springserver.model.team.Team;
import com.ergo.Springserver.model.team.TeamRepository;
import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.model.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskDao {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    /**
     * Save or update a task.
     * Ensures that the associated user and team are valid before saving the task.
     *
     * @param task The task to be saved or updated.
     */
    @Transactional
    public void saveTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        User user = task.getUser();
        if (user != null) {
            user = saveOrUpdateUser(user);
            task.setUser(user);
        }

        Team team = task.getTeam();
        if (team != null) {
            team = saveOrUpdateTeam(team);
            task.setTeam(team);
        }

        taskRepository.save(task);
    }

    /**
     * Save or update the user. If the user doesn't exist, it will be saved.
     *
     * @param user The user to be saved or updated.
     * @return The saved or updated user.
     */
    private User saveOrUpdateUser(User user) {
        if (user.getId() == null) {
            return userRepository.save(user);
        } else {
            return userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        }
    }

    /**
     * Save or update the team. If the team doesn't exist, it will be saved.
     *
     * @param team The team to be saved or updated.
     * @return The saved or updated team.
     */
    private Team saveOrUpdateTeam(Team team) {
        if (team.getId() == null) {
            return teamRepository.save(team);
        } else {
            return teamRepository.findById(team.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Team not found"));
        }
    }

    /**
     * Delete a task.
     *
     * @param task The task to be deleted.
     */
    public void delete(Task task) {
        taskRepository.delete(task);
    }

    /**
     * Get all tasks for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of tasks associated with the user.
     */
    public List<Task> getAllTasksForUser(Long userId) {
        List<Task> tasks = taskRepository.findByUserId(userId);
        if (tasks.isEmpty()) {
            // log.debug("No tasks found for user with ID: {}", userId);
        }
        return tasks;
    }

    /**
     * Get all tasks for a specific team.
     *
     * @param teamId The ID of the team.
     * @return A list of tasks associated with the team.
     */
    public List<Task> getAllTasksForTeam(int teamId) {
        List<Task> tasks = taskRepository.findByTeamId(teamId);
        if (tasks.isEmpty()) {
            // log.debug("No tasks found for team with ID: {}", teamId);
        }
        return tasks;
    }

    /**
     * Update the status of a task.
     *
     * @param taskId The ID of the task to be updated.
     * @param newStatus The new status to set for the task.
     * @return true if the task status was updated, false if the task was not found.
     */
    public boolean updateTaskStatus(int taskId, int newStatus) {
        Optional<Task> optionalTask = taskRepository.findById((long) taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setStatus(newStatus);
            taskRepository.save(task);  //
            return true;
        }
        return false;
    }

    /**
     * Get a task by its ID.
     *
     * @param taskId The ID of the task.
     * @return The task with the given ID.
     * @throws EntityNotFoundException If the task is not found.
     */
    public Task getTaskById(int taskId) {
        return taskRepository.findById((long) taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));
    }

    /**
     * Delete a task by its ID.
     *
     * @param taskId The ID of the task to be deleted.
     */
    public void deleteTaskById(int taskId) {
        Optional<Task> optionalTask = taskRepository.findById((long) taskId);
        optionalTask.ifPresent(taskRepository::delete);
    }
}
