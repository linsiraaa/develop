package com.example.one.security;

import com.example.one.pojo.SysUser;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
                "test-jwt-secret-with-at-least-32-bytes-2026");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
        jwtService.init();
    }

    @Test
    void generatedTokenCanBeValidatedAndParsed() {
        SysUser user = new SysUser();
        user.setUserId(7L);
        user.setUsername("tester");

        String token = jwtService.generateToken(user);
        Claims claims = jwtService.parseToken(token);

        assertTrue(jwtService.isValid(token));
        assertEquals("tester", claims.getSubject());
        assertEquals(7L, ((Number) claims.get("userId")).longValue());
    }

    @Test
    void tamperedTokenIsRejected() {
        SysUser user = new SysUser();
        user.setUsername("tester");
        String token = jwtService.generateToken(user);

        assertFalse(jwtService.isValid(token + "tampered"));
    }
}
