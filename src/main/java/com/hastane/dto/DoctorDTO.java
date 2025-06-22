package com.hastane.dto;

import lombok.Data;

@Data
public class DoctorDTO {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String specialty;
    private String licenseNumber;
} 