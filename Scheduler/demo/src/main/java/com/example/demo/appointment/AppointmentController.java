package com.example.demo.appointment;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService){
        this.appointmentService = appointmentService;
    }

    @PutMapping("/reschedule/{appointmentId}")
    public Appointment rescheduleAppointment(
            @PathVariable Long appointmentId,
            @RequestParam LocalDateTime newStartTime
    ) {
        return appointmentService.rescheduleAppointment(appointmentId, newStartTime);
    }


    @PostMapping
    public Appointment bookSlot(@RequestBody SlotTiming slotTiming){
        return appointmentService.bookSlot(slotTiming);
    }

    @DeleteMapping("/cancel/{appointmentId}")
    public void cancelAppointment(@PathVariable Long appointmentId) {
        appointmentService.cancelAppointment(appointmentId);

    }
}
