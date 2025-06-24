package com.bci.userapi.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class EmailPolicyConfig {

    @Value("${user.email.regex}")
    private String emailRegex;
}
