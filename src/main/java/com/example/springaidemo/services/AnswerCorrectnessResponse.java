package com.example.springaidemo.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerCorrectnessResponse {
   private String[] FP;
   private String[] FN;
   private String[] TP;


}
