package com.example.one.Controller;

import com.example.one.pojo.SysUser;
import com.example.one.repository.SysUserRepository;
import com.example.one.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginControllerTest {

    @Test
    void registrationStoresBcryptPasswordAndWaitsForApproval() {
        SysUserRepository repository = mock(SysUserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        when(repository.findByUsername("new-user")).thenReturn(null);
        when(passwordEncoder.encode("secret1")).thenReturn("$2b$encoded");

        LoginController controller = controller(repository, passwordEncoder, jwtService);
        Map<String, Object> result = controller.register(" new-user ", "secret1");

        assertTrue((Boolean) result.get("success"));
        org.mockito.ArgumentCaptor<SysUser> captor = org.mockito.ArgumentCaptor.forClass(SysUser.class);
        verify(repository).save(captor.capture());
        SysUser saved = captor.getValue();
        assertEquals(0, saved.getStatus());
        assertEquals("$2b$encoded", saved.getPassword());
    }

    @Test
    void successfulLoginReturnsJwt() {
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUsername("tester");
        user.setPassword("$2b$encoded");
        user.setStatus(1);

        SysUserRepository repository = mock(SysUserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        when(repository.findByUsername("tester")).thenReturn(user);
        when(passwordEncoder.matches("secret1", user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.getExpirationSeconds()).thenReturn(7200L);

        Map<String, Object> result = controller(repository, passwordEncoder, jwtService)
                .login("tester", "secret1");

        assertTrue((Boolean) result.get("success"));
        assertEquals("jwt-token", result.get("token"));
        assertEquals("Bearer", result.get("tokenType"));
    }

    private LoginController controller(SysUserRepository repository,
                                       PasswordEncoder passwordEncoder,
                                       JwtService jwtService) {
        LoginController controller = new LoginController();
        ReflectionTestUtils.setField(controller, "sysUserRepository", repository);
        ReflectionTestUtils.setField(controller, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(controller, "jwtService", jwtService);
        return controller;
    }
}
