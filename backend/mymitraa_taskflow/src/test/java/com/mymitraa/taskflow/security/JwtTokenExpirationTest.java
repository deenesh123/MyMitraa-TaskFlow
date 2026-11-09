package com.mymitraa.taskflow.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenExpirationTest {

    private JwtTokenUtil jwtTokenUtil;
    private static final String TEST_SECRET = "dGhpc2lzYXZlcnlzZWN1cmVqd3RzZWNyZXRmb3JteW1pdHJhYXRhc2tmbG93YXBwbGljYXRpb24yMDI2ZGV2";
    private static final long TWENTY_FOUR_HOURS_MS = 86400000L;

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenUtil, "expirationMs", TWENTY_FOUR_HOURS_MS);
    }

    @Test
    @DisplayName("JWT-01: Expiration calculation standardizes exactly on 24 hours (86,400,000 milliseconds)")
    void testStandardizedExpirationMilliseconds() {
        assertEquals(86400000L, jwtTokenUtil.getExpirationMs(), "Expiration must represent exactly 86,400,000 milliseconds (24 hours)");
    }

    @Test
    @DisplayName("JWT-02: Generated token expiration date matches issued time plus 24 hours")
    void testGeneratedTokenExpirationWindow() {
        long beforeTime = System.currentTimeMillis();
        String token = jwtTokenUtil.generateToken("deenesh123");
        long afterTime = System.currentTimeMillis();

        assertNotNull(token, "Generated JWT must not be null");
        assertFalse(token.trim().isEmpty(), "Generated JWT must not be empty");

        String username = jwtTokenUtil.getUsernameFromToken(token);
        assertEquals("deenesh123", username, "Extracted username must match token subject");

        Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);
        assertNotNull(expirationDate, "Expiration date must be present in signed token claims");

        long expectedMinExpiry = beforeTime + TWENTY_FOUR_HOURS_MS;
        long expectedMaxExpiry = afterTime + TWENTY_FOUR_HOURS_MS;

        assertTrue(expirationDate.getTime() >= expectedMinExpiry, "Expiration date must be >= issued + 24 hours");
        assertTrue(expirationDate.getTime() <= expectedMaxExpiry, "Expiration date must be <= issued + 24 hours");
    }

    @Test
    @DisplayName("JWT-03: Freshly generated token is valid and not expired")
    void testTokenValidationSuccess() {
        String token = jwtTokenUtil.generateToken("security-auditor");
        assertTrue(jwtTokenUtil.validateToken(token, "security-auditor"), "Valid token must pass validation");
        assertFalse(jwtTokenUtil.validateToken(token, "wrong-user"), "Token validated against mismatched username must fail");
    }

    @Test
    @DisplayName("JWT-04: Expired token (negative/zero expiration) fails validation")
    void testExpiredTokenValidationFailure() {
        // Configure an already-expired token (-5000 ms)
        ReflectionTestUtils.setField(jwtTokenUtil, "expirationMs", -5000L);
        String expiredToken = jwtTokenUtil.generateToken("expired-user");

        assertFalse(jwtTokenUtil.validateToken(expiredToken, "expired-user"), "Expired token must be rejected by validateToken");
    }
}
