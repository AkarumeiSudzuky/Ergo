package com.ergo.Springserver.model.undertask;

import com.ergo.Springserver.model.task.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnderTaskRepository extends CrudRepository<UnderTask, Integer> {
    List<UnderTask> findByTaskId(Long taskId);
    List<UnderTask> findByUserId(Long userId);
}
