package com.hastane.repository;

import com.hastane.model.MedicalResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalResultRepository extends JpaRepository<MedicalResult, Long> {
    List<MedicalResult> findByAppointmentId(Long appointmentId);
} 