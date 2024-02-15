package com.example.demo.operator;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class Agency {

   private Customer customer;
   private OperatorManager operatorManager; // has-a


}
