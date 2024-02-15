package com.example.demo.operator;


import com.example.demo.appointment.SlotTiming;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {
}
