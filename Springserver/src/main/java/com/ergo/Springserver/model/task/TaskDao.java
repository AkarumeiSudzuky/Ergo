package com.ergo.Springserver.model.task;

import com.ergo.Springserver.model.team.Team;
import com.ergo.Springserver.model.team.TeamRepository;
import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.model.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Transactional
    public void saveTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        // Get the user and team associated with the task, ensuring they are not null before saving
        User user = task.getUser();
        Team team = task.getTeam();

        // Handle saving the user if it's null or not present
        if (user != null) {
            if (user.getId() == null) {
                user = userRepository.save(user); // Save the user if it's a new one
            } else {
                user = userRepository.findById(user.getId())
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));
            }
            task.setUser(user); // Associate the user with the task
        }

        // Handle saving the team if it's null or not present
        if (team != null) {
            if (team.getId() == null) {
                team = teamRepository.save(team); // Save the team if it's a new one
            } else {
                team = teamRepository.findById(team.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Team not found"));
            }
            task.setTeam(team); // Associate the team with the task
        }

        taskRepository.save(task); // Save the task
    }




    public void delete(Task task) {
        taskRepository.delete(task);
    }

    // Get tasks for a specific user
    public List<Task> getAllTasksForUser(Long id) {
        List<Task> tasks = taskRepository.findByUserId(id);
        if (tasks.isEmpty()) {
            System.out.println("No tasks found for user with ID: " + id);
        }
        return tasks;
    }


    public List<Task> getAllTasksForTeam(int teamId) {
        List<Task> tasks = taskRepository.findByTeamId(teamId);
        if (tasks.isEmpty()) {
//            System.out.println("No tasks found for team with ID: " + teamId);
        }
        

        return tasks;
    }


    // Update task status
    public boolean updateTaskStatus(int taskId, int newStatus) {
        Optional<Task> optionalTask = taskRepository.findById((long) taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setStatus(newStatus);
            taskRepository.save(task);
            return true;
        } else {
            return false;  // Task not found
        }
    }

    //
    public Task getTaskById(int taskId) {
        return taskRepository.findById((long) taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));
    }


    public void deleteTaskById(int taskId) {

        Optional<Task> optionalTask = taskRepository.findById((long) taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            taskRepository.delete(task);
        }

    }

}
