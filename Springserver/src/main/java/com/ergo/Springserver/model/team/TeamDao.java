package com.ergo.Springserver.model.team;

import com.ergo.Springserver.model.task.Task;
import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.model.user.UserDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamDao {
    @Autowired
    private TeamRepository teamRepository;

    @PersistenceContext
    private EntityManager entityManager;

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

    public Team findByName(String name) {return teamRepository.findTeamByName(name);}

    public Integer getLastTeamId() {
        // Get the team with the maximum ID
        return teamRepository.findTopByOrderByIdDesc()
                .map(Team::getId)  // Extract the ID of the team
                .orElse(null);  // Return null if no team is found
    }

    // Add a user to a team
    @Transactional
    public void addUserToTeam(Long teamId, Set<User> users) {
        // Retrieve the team entity by its ID
        Team team = entityManager.find(Team.class, teamId);
        if (team == null) {
            throw new IllegalArgumentException("Team with ID " + teamId + " does not exist.");
        }

        // Add each user to the team
        for (User user : users) {
            // Check if the user exists in the database
            User persistedUser = entityManager.find(User.class, user.getId());
            if (persistedUser == null) {
                throw new IllegalArgumentException("User with ID " + user.getId() + " does not exist.");
            }

            // Add the user to the team's user set
            team.getUsers().add(persistedUser);

            // Add the team to the user's group set
            persistedUser.getGroups().add(team);
        }

        // Persist the changes
        entityManager.merge(team);
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
        System.out.println(userId);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return new ArrayList<>(user.getGroups());
    }



    // Method to get all tasks for a specific team
    public List<Task> getTasksForTeam(int teamId) {
        Optional<Team> team = teamRepository.findById(teamId);
        return team.map(t -> t.getTasks().stream().collect(Collectors.toList()))
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
    }
}
