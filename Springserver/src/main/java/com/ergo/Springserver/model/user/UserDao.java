package com.ergo.Springserver.model.user;

import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDao implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);

    }
    public void delete(User user) {
        userRepository.delete(user);
    }

    public List<User> findAll() {
        return (List<User>)userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid username."));
    }

    public User findById(Long id) {
        return userRepository.findById(id) .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // Retrieve list of friends for a specific user by ID
    public List<User> getFriends(Long userId) {

        if (userId == null) {
            // Handle the case where userId is null, maybe log it or return an empty list
            return new ArrayList<>();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Get friends where the user is the source
        List<User> directFriends = new ArrayList<>(user.getFriends());

        // Convert Iterable<User> to List<User>
        List<User> allUsers = new ArrayList<>();
        userRepository.findAll().forEach(allUsers::add);

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found with id: " + friendId));

        // Check if the friendship already exists for both users
        if (!user.getFriends().contains(friend) && !friend.getFriends().contains(user)) {
            user.getFriends().add(friend);
            friend.getFriends().add(user); // Ensure the friend also has the user as a friend

            // Save both users to persist the bidirectional relationship
            userRepository.save(user);
            userRepository.save(friend);
        }
    }


    public void removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found with id: " + friendId));

        // Check if they are friends
        if (user.getFriends().contains(friend) && friend.getFriends().contains(user)) {
            // Remove the friend from both users' friend lists
            user.getFriends().remove(friend);
            friend.getFriends().remove(user);

            // Save both users to persist the changes
            userRepository.save(user);
            userRepository.save(friend);
        } else {
            throw new RuntimeException("No such friendship exists between users");
        }
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        log.info("Loaded user with password: {}", user.getPassword()); // Verify the password
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

}
