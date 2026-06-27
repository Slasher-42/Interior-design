package com.ced.Auth.Security.Service.security;

import com.ced.Auth.Security.Service.domain.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "dGVzdC1vbmx5LWp3dC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RzLWRvLW5vdC11c2U=";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, 300_000L);
    }

    @Test
    void generatesAndParsesAccessTokenRoundTrip() {
        UUID userId = UUID.randomUUID();
        String token = jwtUtil.generateAccessToken(userId, "user@example.com", Role.PROJECT_MANAGER, 60_000L);

        Claims claims = jwtUtil.parseClaims(token);

        assertTrue(jwtUtil.isType(claims, JwtUtil.TYPE_ACCESS));
        assertEquals(userId, jwtUtil.extractUserId(claims));
        assertEquals("PROJECT_MANAGER", jwtUtil.extractRole(claims));
        assertEquals("user@example.com", claims.get("email", String.class));
    }

    @Test
    void generatesMfaChallengeTokenWithDistinctType() {
        UUID userId = UUID.randomUUID();
        String token = jwtUtil.generateMfaChallengeToken(userId);

        Claims claims = jwtUtil.parseClaims(token);

        assertTrue(jwtUtil.isType(claims, JwtUtil.TYPE_MFA_CHALLENGE));
        assertFalse(jwtUtil.isType(claims, JwtUtil.TYPE_ACCESS));
        assertEquals(userId, jwtUtil.extractUserId(claims));
    }
}
