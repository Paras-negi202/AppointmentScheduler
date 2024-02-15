package com.example.demo.operator;

import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class OperatorManager {
    @OneToMany(mappedBy = "operatorManager")
    private final List<Operator> operators;

    private final int totalOperators = 3;

    public OperatorManager(){
        this.operators = new ArrayList<>();

        for(int i = 0 ; i<totalOperators ; i++){
            operators.add(new Operator());
        }
    }

}
