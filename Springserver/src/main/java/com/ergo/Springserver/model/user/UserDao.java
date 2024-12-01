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

    /**
     * Saves a user to the repository.
     * @param user the user to save
     * @return the saved user
     */
    public User save(User user) {
        return userRepository.save(user);

    }

    /**
     * Deletes a specific user from the repository.
     * @param user the user to delete
     */
    public void delete(User user) {
        userRepository.delete(user);
    }

    /**
     * Retrieves all users from the repository.
     * @return a list of all users
     */
    public List<User> findAll() {
        return (List<User>)userRepository.findAll();
    }

    /**
     * Finds a user by username.
     * @param username the username of the user
     * @return the user with the given username
     * @throws IllegalArgumentException if the username is not found
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid username."));
    }

    /**
     * Finds a user by their ID.
     * @param id the ID of the user
     * @return the user with the given ID
     * @throws RuntimeException if the user is not found
     */
    public User findById(Long id) {
        return userRepository.findById(id) .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Retrieves a list of all friends for a specific user.
     * @param userId the ID of the user
     * @return a list of the user's friends
     * @throws RuntimeException if the user is not found
     */
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


    /**
     * Adds a friend to a specific user.
     * @param userId the ID of the user
     * @param friendId the ID of the friend
     * @throws RuntimeException if the user or friend is not found or if the friendship already exists
     */
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

    /**
     * Removes a friend from a specific user's friend list.
     * @param userId the ID of the user
     * @param friendId the ID of the friend to remove
     * @throws RuntimeException if the user or friend is not found or if no friendship exists
     */
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


    /**
     * Loads a user by their username for authentication.
     * @param username the username of the user
     * @return the user details for the specified username
     * @throws UsernameNotFoundException if the username is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

}
