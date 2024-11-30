package com.ergo.Springserver.controllers;

import com.ergo.Springserver.dto.FriendRequest;
import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.model.user.UserDao;
import com.ergo.Springserver.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    private UserDao userDao;

    //=========GET===============

    @GetMapping("/user/get-one")
    public User getOneUserId(Long id) {
        return userDao.findById(id);
    }

    @GetMapping("/user/get-one-username")
    public User getOneUserName(@RequestParam String username) {
        return userDao.findByUsername(username);
    }

    @GetMapping("/user/get-friends")
    public List<User> getFriends(Long userId) {
        return userDao.getFriends(userId);
    }

    @GetMapping("/user/get-all")
    public List<User> getAll() {
        return userDao.findAll();
    }


    //=========POST=================

    @PostMapping("/user/add-friend")
    public ResponseEntity<String> addFriend(@RequestBody FriendRequest friendRequest) {
        // Ensure that the friendRequest object contains both userId and friendId
        if (friendRequest == null || friendRequest.getUserId() == null || friendRequest.getFriendId() == null) {
            return ResponseEntity.badRequest().body("User ID and Friend ID must be provided.");
        }

        // Call your DAO method to add the friend
        try {
            userDao.addFriend(friendRequest.getUserId(), friendRequest.getFriendId());
            return ResponseEntity.ok("Friend added successfully.");
        } catch (Exception e) {
            // Handle any exceptions, such as user not found or database errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding friend.");
        }
    }

    //=========DELETE=================
    @DeleteMapping("/user/remove-friend")
    public void removeFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        userDao.removeFriend(userId, friendId);
    }

}
