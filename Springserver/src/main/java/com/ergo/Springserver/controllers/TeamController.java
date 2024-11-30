package com.ergo.Springserver.controllers;

import com.ergo.Springserver.model.team.Team;
import com.ergo.Springserver.model.team.TeamDao;
import com.ergo.Springserver.model.team.TeamRepository;
import com.ergo.Springserver.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class TeamController {
    @Autowired
    private TeamDao teamDao;


    //===========Post================================
    @PostMapping("/team/save")
    public Team saveTeam(@RequestBody Team team) {
        return teamDao.save(team);
    }

    @PostMapping("/team/add-users")
    public void addUser(@RequestParam Long teamId, @RequestBody Set<User> users) {
        teamDao.addUserToTeam(teamId, users);
    }



    //==========Get===================================

    @GetMapping("/team/get-all-forUser")
    public List<Team> getAllTeams(@RequestParam("userId") long userId) {
        return teamDao.getTeamsForUser(userId);
    }

    @GetMapping("/team/get-last")
    public Long getLastTeam() {
        return Long.valueOf(teamDao.getLastTeamId());
    }

    @GetMapping("/team/get-all-users")
    public List<User> getAllUsers(@RequestParam("teamId") Integer teamId) {
        return teamDao.getUsersInTeam(teamId);
    }





}
