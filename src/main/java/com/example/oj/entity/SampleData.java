package com.example.oj.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleData {
    String comment;
    String sampleInput;
    String sampleOutput;

    @Override
    public String toString() {
        return "[" +
                 comment + ',' +
                ", sampleInput='" + sampleInput + ',' +
                ", sampleOutput='" + sampleOutput +
                ']';
    }
}
