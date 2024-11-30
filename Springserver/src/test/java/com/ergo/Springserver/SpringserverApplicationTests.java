package com.ergo.Springserver;

import com.ergo.Springserver.model.team.Team;
import com.ergo.Springserver.model.team.TeamDao;
import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.model.user.UserDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpringserverApplicationTests {

	@Autowired
	private UserDao userDao;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private TeamDao teamDao;




	@Transactional
	@Test
	public void testAddUserToTeam() {
		// Create and persist a team
		Team team = new Team();
		team.setName("Team Alpha");
		entityManager.persist(team);

		// Create and persist users
		User user1 = new User();
		user1.setUsername("John Doe");
		user1.setPassword("password1");
		user1.setEmail("john.doe@example.com");
		entityManager.persist(user1);

		User user2 = new User();
		user2.setUsername("Jane Smith");
		user2.setPassword("password2");
		user2.setEmail("jane.smith@example.com");
		entityManager.persist(user2);

		// Prepare the user set to add to the team
		Set<User> users = new HashSet<>();
		users.add(user1);
		users.add(user2);

		// Add users to the team
		teamDao.addUserToTeam(team.getId().longValue(), users);

		// Verify that the users are associated with the team
		Team persistedTeam = entityManager.find(Team.class, team.getId());
		assertNotNull(persistedTeam);
		assertEquals(2, persistedTeam.getUsers().size());
		assertTrue(persistedTeam.getUsers().contains(user1));
		assertTrue(persistedTeam.getUsers().contains(user2));

		// Verify that the team is associated with each user
		User persistedUser1 = entityManager.find(User.class, user1.getId());
		User persistedUser2 = entityManager.find(User.class, user2.getId());
		assertTrue(persistedUser1.getGroups().contains(team));
		assertTrue(persistedUser2.getGroups().contains(team));
	}

}
