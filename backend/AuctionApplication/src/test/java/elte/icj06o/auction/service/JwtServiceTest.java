package elte.icj06o.auction.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class JwtServiceTest {
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private final String username = "test@example.com";

    @BeforeEach
    void setUp() {
        openMocks(this);
        jwtService = new JwtService();
        when(userDetails.getUsername()).thenReturn(username);
    }

    @Test
    void generateToken_WithUserDetails_ReturnsValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtService.extractUsername(token));
    }

    @Test
    void generateToken_WithClaims_ReturnsValidToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtService.generateToken(claims, username);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_WithValidToken_ReturnsTrue() {
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithDifferentUsername_ReturnsFalse() {
        String token = jwtService.generateToken(userDetails);

        UserDetails differentUser = new org.springframework.security.core.userdetails.User(
                "different@example.com",
                "password",
                new ArrayList<>()
        );

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    void extractAllClaims_ValidToken_ReturnsAllClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("custom", "value");
        String token = jwtService.generateToken(claims, username);

        Claims extractedClaims = jwtService.extractAllClaims(token);

        assertNotNull(extractedClaims);
        assertEquals(username, extractedClaims.getSubject());
        assertEquals("value", extractedClaims.get("custom"));
    }
}
