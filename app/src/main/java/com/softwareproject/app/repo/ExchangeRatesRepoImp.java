package com.softwareproject.app.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
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

    @Override
    public ExchangeRate findByBase(String base) {
        return jdbcTemplate.queryForObject("SELECT * FROM exchange_rates WHERE base = ?", new Object[] { base }, ExchangeRate.class);
    }


    @Override
    public void save() throws JsonMappingException, JsonProcessingException {
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
    }

    private void saveExchangeRateToDatabase(ExchangeRate exchangeRate) {
        jdbcTemplate.update("INSERT INTO exchange_rates (base, code, rate) VALUES (?, ?, ?)",
                exchangeRate.getBase(), exchangeRate.getCode(), exchangeRate.getRate());
    }

    @Override
    public double convert(String from, String to, double amount) {
        ExchangeRate fromRate = findByBase(from);
        ExchangeRate toRate = findByBase(to);
        return amount * fromRate.getRate() / toRate.getRate();

    }

    @Override
    public List<String> getAllCodes() {
        
        return jdbcTemplate.queryForList("SELECT code FROM exchange_rates", String.class);
    }
    
}
