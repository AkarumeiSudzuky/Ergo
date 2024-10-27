package com.ergo.Springserver.model.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    public User findByUsername(String username) {
        return usrRepository.findByUsername(username);
    }

    public User findById(Long id) {
        return usrRepository.findById(id) .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // Retrieve list of friends for a specific user by ID
    public List<User> getFriends(Long userId) {
        Optional<User> user = usrRepository.findById(userId);
        return user.map(u -> u.getFriends().stream().collect(Collectors.toList()))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
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


}
