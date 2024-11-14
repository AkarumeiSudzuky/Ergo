package com.ergo.Springserver.model.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserDao {

    @Autowired
    private UserRepository usrRepository;

    public User save(User user) {
        return usrRepository.save(user);

    }
    public void delete(User user) {
        usrRepository.delete(user);
    }

    public List<User> findAll() {
        return (List<User>)usrRepository.findAll();
    }

    public User findByUsername(String username) {
        return usrRepository.findByUsername(username);
    }

    public User findById(Long id) {
        return usrRepository.findById(id) .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // Retrieve list of friends for a specific user by ID
    public List<User> getFriends(Long userId) {

        if (userId == null) {
            // Handle the case where userId is null, maybe log it or return an empty list
            return new ArrayList<>();
        }

        User user = usrRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Get friends where the user is the source
        List<User> directFriends = new ArrayList<>(user.getFriends());

        // Convert Iterable<User> to List<User>
        List<User> allUsers = new ArrayList<>();
        usrRepository.findAll().forEach(allUsers::add);

        // Get friends where the user is the target (i.e., others have this user as a friend)
        List<User> reciprocalFriends = allUsers.stream()
                .filter(u -> u.getFriends().contains(user))
                .collect(Collectors.toList());

        // Combine the two lists and remove duplicates
        Set<User> allFriends = new HashSet<>(directFriends);
        allFriends.addAll(reciprocalFriends);

        return new ArrayList<>(allFriends);
    }



    // Add a friend to a specific user
    public void addFriend(Long userId, Long friendId) {
        User user = usrRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        User friend = usrRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found with id: " + friendId));

        // Check if the friendship already exists for both users
        if (!user.getFriends().contains(friend) && !friend.getFriends().contains(user)) {
            user.getFriends().add(friend);
            friend.getFriends().add(user); // Ensure the friend also has the user as a friend

            // Save both users to persist the bidirectional relationship
            usrRepository.save(user);
            usrRepository.save(friend);
        }
    }


    public void removeFriend(Long userId, Long friendId) {
        User user = usrRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        User friend = usrRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found with id: " + friendId));

        // Check if they are friends
        if (user.getFriends().contains(friend) && friend.getFriends().contains(user)) {
            // Remove the friend from both users' friend lists
            user.getFriends().remove(friend);
            friend.getFriends().remove(user);

            // Save both users to persist the changes
            usrRepository.save(user);
            usrRepository.save(friend);
        } else {
            throw new RuntimeException("No such friendship exists between users");
        }
    }

}
