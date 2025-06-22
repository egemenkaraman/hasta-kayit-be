package com.hastane.service;

import com.hastane.dto.PatientDTO;
import com.hastane.dto.AppointmentDTO;
import com.hastane.dto.DoctorDTO;
import com.hastane.model.*;
import com.hastane.repository.AppointmentRepository;
import com.hastane.repository.PatientRepository;
import com.hastane.repository.UserRepository;
import com.hastane.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PatientDTO createPatient(PatientDTO patientDTO) {
        User user = new User();
        user.setUsername(patientDTO.getUsername());
        user.setPassword(passwordEncoder.encode(patientDTO.getPassword()));
        user.setRole(UserRole.PATIENT);
        user.setName(patientDTO.getName());
        user.setSurname(patientDTO.getSurname());
        user.setEmail(patientDTO.getEmail());
        user.setPhone(patientDTO.getPhone());
        
        User savedUser = userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(savedUser);
        patient.setTcNo(patientDTO.getTcNo());
        patient.setBirthDate(patientDTO.getBirthDate());
        patient.setBloodType(patientDTO.getBloodType());
        patient.setAddress(patientDTO.getAddress());

        Patient savedPatient = patientRepository.save(patient);
        return convertToDTO(savedPatient);
    }

    @Transactional
    public void deletePatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));
        
        User user = patient.getUser();
        patientRepository.delete(patient);
        userRepository.delete(user);
    }

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public PatientDTO getPatientByTcNo(String tcNo) {
        return patientRepository.findByTcNo(tcNo)
            .map(this::convertToDTO)
            .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));
    }

    public List<AppointmentDTO> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
            .map(this::convertToAppointmentDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAppointment(Long appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }

    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
            .map(this::convertToDoctorDTO)
            .collect(Collectors.toList());
    }

    public List<DoctorDTO> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty).stream()
            .map(this::convertToDoctorDTO)
            .collect(Collectors.toList());
    }

    public List<DoctorDTO> searchDoctors(String searchTerm) {
        return doctorRepository.findByUserNameContainingIgnoreCaseOrUserSurnameContainingIgnoreCase(
                searchTerm, searchTerm).stream()
            .map(this::convertToDoctorDTO)
            .collect(Collectors.toList());
    }

    public List<PatientDTO> searchPatients(String searchTerm) {
        return patientRepository.findByUserNameContainingIgnoreCaseOrUserSurnameContainingIgnoreCase(
                searchTerm, searchTerm).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
        Patient patient = patientRepository.findById(appointmentDTO.getPatientId())
            .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));
        
        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId())
            .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointment.setNotes(appointmentDTO.getNotes());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToAppointmentDTO(savedAppointment);
    }

    @Transactional
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        User user = new User();
        user.setUsername(doctorDTO.getUsername());
        user.setPassword(passwordEncoder.encode(doctorDTO.getPassword() != null ? doctorDTO.getPassword() : "defaultPassword"));
        user.setRole(UserRole.DOCTOR);
        user.setName(doctorDTO.getName());
        user.setSurname(doctorDTO.getSurname());
        user.setEmail(doctorDTO.getEmail());
        user.setPhone(doctorDTO.getPhone());
        User savedUser = userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setSpecialty(doctorDTO.getSpecialty());
        doctor.setLicenseNumber(doctorDTO.getLicenseNumber());
        Doctor savedDoctor = doctorRepository.save(doctor);
        return convertToDoctorDTO(savedDoctor);
    }

    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setUsername(patient.getUser().getUsername());
        dto.setName(patient.getUser().getName());
        dto.setSurname(patient.getUser().getSurname());
        dto.setEmail(patient.getUser().getEmail());
        dto.setPhone(patient.getUser().getPhone());
        dto.setTcNo(patient.getTcNo());
        dto.setBirthDate(patient.getBirthDate());
        dto.setBloodType(patient.getBloodType());
        dto.setAddress(patient.getAddress());
        return dto;
    }

    private AppointmentDTO convertToAppointmentDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setPatientName(appointment.getPatient().getUser().getName() + " " + 
                          appointment.getPatient().getUser().getSurname());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorName(appointment.getDoctor().getUser().getName() + " " + 
                         appointment.getDoctor().getUser().getSurname());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStatus(appointment.getStatus().toString());
        dto.setNotes(appointment.getNotes());
        return dto;
    }

    private DoctorDTO convertToDoctorDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setName(doctor.getUser().getName());
        dto.setSurname(doctor.getUser().getSurname());
        dto.setEmail(doctor.getUser().getEmail());
        dto.setPhone(doctor.getUser().getPhone());
        dto.setSpecialty(doctor.getSpecialty());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        return dto;
    }
} 