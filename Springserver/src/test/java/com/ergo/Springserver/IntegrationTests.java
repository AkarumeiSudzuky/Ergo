package com.ergo.Springserver;

import static org.assertj.core.api.Assertions.assertThat;

import com.ergo.Springserver.dto.SignInRequest;
import com.ergo.Springserver.model.task.Task;
import com.ergo.Springserver.model.task.TaskDao;
import com.ergo.Springserver.model.task.TaskRepository;
import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.model.user.UserDao;
import com.ergo.Springserver.model.user.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request
        .MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
public class IntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    @Mock
    private UserRepository userRepository;
    @Autowired
    @Mock
    private TaskRepository taskRepository;
    @Autowired
    private UserDao userDao;
    @Autowired
    private TaskDao taskDao;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void cleanUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void whenValidInput_thenCreateUser() throws Exception {
        User newUser = new User(null, "Ergo", "Ergo", "Ergo@gmail.com");

        mvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newUser)))
                .andExpect(status().isOk());

        List<User> found = (List<User>) userRepository.findAll();
        assertThat(found).extracting(User::getUsername)
                .containsOnly("Ergo");
    }

    @Test
    public void whenValidSigninRequest_thenReturnJwtToken() throws Exception {
        User newUser = User.builder()
                .username("Ergo")
                .password(passwordEncoder.encode("Ergo"))
                .email("Ergo@gmail.com")
                .build();
        userDao.save(newUser);

        userDao.save(newUser);
        SignInRequest request = SignInRequest.builder()
                .username("Ergo")
                .password("Ergo")
                .build();

        String jsonRequest = mapper.writeValueAsString(request);

        mvc.perform(post("/user/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

    }

    @Test
    public void whenValidSignInAndToken_thenFetchCurrentUser() throws Exception {
        // Step 1: Create and save a new user
        User newUser = User.builder()
                .username("Ergo")
                .password(passwordEncoder.encode("Ergo"))
                .email("Ergo@gmail.com")
                .build();
        userDao.save(newUser);

        // Step 2: Sign in and get JWT token
        SignInRequest request = SignInRequest.builder()
                .username("Ergo")
                .password("Ergo")
                .build();

        String jsonRequest = mapper.writeValueAsString(request);

        MvcResult res = mvc.perform(post("/user/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        // Extract token from response
        String responseContent = res.getResponse().getContentAsString();
        String token = JsonPath.parse(responseContent).read("$.token");
        String validToken = "Bearer " + token;

        // Step 3: Use the token to fetch current user
        mvc.perform(get("/user/current")
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Ergo"))
                .andExpect(result -> {
                    // Use JsonPath to extract the password from the response JSON
                    String actualPassword = JsonPath.parse(result.getResponse().getContentAsString())
                            .read("$.password");

                    // Validate password using passwordEncoder
                    assertTrue(passwordEncoder.matches("Ergo", actualPassword)); // Check if the encoded password matches
                });
    }

    @Test
    public void whenValidTask_thenChangeItsStatus() throws Exception {
        User newUser = new User(null, "Ergo", "Ergo", "Ergo@gmail.com");
        userDao.save(newUser);
        Task task = new Task(null, "task description", 1, Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), 1, "task 1", newUser);
        taskDao.saveTask(task);
        Integer id = task.getId();
        mvc.perform(put("/task/update-task-status?taskId="+id+"&status=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/task/get-task-by-id?taskId="+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(2));
    }



}

