package com.example.demo.operator;


import com.example.demo.appointment.Appointment;
import com.example.demo.appointment.SlotTiming;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/operator")
public class OperatorManagerController {

    private final OperatorManagerService operatorManagerService;

    @Autowired
    public OperatorManagerController(OperatorManagerService operatorManagerService){
        this.operatorManagerService = operatorManagerService;
    }

    @GetMapping("/operator/{operatorId}")
    public List<SlotTiming> getFreeSlots(@PathVariable Long operatorId){
        return operatorManagerService.getFreeSlots(operatorId);
    }

    @GetMapping("/operator/{operatorId}")
    public List<SlotTiming> getBookedSlots(@PathVariable Long operatorId){
        return operatorManagerService.getBookedSlots(operatorId);
    }

    @PostMapping("/book/{operatorId}")
    public Appointment bookAppointmentBySpecificOperator(@PathVariable Long operatorId){
         return operatorManagerService.bookAppointmentBySpecificOperator(operatorId);
    }

}
