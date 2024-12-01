package com.ergo.Springserver.controllers;

import com.ergo.Springserver.dto.JwtAuthenticationResponse;
import com.ergo.Springserver.dto.SignInRequest;
import com.ergo.Springserver.dto.SignUpRequest;
import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication-related requests.
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Handles user signup requests.
     *
     * @param request the signup request containing user details.
     * @return JwtAuthenticationResponse with the generated JWT token.
     */
    @PostMapping("/signup")
    public JwtAuthenticationResponse signup(@RequestBody SignUpRequest request) {
        return authenticationService.signup(request);
    }

    /**
     * Handles user signin requests.
     *
     * @param request the signin request containing credentials.
     * @return JwtAuthenticationResponse with the generated JWT token.
     */
    @PostMapping("/signin")
    public JwtAuthenticationResponse signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request);
    }

    /**
     * Retrieves the current authenticated user based on the provided JWT token.
     *
     * @param token the Authorization header containing the JWT token.
     * @return User corresponding to the JWT token.
     */
    @GetMapping("/current")
    public User getCurrentUser(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return authenticationService.getUserFromToken(token);
    }
}
