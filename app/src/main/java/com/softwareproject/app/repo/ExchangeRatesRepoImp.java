package com.softwareproject.app.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwareproject.app.entities.ExchangeRate;

@Repository
public class ExchangeRatesRepoImp implements ExchangeRatesRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    final String apiUrl = "https://currency-conversion-and-exchange-rates.p.rapidapi.com/latest";
    private final String apiKey = "6783067dd6mshf3f2a37f6be4441p11d94cjsnb59052523375";
    private final String apiHost = "currency-conversion-and-exchange-rates.p.rapidapi.com";
    private final String apiForCountryNameWithSymbols = "https://currency-conversion-and-exchange-rates.p.rapidapi.com/symbols";

    @Override
    public ExchangeRate findByBase(String code) {
        try {
            return jdbcTemplate.query("SELECT * FROM exchange_rates WHERE code = ?",
                    BeanPropertyRowMapper.newInstance(ExchangeRate.class), code).get(0);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    @Override
    public ResponseEntity<String> save() throws JsonMappingException, JsonProcessingException {

        // before saving the new exchange rates, delete all the existing exchange rates
        jdbcTemplate.update("DELETE FROM exchange_rates");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());

        String baseCurrency = rootNode.path("base").asText();
        JsonNode ratesNode = rootNode.path("rates");

        // Iterate through the rates and save each exchange rate to the database
        ratesNode.fields().forEachRemaining(entry -> {
            String code = entry.getKey();
            double rate = entry.getValue().asDouble();

            ExchangeRate newExchangeRate = new ExchangeRate();
            newExchangeRate.setBase(baseCurrency);
            newExchangeRate.setCode(code);
            newExchangeRate.setRate(rate);

            // Save the new exchange rate to the database
            saveExchangeRateToDatabase(newExchangeRate);

        });
        return new ResponseEntity<String>("Exchange rates updated successfully", HttpStatus.OK);
    }

    private void saveExchangeRateToDatabase(ExchangeRate exchangeRate) {
        jdbcTemplate.update("INSERT INTO exchange_rates (base, code, rate) VALUES (?, ?, ?)",
                exchangeRate.getBase(), exchangeRate.getCode(), exchangeRate.getRate());
    }

    @Override
    public ResponseEntity<Double> convert(String from, String to, double amount) {
        try {
            ExchangeRate fromRate = findByBase(from);
            System.out.println(fromRate.getRate());
            ExchangeRate toRate = findByBase(to);
            System.out.println(toRate.getRate());
            double result = amount * toRate.getRate() / fromRate.getRate();

            return new ResponseEntity<Double>(result, HttpStatus.OK);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResponseEntity<Double>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<List<String>> getAllCodes() {

        try {
            List<String> codes = jdbcTemplate.queryForList("SELECT code FROM exchange_rates", String.class);
            return new ResponseEntity<List<String>>(codes, HttpStatus.OK);
        } catch (Exception e) {
            // TODO: handle exception
            return new ResponseEntity<List<String>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<Map<String, String>> getCountryNameWithSymbols() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiForCountryNameWithSymbols, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode;

            try {
                rootNode = objectMapper.readTree(response.getBody());
                if (rootNode.path("success").asBoolean()) {
                    Map<String, String> symbolsMap = new HashMap<>();

                    JsonNode symbolsNode = rootNode.path("symbols");
                    symbolsNode.fields().forEachRemaining(entry -> symbolsMap.put(entry.getKey(), entry.getValue().asText()));

                    return ResponseEntity.ok(symbolsMap);
                } else {
                    // Handle the case where the API call was not successful
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } catch (Exception e) {
                // Handle the exception, e.g., log it
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            // Handle the case where the API call itself failed
            return ResponseEntity.status(response.getStatusCode()).build();
        }
    }

}
