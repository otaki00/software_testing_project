package com.softwareproject.app.repo;

import java.util.List;
import java.util.Map;

// import org.hibernate.mapping.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.softwareproject.app.entities.ExchangeRate;

public interface ExchangeRatesRepo {

    ExchangeRate findByBase(String code);

    ResponseEntity<String> save() throws JsonMappingException, JsonProcessingException;

    ResponseEntity<Double> convert(String from, String to, double amount);

    ResponseEntity<List<String>> getAllCodes();

    ResponseEntity<Map<String, String>> getCountryNameWithSymbols() throws JsonMappingException, JsonProcessingException;


    ExchangeRate findByCodeFromCSVFile(String code);
}
