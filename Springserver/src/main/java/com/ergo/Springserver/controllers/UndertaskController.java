package com.ergo.Springserver.controllers;

import com.ergo.Springserver.model.undertask.UnderTask;
import com.ergo.Springserver.model.undertask.UndertaskDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UndertaskController {
    @Autowired
    private UndertaskDao undertaskDao;

    //==================Get==================

    @GetMapping("/undertask/get-all-forTask")
    public List<UnderTask> getAllForTask(Long teamId) {
        return undertaskDao.getAllUnderTasksForTask(teamId);
    }

    @GetMapping("/undertask/get-all-forUser")
    public List<UnderTask> getAllForUser(Long userId) {
        return undertaskDao.getAllUndertasksForUser(userId);
    }

    //==================Post===================

    @PostMapping("/undertask/save")
    public void saveUndertask(UnderTask undertask) {
        undertaskDao.save(undertask);
    }
}
