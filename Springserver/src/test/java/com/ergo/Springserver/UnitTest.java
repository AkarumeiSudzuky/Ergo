package com.ergo.Springserver;

import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.service.JwtService;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UnitTest {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Test
    public void getTokenFromUserData() {
        User newUser = new User(null, "Ergo", "Ergo", "Ergo@gmail.com");
        String token = jwtService.generateToken(newUser);
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
        String decodedUsername = Jwts.parser()
                .setSigningKey("58a868a4042f634lp04a117f00a87202131dd7c46c4b32c4acb3edc5e15f4511")
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        assertEquals(decodedUsername, "Ergo", "Username should be Ergo");
    }

}
