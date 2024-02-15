package com.example.demo.appointment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class SlotTiming {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
