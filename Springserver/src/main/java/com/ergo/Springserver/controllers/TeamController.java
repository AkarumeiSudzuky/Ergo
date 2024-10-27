package com.ergo.Springserver.controllers;

import com.ergo.Springserver.model.team.Team;
import com.ergo.Springserver.model.team.TeamDao;
import com.ergo.Springserver.model.team.TeamRepository;
import com.ergo.Springserver.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TeamController {
    @Autowired
    private TeamDao teamDao;


    //===========Post================================
    @PostMapping("/team/save")
    public Team saveTeam(@RequestBody Team team) {
        return teamDao.save(team);
    }

    @PostMapping("/team/add-user")
    public void addUser(@RequestParam Long teamId, @RequestParam Long userId) {
        teamDao.addUserToTeam(teamId,userId);
    }


    //==========Get===================================

    @GetMapping("/team/get-all-forUser")
    public List<Team> getAllTeams(long id) {
        return teamDao.getTeamsForUser(id);
    }

    @GetMapping("/team/get-all-users")
    public List<User> getAllUsers(int id) {
        return teamDao.getUsersInTeam(id);
    }





}
