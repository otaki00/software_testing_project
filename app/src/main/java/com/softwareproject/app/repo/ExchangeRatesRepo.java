package com.softwareproject.app.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.softwareproject.app.entities.ExchangeRate;

public interface ExchangeRatesRepo {

    ExchangeRate findByBase(String base);

    void save() throws JsonMappingException, JsonProcessingException;

    double convert(String from, String to, double amount);

    List<String> getAllCodes();
    
}
