package com.hastane.repository;

import com.hastane.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecialty(String specialty);
    List<Doctor> findByUserNameContainingIgnoreCaseOrUserSurnameContainingIgnoreCase(String name, String surname);
    Optional<Doctor> findByUserId(Long userId);
} 