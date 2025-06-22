package com.hastane.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MedicalResultDTO {
    private Long id;
    private Long appointmentId;
    private String resultType;
    private String resultDescription;
    private java.time.LocalDateTime createdAt;
} 