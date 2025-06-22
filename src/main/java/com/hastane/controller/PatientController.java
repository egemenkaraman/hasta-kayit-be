package com.hastane.controller;

import com.hastane.dto.AppointmentDTO;
import com.hastane.dto.MedicalResultDTO;
import com.hastane.dto.PrescriptionDTO;
import com.hastane.service.PatientService;
import com.hastane.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {
    private final PatientService patientService;
    private final JwtUtil jwtUtil;

    // Kendi randevularını listele
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDTO>> getPatientAppointments(HttpServletRequest request) {
        Long patientId = getUserIdFromRequest(request);
        return ResponseEntity.ok(patientService.getPatientAppointments(patientId));
    }

    // Kendi randevusunu iptal et
    @PostMapping("/appointments/{appointmentId}/cancel")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long appointmentId, HttpServletRequest request) {
        Long patientId = getUserIdFromRequest(request);
        patientService.cancelAppointment(appointmentId, patientId);
        return ResponseEntity.ok().build();
    }

    // Kendi reçetelerini gör
    @GetMapping("/prescriptions")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptions(HttpServletRequest request) {
        Long patientId = getUserIdFromRequest(request);
        return ResponseEntity.ok(patientService.getPrescriptionsByPatient(patientId));
    }

    // Kendi sonuçlarını gör
    @GetMapping("/results")
    public ResponseEntity<List<MedicalResultDTO>> getResults(HttpServletRequest request) {
        Long patientId = getUserIdFromRequest(request);
        return ResponseEntity.ok(patientService.getResultsByPatient(patientId));
    }

    // Randevu al
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestParam Long doctorId, @RequestBody AppointmentDTO dto, HttpServletRequest request) {
        Long patientId = getUserIdFromRequest(request);
        return ResponseEntity.ok(patientService.createAppointment(patientId, doctorId, dto));
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