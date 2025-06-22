package com.hastane.controller;

import com.hastane.dto.PatientDTO;
import com.hastane.dto.AppointmentDTO;
import com.hastane.dto.DoctorDTO;
import com.hastane.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STAFF')")
public class StaffController {
    private final StaffService staffService;

    // Patient management endpoints
    @PostMapping("/patients")
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO) {
        return ResponseEntity.ok(staffService.createPatient(patientDTO));
    }

    @DeleteMapping("/patients/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        staffService.deletePatient(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        return ResponseEntity.ok(staffService.getAllPatients());
    }

    @GetMapping("/patients/tc/{tcNo}")
    public ResponseEntity<PatientDTO> getPatientByTcNo(@PathVariable String tcNo) {
        return ResponseEntity.ok(staffService.getPatientByTcNo(tcNo));
    }

    @GetMapping("/patients/search")
    public ResponseEntity<List<PatientDTO>> searchPatients(@RequestParam String searchTerm) {
        return ResponseEntity.ok(staffService.searchPatients(searchTerm));
    }

    // Doctor management endpoints
    @PostMapping("/doctors")
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody DoctorDTO doctorDTO) {
        return ResponseEntity.ok(staffService.createDoctor(doctorDTO));
    }

    @PreAuthorize("hasAnyRole('STAFF','PATIENT')")
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(staffService.getAllDoctors());
    }

    @PreAuthorize("hasAnyRole('STAFF','PATIENT')")
    @GetMapping("/doctors/specialty/{specialty}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialty(@PathVariable String specialty) {
        return ResponseEntity.ok(staffService.getDoctorsBySpecialty(specialty));
    }

    @PreAuthorize("hasAnyRole('STAFF','PATIENT')")
    @GetMapping("/doctors/search")
    public ResponseEntity<List<DoctorDTO>> searchDoctors(@RequestParam String searchTerm) {
        return ResponseEntity.ok(staffService.searchDoctors(searchTerm));
    }

    // Appointment management endpoints
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        return ResponseEntity.ok(staffService.createAppointment(appointmentDTO));
    }

    @GetMapping("/appointments/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getPatientAppointments(@PathVariable Long patientId) {
        return ResponseEntity.ok(staffService.getPatientAppointments(patientId));
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        staffService.deleteAppointment(id);
        return ResponseEntity.ok().build();
    }
} 