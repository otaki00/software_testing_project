package com.softwareproject.app.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {

    private int id;
    private String base;
    private String code;
    private double rate;

}
