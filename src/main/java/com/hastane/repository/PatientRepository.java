package com.hastane.repository;

import com.hastane.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByTcNo(String tcNo);
    Optional<Patient> findByUserUsername(String username);
    List<Patient> findByUserNameContainingIgnoreCaseOrUserSurnameContainingIgnoreCase(String name, String surname);
    Optional<Patient> findByUserId(Long userId);
} 