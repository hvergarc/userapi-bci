package com.bci.userapi.service;

import com.bci.userapi.config.EmailPolicyConfig;
import com.bci.userapi.config.PasswordPolicyConfig;
import com.bci.userapi.dto.PhoneRequest;
import com.bci.userapi.dto.UserRequest;
import com.bci.userapi.dto.UserResponse;
import com.bci.userapi.entity.User;
import com.bci.userapi.repository.UserRepository;
import com.bci.userapi.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private PasswordPolicyConfig passwordPolicyConfig;
    private EmailPolicyConfig emailPolicyConfig;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtUtil = mock(JwtUtil.class);
        passwordPolicyConfig = mock(PasswordPolicyConfig.class);
        emailPolicyConfig = mock(EmailPolicyConfig.class);

        userService = new UserService(jwtUtil, userRepository, passwordPolicyConfig, emailPolicyConfig);

        when(passwordPolicyConfig.getPasswordRegex())
            .thenReturn("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");
        when(emailPolicyConfig.getEmailRegex())
            .thenReturn("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    @Test
    void testRegisterUser_Successful() {
        UserRequest request = new UserRequest();
        request.setName("Juan");
        request.setEmail("juan@mail.com");
        request.setPassword("Hunter2024#");

        PhoneRequest phone = new PhoneRequest();
        phone.setNumber("123");
        phone.setCitycode("1");
        phone.setContrycode("56");
        request.setPhones(List.of(phone));

        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.empty());
        when(jwtUtil.generateToken("juan@mail.com")).thenReturn("mocked-token");
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(UUID.randomUUID());
            u.setActive(true);
            return u;
        });

        UserResponse response = userService.registerUser(request);

        assertNotNull(response.getId());
        assertEquals("mocked-token", response.getToken());
        assertTrue(response.isActive());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        UserRequest request = new UserRequest();
        request.setEmail("exists@mail.com");
        request.setPassword("Hunter2024#");
        request.setName("Usuario");

        when(userRepository.findByEmail("exists@mail.com"))
            .thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userService.registerUser(request));

        assertEquals("El correo ya registrado", ex.getMessage());
    }

    @Test
    void testRegisterUser_InvalidPassword() {
        UserRequest request = new UserRequest();
        request.setEmail("nuevo@mail.com");
        request.setPassword("simple"); // no cumple regex
        request.setName("Usuario");

        when(userRepository.findByEmail("nuevo@mail.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userService.registerUser(request));

        assertEquals("La contraseña no cumple con el formato requerido.", ex.getMessage());
    }

    @Test
    void testRegisterUser_InvalidEmail() {
        UserRequest request = new UserRequest();
        request.setEmail("sinformato");
        request.setPassword("Hunter2024#");
        request.setName("Usuario");

        when(userRepository.findByEmail("sinformato")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userService.registerUser(request));

        assertEquals("El correo no tiene un formato válido.", ex.getMessage());
    }
}
