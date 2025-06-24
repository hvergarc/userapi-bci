package com.bci.userapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class UserRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email(message = "El formato del correo no es válido")
    private String email;

    @NotBlank
    private String password;

    private List<PhoneRequest> phones;
}
