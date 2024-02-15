package com.example.demo.appointment;


import com.example.demo.operator.Operator;
import com.example.demo.operator.OperatorManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private static final OperatorManager operatorManager = new OperatorManager();
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository){
        this.appointmentRepository = appointmentRepository;
    }


    public Appointment rescheduleAppointment(Long appointmentId, LocalDateTime newStartTime) {
        Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        existingAppointment.getSlotTiming().setStartTime(newStartTime);

        return appointmentRepository.save(existingAppointment);
    }

    public Appointment bookSlot(SlotTiming slotTiming) {

        for(Operator operator : operatorManager.getOperators()){
            List<Appointment> existingAppointments = appointmentRepository
                    .findByOperatorAndStartTimeBetween(operator,
                            slotTiming.getStartTime(), slotTiming.getEndTime());

            boolean isTimeSlotAvailable = existingAppointments.stream()
                    .noneMatch(existingAppointment ->
                            isTimeSlotOverlap(slotTiming, existingAppointment.getSlotTiming()));

            if(isTimeSlotAvailable){
                // Create a new appointment with the provided slot timing
                Appointment newAppointment = new Appointment();
                newAppointment.setOperator(operator);
                newAppointment.setSlotTiming(slotTiming);

                return appointmentRepository.save(newAppointment);

            }
        }

        return null;

    }

    private boolean isTimeSlotOverlap(SlotTiming newSlot, SlotTiming existingSlot) {
        return newSlot.getStartTime().isBefore(existingSlot.getEndTime()) &&
                newSlot.getEndTime().isAfter(existingSlot.getStartTime());
    }

    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        appointmentRepository.cancelAppointmentById(appointmentId);
        appointmentRepository.save(appointment);
    }
}
