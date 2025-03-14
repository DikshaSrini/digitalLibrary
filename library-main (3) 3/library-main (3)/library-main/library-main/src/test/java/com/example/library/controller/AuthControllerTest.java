package com.example.library.controller;

import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        User user = new User("testUser", "password", "USER");
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        String result = authController.registerUser(user, redirectAttributes);

        assertEquals("redirect:/login", result);
        verify(userRepository, times(1)).save(user);
        verify(passwordEncoder, times(1)).encode("password");
    }
}
