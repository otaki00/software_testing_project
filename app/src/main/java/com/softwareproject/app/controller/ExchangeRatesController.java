package com.softwareproject.app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.softwareproject.app.repo.ExchangeRatesRepoImp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
public class ExchangeRatesController {

    @Autowired
    private ExchangeRatesRepoImp exchangeRatesRepoImp;

    // localhost:8080/api/v1/convert?from=USD&to=EUR&amount=100
    @GetMapping(value = "/convert")
    public ResponseEntity<Double> convert(@RequestParam String from, @RequestParam String to,
            @RequestParam double amount) {
        return exchangeRatesRepoImp.convert(from, to, amount);
    }

    /**
     * @return
     */
    // localhost:8080/api/v1/codes
    @GetMapping(value = "/codes")
    public ResponseEntity<List<String>> getAllCodes() {
        return exchangeRatesRepoImp.getAllCodes();
    }

    // localhost:8080/api/v1/update
    @GetMapping(value = "/update")
    public ResponseEntity<String> update() {
        try {
            return exchangeRatesRepoImp.save();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            return new ResponseEntity<String>("{\"message\": \"Error updating exchange rates\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            return new ResponseEntity<String>("{\"message\": \"Error updating exchange rates\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
