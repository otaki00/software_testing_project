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

    public ExchangeRate(String base, String code, double rate) {
        this.base = base;
        this.code = code;
        this.rate = rate;
    }

    public ExchangeRate(String code, double rate) {
        this.base = "EUR";
        this.code = code;
        this.rate = rate;
    }
}
