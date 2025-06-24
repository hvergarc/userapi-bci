package com.bci.userapi.service;

import com.bci.userapi.config.EmailPolicyConfig;
import com.bci.userapi.config.PasswordPolicyConfig;
import com.bci.userapi.dto.*;
import com.bci.userapi.entity.Phone;
import com.bci.userapi.entity.User;
import com.bci.userapi.repository.UserRepository;
import com.bci.userapi.security.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

	private final JwtUtil jwtUtil;
	
    private final UserRepository userRepository;

    private final PasswordPolicyConfig passwordPolicyConfig;

    private final EmailPolicyConfig emailPolicyConfig;

    @Transactional
    public UserResponse registerUser(UserRequest request) {
    	
    	if (!Pattern.matches(emailPolicyConfig.getEmailRegex(), request.getEmail())) {
    	    throw new RuntimeException("El correo no tiene un formato válido.");
    	}

        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new RuntimeException("El correo ya registrado");
        });

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        
        
        System.out.println("Regex configurado: " + passwordPolicyConfig.getPasswordRegex());
        System.out.println("Clave recibida: " + request.getPassword());

        
        if (!Pattern.matches(passwordPolicyConfig.getPasswordRegex(), request.getPassword())) {
            throw new RuntimeException("La contraseña no cumple con el formato requerido.");
        }

        user.setPassword(request.getPassword());
        user.setToken(jwtUtil.generateToken(user.getEmail()));

        List<Phone> phones = request.getPhones().stream().map(phoneReq -> {
            Phone phone = new Phone();
            phone.setNumber(phoneReq.getNumber());
            phone.setCitycode(phoneReq.getCitycode());
            phone.setContrycode(phoneReq.getContrycode());
            phone.setUser(user);
            return phone;
        }).collect(Collectors.toList());

        user.setPhones(phones);

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setCreated(user.getCreated());
        response.setModified(user.getModified());
        response.setLastLogin(user.getLastLogin());
        response.setToken(user.getToken());
        response.setActive(user.isActive());

        List<PhoneResponse> phones = user.getPhones().stream().map(p -> {
            PhoneResponse pr = new PhoneResponse();
            pr.setNumber(p.getNumber());
            pr.setCitycode(p.getCitycode());
            pr.setContrycode(p.getContrycode());
            return pr;
        }).collect(Collectors.toList());

        response.setPhones(phones);
        return response;
    }
}
