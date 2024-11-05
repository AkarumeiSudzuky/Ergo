package com.ergo.Springserver.model.task;

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

    @Transactional
    public void saveTask(Task task) {
        // Assuming the task has a method to get the associated User
        User user = task.getUser();

        // Save the user first if it's not already saved
        if (user.getId() == null) {
            user = userRepository.save(user);
        } else {
            user = userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        }

        task.setUser(user);
        taskRepository.save(task);
    }



    public void delete(Task task) {
        taskRepository.delete(task);
    }

    // Get tasks for a specific user
    public List<Task> getAllTasksForUser(Long id) {
        List<Task> tasks = new ArrayList<>();
        Streamable.of(taskRepository.findByUserId(id)).forEach(tasks::add);
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
