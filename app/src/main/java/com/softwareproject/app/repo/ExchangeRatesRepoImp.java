package com.softwareproject.app.repo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    private FileReaderRepo fileReaderRepo;
    final String apiUrl = "https://currency-conversion-and-exchange-rates.p.rapidapi.com/latest";
    private final String apiKey = "6d62cefe2fmshafd5ba4a870d13ap10058ejsnbc0c9a2a4920";
    private final String apiHost = "currency-conversion-and-exchange-rates.p.rapidapi.com";
    private final String apiForCountryNameWithSymbols = "https://currency-conversion-and-exchange-rates.p.rapidapi.com/symbols";




    @Override
    public ExchangeRate findByBase(String code) {

        if (code == null) {
            return null;
        }

        if(code == ""){
            return null;
        }

        try {
            return jdbcTemplate.query("SELECT * FROM exchange_rates WHERE code = ?",
                    BeanPropertyRowMapper.newInstance(ExchangeRate.class), code).get(0);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }


    // 


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
        String currentDirectory = System.getProperty("user.dir");
        File rootDirectory = new File(currentDirectory);
        String csvFileName = rootDirectory + File.separator + "exchange_rates.csv";
        try (FileWriter writer = new FileWriter(csvFileName, false)) {
            // Append Empty text to clear if exist
            writer.append("");
        }catch(Exception e){
            System.out.println(e);
        }
        ratesNode.fields().forEachRemaining(entry -> {
            String code = entry.getKey();
            double rate = entry.getValue().asDouble();

            ExchangeRate newExchangeRate = new ExchangeRate();
            newExchangeRate.setBase(baseCurrency);
            newExchangeRate.setCode(code);
            newExchangeRate.setRate(rate);

            // Save the new exchange rate to the database
            saveExchangeRateToDatabase(newExchangeRate);
            // Get the current working directory
        try (FileWriter writer = new FileWriter(csvFileName, true)) {
            // Append data to the CSV file
            writer.append(baseCurrency);
            writer.append(",");
            writer.append(code);
            writer.append(",");
            writer.append(String.valueOf(rate));
            writer.append("\n");
        }catch(Exception e){
            System.out.println(e);
        }
        });
        return new ResponseEntity<String>("Exchange rates updated successfully", HttpStatus.OK);
    }

    public void saveExchangeRateToDatabase(ExchangeRate exchangeRate) {
        jdbcTemplate.update("INSERT INTO exchange_rates (base, code, rate) VALUES (?, ?, ?)",
                exchangeRate.getBase(), exchangeRate.getCode(), exchangeRate.getRate());
    }

    @Override
    public ResponseEntity<Double> convert(String from, String to, double amount) {

        
        if(from == null || to == null){
            return new ResponseEntity<Double>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if (from.equals(to)) {
            return new ResponseEntity<Double>(amount, HttpStatus.OK);
        }

        if (amount < 0) {
            return new ResponseEntity<Double>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            ExchangeRate fromRate = findByBase(from);
            System.out.println(fromRate.getRate());
            ExchangeRate toRate = findByBase(to);
            System.out.println(toRate.getRate());
            double result = amount * toRate.getRate() / fromRate.getRate();

            return new ResponseEntity<Double>(result, HttpStatus.OK);
        } catch (Exception e) {
            // TODO: handle exception
            
            return new ResponseEntity<Double>(0.0, HttpStatus.INTERNAL_SERVER_ERROR);
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


    @Override
    public ExchangeRate findByCodeFromCSVFile(String code) {
        String currentDirectory = System.getProperty("user.dir");
        String csvFileName = currentDirectory + File.separator + "exchange_rates.csv";
        String splitBy = ",";

        try {
            List<String> lines = fileReaderRepo.readLines(csvFileName);
            for (String line : lines) {
                String[] exchangeRateData = line.split(splitBy);
                if (exchangeRateData.length >= 3 && exchangeRateData[1].equals(code)) {
                    String base = exchangeRateData[0];
                    double rate = Double.parseDouble(exchangeRateData[2]);
                    return new ExchangeRate(base, code, rate);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
