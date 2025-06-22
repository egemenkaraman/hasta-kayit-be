package com.hastane.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientDTO {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String tcNo;
    private LocalDate birthDate;
    private String bloodType;
    private String address;
} 