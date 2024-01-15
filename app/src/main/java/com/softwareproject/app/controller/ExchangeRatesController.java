package com.softwareproject.app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.softwareproject.app.repo.ExchangeRatesRepo;
import com.softwareproject.app.repo.ExchangeRatesRepoImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1")
public class ExchangeRatesController {
    
    @Autowired
    private ExchangeRatesRepoImp exchangeRatesRepoImp;

    @RequestMapping(value = "/convert", method = RequestMethod.GET)
    public double convert(@RequestParam String from, @RequestParam String to, @RequestParam double amount) {
        return exchangeRatesRepoImp.convert(from, to, amount);
    }

    /**
     * @return
     */
    @RequestMapping(value = "/codes", method = RequestMethod.GET)
    public String[] getAllCodes() {
        return exchangeRatesRepoImp.getAllCodes().toArray(new String[0]);
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public ResponseEntity<String> update() {
        try {
            exchangeRatesRepoImp.save();
            return new ResponseEntity<>("Exchange rates updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating exchange rates", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
