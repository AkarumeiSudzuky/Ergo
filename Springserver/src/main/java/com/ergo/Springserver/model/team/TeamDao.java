package com.ergo.Springserver.model.team;

import com.ergo.Springserver.model.task.Task;
import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.model.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamDao {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserDao userDao;

    public Team save(Team team) {
        return teamRepository.save(team);
    }

    public void delete(Team team) {
        teamRepository.delete(team);
    }

    public Team findById(int id) {
        return teamRepository.findById(id).orElseThrow(() -> new RuntimeException("Team not found with id: " + id));
    }

    // Add a user to a team
    public void addUserToTeam(Long teamId, Long userId) {
        Team team = teamRepository.findById(Math.toIntExact(teamId)).orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
        User user = userDao.findById(userId);
        team.getUsers().add(user); // Add user to the team's users set
        teamRepository.save(team); // Save team to persist the relationship
    }

    // Method to get all users in a specific team
    public List<User> getUsersInTeam(int teamId) {
        Optional<Team> team = teamRepository.findById(teamId);
        return team.map(t -> t.getUsers().stream().collect(Collectors.toList()))
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
    }

    // Method to get all teams for a specific user
    public List<Team> getTeamsForUser(Long userId) {
        User user = userDao.findById(userId);
        return new ArrayList<>(user.getGroups());
    }


    // Method to get all tasks for a specific team
    public List<Task> getTasksForTeam(int teamId) {
        Optional<Team> team = teamRepository.findById(teamId);
        return team.map(t -> t.getTasks().stream().collect(Collectors.toList()))
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
    }
}
