package com.ergo.Springserver.controllers;

import com.ergo.Springserver.dto.FriendRequest;
import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.model.user.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing user-related operations.
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserDao userDao;

    //========= GET Endpoints ==========

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user.
     * @return the user object.
     */
    @GetMapping("/get-one")
    public ResponseEntity<User> getOneUserId(@RequestParam Long id) {
        User user = userDao.findById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user.
     * @return the user object.
     */
    @GetMapping("/get-one-username")
    public ResponseEntity<User> getOneUserName(@RequestParam String username) {
        User user = userDao.findByUsername(username);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    /**
     * Retrieves the friends of a user.
     *
     * @param userId the ID of the user.
     * @return a list of friends.
     */
    @GetMapping("/get-friends")
    public ResponseEntity<List<User>> getFriends(@RequestParam Long userId) {
        List<User> friends = userDao.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all users.
     */
    @GetMapping("/get-all")
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userDao.findAll());
    }

    //========= POST Endpoints ==========

    /**
     * Adds a friend to a user's friend list.
     *
     * @param friendRequest the friend request containing userId and friendId.
     * @return a response indicating success or failure.
     */
    @PostMapping("/add-friend")
    public ResponseEntity<String> addFriend(@RequestBody FriendRequest friendRequest) {
        if (friendRequest == null || friendRequest.getUserId() == null || friendRequest.getFriendId() == null) {
            return ResponseEntity.badRequest().body("User ID and Friend ID must be provided.");
        }

        try {
            userDao.addFriend(friendRequest.getUserId(), friendRequest.getFriendId());
            return ResponseEntity.ok("Friend added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding friend.");
        }
    }

    //========= DELETE Endpoints ==========

    /**
     * Removes a friend from a user's friend list.
     *
     * @param userId   the ID of the user.
     * @param friendId the ID of the friend to be removed.
     */
    @DeleteMapping("/remove-friend")
    public ResponseEntity<String> removeFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        try {
            userDao.removeFriend(userId, friendId);
            return ResponseEntity.ok("Friend removed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing friend.");
        }
    }
}
