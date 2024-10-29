package com.ergo.Springserver.model.undertask;

import com.ergo.Springserver.model.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UndertaskDao {
    @Autowired
    private UnderTaskRepository underTaskRepository;
    public void save(UnderTask underTask) {
        underTaskRepository.save(underTask);
    }
    public void delete(UnderTask underTask) {
        underTaskRepository.delete(underTask);
    }
//    public UnderTask findById(Long id) {
//        return underTaskRepository.findById(id).get();
//    }

    // Method to get all under-tasks for a specific task
    public List<UnderTask> getAllUnderTasksForTask(Long taskId) {
        return underTaskRepository.findByParentTaskId(taskId);
    }

    // Get tasks for a specific user
    public List<UnderTask> getAllUndertasksForUser(Long id) {
        List<UnderTask> undertasks = new ArrayList<>();
        Streamable.of(underTaskRepository.findByUserId(id)).forEach(undertasks::add);
        return undertasks;
    }

}
