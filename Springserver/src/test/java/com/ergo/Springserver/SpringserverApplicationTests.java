package com.ergo.Springserver;

import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.model.user.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringserverApplicationTests {

	@Autowired
	private UserDao userDao;

	@Test
	void addUserTest() {
		User user = new User();
		user.setUsername("Jane");
		user.setPassword("123");
		user.setEmail("jane@gmail.com");
		userDao.save(user);
	}

//	@Test
//	void getUserTest() {
//		User user = userDao.findByUsername("Mei");
//	}

}
