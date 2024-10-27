package com.ergo.Springserver.model.task;

import com.ergo.Springserver.model.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task,Long> {
    List<Task> findByUserId(Long userId);

}
