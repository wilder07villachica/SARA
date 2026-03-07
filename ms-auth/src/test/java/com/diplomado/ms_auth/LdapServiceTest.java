package com.diplomado.ms_auth;

import com.diplomado.ms_auth.services.LdapService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LdapServiceTest {

    @Autowired
    private LdapService ldapService;

    @Test
    void shouldAuthenticateSuccessfully() {
        boolean result = ldapService.authenticate(
                "testuser",
                "test123"
        );
        Assertions.assertTrue(result);
    }

    @Test
    void shouldFailAuthenticationWithWrongPassword() {
        boolean result = ldapService.authenticate(
                "testuser",
                "wrongpass");
        Assertions.assertFalse(result);
    }
}
