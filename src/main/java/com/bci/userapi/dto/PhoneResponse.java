package com.bci.userapi.dto;

import lombok.Data;

@Data
public class PhoneResponse {
    private String number;
    private String citycode;
    private String contrycode;
}
