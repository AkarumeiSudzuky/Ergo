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

    /**
     * Saves the given team to the repository.
     * @param team the team to save
     * @return the saved team
     */
    public Team save(Team team) {
        return teamRepository.save(team);
    }

    /**
     * Deletes the specified team from the repository.
     * @param team the team to delete
     */
    public void delete(Team team) {
        teamRepository.delete(team);
    }

    /**
     * Finds a team by its ID.
     * @param id the team ID
     * @return the team with the specified ID
     * @throws RuntimeException if the team is not found
     */
    public Team findById(int id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));
    }

    /**
     * Finds a team by its name.
     * @param name the name of the team
     * @return the team with the specified name
     */
    public Team findByName(String name) {
        return teamRepository.findTeamByName(name);
    }

    /**
     * Retrieves the ID of the last team created (i.e., the team with the highest ID).
     * @return the ID of the last team, or null if no teams exist
     */
    public Integer getLastTeamId() {
        return teamRepository.findTopByOrderByIdDesc()
                .map(Team::getId)
                .orElse(null);
    }

    /**
     * Adds a set of users to a team.
     * @param teamId the ID of the team
     * @param users the set of users to add to the team
     * @throws IllegalArgumentException if the team or any user does not exist
     */
    @Transactional
    public void addUserToTeam(Long teamId, Set<User> users) {
        Team team = entityManager.find(Team.class, teamId);
        if (team == null) {
            throw new IllegalArgumentException("Team with ID " + teamId + " does not exist.");
        }

        for (User user : users) {
            User persistedUser = entityManager.find(User.class, user.getId());
            if (persistedUser == null) {
                throw new IllegalArgumentException("User with ID " + user.getId() + " does not exist.");
            }
            team.getUsers().add(persistedUser);
            persistedUser.getGroups().add(team);
        }

        // Persist the changes
        entityManager.merge(team);
    }

    /**
     * Retrieves all users in a specific team.
     * @param teamId the ID of the team
     * @return a list of users in the specified team
     * @throws RuntimeException if the team is not found
     */
    public List<User> getUsersInTeam(int teamId) {
        Optional<Team> team = teamRepository.findById(teamId);
        return team.map(t -> new ArrayList<>(t.getUsers()))
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
    }

    /**
     * Retrieves all teams for a specific user.
     * @param userId the ID of the user
     * @return a list of teams the user is a member of
     * @throws RuntimeException if the user is not found
     */
    public List<Team> getTeamsForUser(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return new ArrayList<>(user.getGroups());
    }

    /**
     * Retrieves all tasks for a specific team.
     * @param teamId the ID of the team
     * @return a list of tasks associated with the specified team
     * @throws RuntimeException if the team is not found
     */
    public List<Task> getTasksForTeam(int teamId) {
        Optional<Team> team = teamRepository.findById(teamId);
        return team.map(t -> new ArrayList<>(t.getTasks()))
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
    }
}
