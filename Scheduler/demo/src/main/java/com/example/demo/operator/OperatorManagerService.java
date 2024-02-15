package com.example.demo.operator;

import com.example.demo.appointment.Appointment;
import com.example.demo.appointment.AppointmentRepository;
import com.example.demo.appointment.SlotTiming;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperatorManagerService {

    private final AppointmentRepository appointmentRepository;
    private final OperatorRepository operatorRepository;

    @Autowired
    public OperatorManagerService( AppointmentRepository appointmentRepository, OperatorRepository operatorRepository){
        this.appointmentRepository = appointmentRepository;
        this.operatorRepository = operatorRepository;
    }
    public List<SlotTiming> getFreeSlots(Long operatorId) {

        return freeSlots(operatorId, LocalDateTime.now());
    }

    private List<SlotTiming> freeSlots(Long operatorId, LocalDateTime date) {

        Operator operator = operatorRepository.findById(operatorId)
                .orElseThrow(() -> new EntityNotFoundException("Operator not found"));

        // Get all appointments for the operator on the specified date
        List<Appointment> appointments = appointmentRepository.findByOperatorAndStartTimeBetween(
                operator,
                LocalDateTime.now(),
                date.withHour(23).withMinute(59).withSecond(59)
        );

        // Calculate free slots by finding gaps between appointments
        List<SlotTiming> freeSlots = calculateFreeSlots(appointments, date);

        // Merge overlapping free slots
        return mergeOverlappingSlots(freeSlots);
    }

    private List<SlotTiming> calculateFreeSlots(List<Appointment> appointments, LocalDateTime date) {
        // Sort appointments by start time
        appointments.sort(Comparator.comparing(a -> a.getSlotTiming().getStartTime()));

        // Calculate free slots by finding gaps between appointments
        List<SlotTiming> freeSlots = appointments.stream()
                .reduce(
                        // Seed with the first free slot starting from midnight
                        List.of(new SlotTiming(date.withHour(0).withMinute(0), date.withHour(0).withMinute(0))),
                        (slots, appointment) -> {
                            SlotTiming lastSlot = slots.get(slots.size() - 1);
                            LocalDateTime nextStartTime = lastSlot.getEndTime();
                            LocalDateTime appointmentStartTime = appointment.getSlotTiming().getStartTime();

                            if (nextStartTime.isBefore(appointmentStartTime)) {
                                // Add the gap as a free slot
                                slots.add(new SlotTiming(nextStartTime, appointmentStartTime));
                            }

                            // Add the current appointment as a booked slot
                            slots.add(new SlotTiming(appointmentStartTime, appointment.getSlotTiming().getStartTime()));

                            return slots;
                        },
                        (list1, list2) -> {
                            // Combine the results of parallel streams, if needed
                            list1.addAll(list2);
                            return list1;
                        }
                );

        return freeSlots;
    }

    private List<SlotTiming> mergeOverlappingSlots(List<SlotTiming> slots) {
        // Sort slots by start time
        slots.sort(Comparator.comparing(SlotTiming::getStartTime));

        // Merge overlapping slots
        return slots.stream()
                .reduce(
                        // Seed with the first slot
                        List.of(slots.get(0)),
                        (merged, current) -> {
                            SlotTiming lastMerged = merged.get(merged.size() - 1);
                            if (lastMerged.getEndTime().isAfter(current.getStartTime())) {
                                // Merge overlapping slots
                                lastMerged.setEndTime(lastMerged.getEndTime().plus(Duration.between(lastMerged.getEndTime(),
                                        current.getEndTime())));
                            } else {
                                // Add the non-overlapping slot
                                merged.add(current);
                            }
                            return merged;
                        },
                        (list1, list2) -> {
                            // Combine the results of parallel streams, if needed
                            list1.addAll(list2);
                            return list1;
                        }
                );
    }

    public List<SlotTiming> getBookedSlots(Long operatorId) {
        Operator operator = operatorRepository.findById(operatorId)
                .orElseThrow(() -> new EntityNotFoundException("Operator not found"));


        List<Appointment> bookedAppointments = appointmentRepository.findByOperator(operator);
        return bookedSlots(bookedAppointments);

    }

    private List<SlotTiming> bookedSlots(List<Appointment> bookedAppointments) {

        return bookedAppointments.stream()
                .map(appointment -> new SlotTiming(appointment.getSlotTiming().getStartTime(),
                                                   appointment.getSlotTiming().getEndTime()))
                .collect(Collectors.toList());

    }

    public Appointment bookAppointmentBySpecificOperator(Long operatorId) {
        Operator operator = operatorRepository.findById(operatorId)
                .orElseThrow(() -> new EntityNotFoundException("Operator not found"));


         SlotTiming slotTiming = findLatestFreeSlot(operator);

         Appointment appointment = new Appointment();

         appointment.setOperator(operator);
         appointment.setSlotTiming(slotTiming);
         appointmentRepository.save(appointment);
         return appointment;
    }

    private SlotTiming findLatestFreeSlot(Operator operator) {
        List<Appointment> appointments = appointmentRepository.findByOperator(operator);

        // Sort appointments by end time in ascending order
        appointments.sort(Comparator.comparing(a -> a.getSlotTiming().getEndTime()));

        // Find the latest free slot
        LocalDateTime currentDateTime = LocalDateTime.now();
        SlotTiming slotTiming = new SlotTiming();


        Duration duration = Duration.ofHours(1);

        for (Appointment appointment : appointments) {
            if (appointment.getSlotTiming().getEndTime().isBefore(currentDateTime.minusHours(1))) {
                // The appointment has already ended, check if there's a free slot after it
                slotTiming.setStartTime(appointment.getSlotTiming().getEndTime());
                slotTiming.setEndTime(slotTiming.getStartTime().plusHours(1));
                return slotTiming;
            } else if (appointment.getSlotTiming().getStartTime().isBefore(currentDateTime)) {
                // The appointment is ongoing, move to the next appointment

                continue;
            } else {
                // The appointment is in the future, check if there's a free slot before it
                LocalDateTime latestFreeSlot = appointment.getSlotTiming().getStartTime().minusMinutes(1);
                if (Duration.between(currentDateTime, latestFreeSlot).toMinutes() >= duration.toMinutes()) {
                    slotTiming.setStartTime(currentDateTime);
                    slotTiming.setEndTime(slotTiming.getStartTime().plusHours(1));
                    return slotTiming;
                }
            }
        }

        return null;
    }

}
