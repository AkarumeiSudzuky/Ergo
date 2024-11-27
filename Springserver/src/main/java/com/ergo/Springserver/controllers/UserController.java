package com.ergo.Springserver.controllers;

import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.model.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @GetMapping("/user/get-one-username")
//    public User getOneUserName(String username) {
//        return userDao.findByUsername(username);
//    }

    @GetMapping("/user/get-friends")
    public List<User> getFriends(Long userId) {
        return userDao.getFriends(userId);
    }

    @GetMapping("/user/get-all")
    public List<User> getAll() {
        return userDao.findAll();
    }



    //=========POST=================
//    @PostMapping("/user/save")
//    public User addUser(@RequestBody User user) {
//        return userDao.save(user);
//    }

    @PostMapping("/user/add-friend")
    public void addFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        userDao.addFriend(userId,friendId);
    }


    //=========DELETE=================
    @DeleteMapping("/user/remove-friend")
    public void removeFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        userDao.removeFriend(userId, friendId);
    }




}
