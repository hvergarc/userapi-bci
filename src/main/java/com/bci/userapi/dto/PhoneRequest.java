package com.bci.userapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhoneRequest {

    @NotBlank
    private String number;

    @NotBlank
    private String citycode;

    @NotBlank
    private String contrycode;
}
