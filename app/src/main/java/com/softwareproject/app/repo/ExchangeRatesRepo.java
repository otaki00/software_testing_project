package com.softwareproject.app.repo;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.softwareproject.app.entities.ExchangeRate;

public interface ExchangeRatesRepo {

    ExchangeRate findByBase(String code);

    void save() throws JsonMappingException, JsonProcessingException;

    double convert(String from, String to, double amount);

    List<String> getAllCodes();
    
}
