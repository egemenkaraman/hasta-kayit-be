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
public class DoctorService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalResultRepository medicalResultRepository;
    private final PrescriptionRepository prescriptionRepository;

    public List<AppointmentDTO> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::convertToAppointmentDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Bu randevuya erişim yetkiniz yok");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Transactional
    public MedicalResultDTO addMedicalResult(MedicalResultDTO dto, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Bu randevuya erişim yetkiniz yok");
        }
        MedicalResult result = new MedicalResult();
        result.setAppointment(appointment);
        result.setResultType(dto.getResultType());
        result.setResultDescription(dto.getResultDescription());
        MedicalResult saved = medicalResultRepository.saveAndFlush(result);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        dto.setId(saved.getId());
        return dto;
    }

    @Transactional
    public PrescriptionDTO addPrescription(PrescriptionDTO dto, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Bu randevuya erişim yetkiniz yok");
        }
        Prescription prescription = new Prescription();
        prescription.setAppointment(appointment);
        prescription.setPrescriptionNumber(dto.getPrescriptionNumber());
        prescription.setDescription(dto.getDescription());
        Prescription saved = prescriptionRepository.saveAndFlush(prescription);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        dto.setId(saved.getId());
        return dto;
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
} 