package com.ergo.Springserver.controllers;

import com.ergo.Springserver.model.team.Team;
import com.ergo.Springserver.model.team.TeamDao;
import com.ergo.Springserver.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * Controller for managing teams and their associated users.
 */
@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamDao teamDao;

    //=========== Post Endpoints ============

    /**
     * Saves a new team to the database.
     *
     * @param team the team to be saved.
     * @return the saved team.
     */
    @PostMapping("/save")
    public Team saveTeam(@RequestBody Team team) {
        return teamDao.save(team);
    }

    /**
     * Adds users to a specific team.
     *
     * @param teamId the ID of the team.
     * @param users  the set of users to add.
     */
    @PostMapping("/add-users")
    public void addUser(@RequestParam Long teamId, @RequestBody Set<User> users) {
        teamDao.addUserToTeam(teamId, users);
    }

    //=========== Get Endpoints ============

    /**
     * Retrieves all teams for a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of teams associated with the user.
     */
    @GetMapping("/get-all-for-user")
    public List<Team> getAllTeams(@RequestParam("userId") long userId) {
        return teamDao.getTeamsForUser(userId);
    }

    /**
     * Retrieves the ID of the last created team.
     *
     * @return the ID of the last team.
     */
    @GetMapping("/get-last")
    public Long getLastTeam() {
        return (long) teamDao.getLastTeamId();
    }

    /**
     * Retrieves all users in a specific team.
     *
     * @param teamId the ID of the team.
     * @return a list of users in the team.
     */
    @GetMapping("/get-all-users")
    public List<User> getAllUsers(@RequestParam("teamId") Integer teamId) {
        return teamDao.getUsersInTeam(teamId);
    }
}
