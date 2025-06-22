package com.hastane.service;

import com.hastane.dto.AppointmentDTO;
import com.hastane.dto.MedicalResultDTO;
import com.hastane.dto.PrescriptionDTO;
import com.hastane.model.*;
import com.hastane.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final MedicalResultRepository medicalResultRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final DoctorRepository doctorRepository;

    public List<AppointmentDTO> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::convertToAppointmentDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));
        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Bu randevuya erişim yetkiniz yok");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public List<PrescriptionDTO> getPrescriptionsByPatient(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointments.stream()
                .flatMap(a -> prescriptionRepository.findByAppointmentId(a.getId()).stream())
                .map(this::convertToPrescriptionDTO)
                .collect(Collectors.toList());
    }

    public List<MedicalResultDTO> getResultsByPatient(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointments.stream()
                .flatMap(a -> medicalResultRepository.findByAppointmentId(a.getId()).stream())
                .map(this::convertToMedicalResultDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentDTO createAppointment(Long patientId, Long doctorId, AppointmentDTO dto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setNotes(dto.getNotes());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        Appointment saved = appointmentRepository.save(appointment);
        return convertToAppointmentDTO(saved);
    }

    private AppointmentDTO convertToAppointmentDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setPatientName(appointment.getPatient().getUser().getName() + " " + appointment.getPatient().getUser().getSurname());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorName(appointment.getDoctor().getUser().getName() + " " + appointment.getDoctor().getUser().getSurname());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStatus(appointment.getStatus().toString());
        dto.setNotes(appointment.getNotes());
        return dto;
    }

    private PrescriptionDTO convertToPrescriptionDTO(Prescription prescription) {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId(prescription.getId());
        dto.setAppointmentId(prescription.getAppointment().getId());
        dto.setPrescriptionNumber(prescription.getPrescriptionNumber());
        dto.setDescription(prescription.getDescription());
        dto.setCreatedAt(prescription.getCreatedAt());
        return dto;
    }

    private MedicalResultDTO convertToMedicalResultDTO(MedicalResult result) {
        MedicalResultDTO dto = new MedicalResultDTO();
        dto.setId(result.getId());
        dto.setAppointmentId(result.getAppointment().getId());
        dto.setResultType(result.getResultType());
        dto.setResultDescription(result.getResultDescription());
        dto.setCreatedAt(result.getCreatedAt());
        return dto;
    }
} 