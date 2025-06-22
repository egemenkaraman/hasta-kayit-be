package com.hastane.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PrescriptionDTO {
    private Long id;
    private Long appointmentId;
    private String prescriptionNumber;
    private String description;
    private java.time.LocalDateTime createdAt;
} 