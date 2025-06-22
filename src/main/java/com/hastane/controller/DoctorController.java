package com.hastane.controller;

import com.hastane.dto.AppointmentDTO;
import com.hastane.dto.MedicalResultDTO;
import com.hastane.dto.PrescriptionDTO;
import com.hastane.service.DoctorService;
import com.hastane.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {
    private final DoctorService doctorService;
    private final JwtUtil jwtUtil;

    // Randevularını listele
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDTO>> getDoctorAppointments(HttpServletRequest request) {
        Long doctorId = getUserIdFromRequest(request);
        return ResponseEntity.ok(doctorService.getDoctorAppointments(doctorId));
    }

    // Randevu iptal
    @PostMapping("/appointments/{appointmentId}/cancel")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long appointmentId, HttpServletRequest request) {
        Long doctorId = getUserIdFromRequest(request);
        doctorService.cancelAppointment(appointmentId, doctorId);
        return ResponseEntity.ok().build();
    }

    // Sonuç gir
    @PostMapping("/results")
    public ResponseEntity<MedicalResultDTO> addMedicalResult(@RequestBody MedicalResultDTO dto, HttpServletRequest request) {
        Long doctorId = getUserIdFromRequest(request);
        return ResponseEntity.ok(doctorService.addMedicalResult(dto, doctorId));
    }

    // Reçete gir
    @PostMapping("/prescriptions")
    public ResponseEntity<PrescriptionDTO> addPrescription(@RequestBody PrescriptionDTO dto, HttpServletRequest request) {
        Long doctorId = getUserIdFromRequest(request);
        return ResponseEntity.ok(doctorService.addPrescription(dto, doctorId));
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractUserId(token);
        }
        throw new RuntimeException("Token bulunamadı veya geçersiz");
    }
} 