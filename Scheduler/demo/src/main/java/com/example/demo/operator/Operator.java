package com.example.demo.operator;

import com.example.demo.appointment.SlotTiming;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter @Setter
public class Operator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String name;

    private Customer currentCustomerServicing; // has-a relation

    @Enumerated(EnumType.STRING)
    private OperatorStatus status;

    private List<SlotTiming> bookedSlotTimings;




}
