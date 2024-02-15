package com.example.demo.appointment;

import com.example.demo.operator.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    List<Appointment> findByOperator(Operator operator);

    List<Appointment> findByOperatorAndStartTimeBetween(Operator operator, LocalDateTime startTime, LocalDateTime endTime);

    @Modifying
    @Query("DELETE FROM Appointment a WHERE a.id = :appointmentId")
    void cancelAppointmentById(@Param("appointmentId") Long appointmentId);

}
