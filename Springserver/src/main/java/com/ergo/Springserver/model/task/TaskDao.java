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
        // Assuming the task has a method to get the associated User
        User user = task.getUser();
        Team team = task.getTeam();

        // Save the user first if it's not already saved
        if (user.getId() == null) {
            user = userRepository.save(user);
        } else {
            user = userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        }
        if (team == null) {
            team = teamRepository.save(team);
        }
        else{
            team = teamRepository.findById(team.getId()).orElseThrow(() -> new EntityNotFoundException("Team not found"));
            task.setTeam(team);
        }

        task.setUser(user);
        taskRepository.save(task);
    }



    public void delete(Task task) {
        taskRepository.delete(task);
    }

    // Get tasks for a specific user
    public List<Task> getAllTasksForUser(Long id) {
        List<Task> tasks = taskRepository.findByUserId(id);
        if (tasks.isEmpty()) {
            System.out.println("No tasks found for user with ID: " + id);
        } else {
            System.out.println("Retrieved tasks for user with ID " + id + ": " + tasks);
        }
        return tasks;
    }


    public List<Task> getAllTasksForTeam(int teamId) {
        List<Task> tasks = taskRepository.findByTeamId(teamId);
        if (tasks.isEmpty()) {
            System.out.println("No tasks found for team with ID: " + teamId);
        }
        else {
            System.out.println("Retrieved tasks for team with ID " + teamId + ": " + tasks);
        }

        return tasks;
    }


    // Update task status
    public boolean updateTaskStatus(Long taskId, String newStatus) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setStatus(Integer.parseInt(newStatus));
            taskRepository.save(task);
            return true;
        } else {
            return false;  // Task not found
        }
    }

}
