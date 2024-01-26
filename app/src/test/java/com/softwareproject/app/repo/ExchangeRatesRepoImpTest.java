package com.softwareproject.app.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.softwareproject.app.entities.ExchangeRate;

@ExtendWith(MockitoExtension.class)
public class ExchangeRatesRepoImpTest {
    

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private FileReaderRepo fileReaderRepo;

    @InjectMocks
    private ExchangeRatesRepoImp exchangeRatesRepoImp;

    @Test
    public void findByBaseTestShouldReturnNullWhenCodeIsNull() {
        assertNull(exchangeRatesRepoImp.findByBase(null));
    }

    @Test
    public void findByBaseTestShouldReturnNullWhenCodeIsEmpty() {
        assertNull(exchangeRatesRepoImp.findByBase(""));
    }

    @Test
    public void findByBaseTestShouldReturnExchangeRate() {
        String code = "USD";
        ExchangeRate expectedRate = new ExchangeRate();
        when(jdbcTemplate.query(anyString(), any(BeanPropertyRowMapper.class), eq(code)))
                .thenReturn(Collections.singletonList(expectedRate));
        assertEquals(expectedRate, exchangeRatesRepoImp.findByBase(code));
    }

    @Test
    public void findByBaseTestShouldReturnNullWhenNotFound() {
        String code = "USS";
        when(jdbcTemplate.query(anyString(), any(BeanPropertyRowMapper.class), eq(code)))
                .thenReturn(null);
        assertNull(exchangeRatesRepoImp.findByBase(code));
    }

    @Test
    public void convertShouldReturnCalculatedAmount() {
        when(jdbcTemplate.queryForObject(anyString(), any(BeanPropertyRowMapper.class), eq("USD")))
            .thenReturn(new ExchangeRate("EUR", "USD", 1.0));
        when(jdbcTemplate.queryForObject(anyString(), any(BeanPropertyRowMapper.class), eq("EUR")))
            .thenReturn(new ExchangeRate("EUR", "EUR", 0.85));

        ResponseEntity<Double> response = exchangeRatesRepoImp.convert("USD", "EUR", 100);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void convertShouldReturnInternalServerErrorWhenFromIsNull() {
        
        // when(jdbcTemplate.queryForObject(anyString(), any(BeanPropertyRowMapper.class), eq("EUR")))
        //     .thenReturn(new ExchangeRate("EUR", "EUR", 0.85))
        //     .thenReturn(null);
        
        ResponseEntity<Double> response = exchangeRatesRepoImp.convert(null, "EUR", 100);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void convertShouldReturnInternalServerErrorWhenToIsNull() {
        
        ResponseEntity<Double> response = exchangeRatesRepoImp.convert("USD", null, 100);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void convertShouldReturnInternalServerErrorWhenAmountIsNegative() {
        
        
        ResponseEntity<Double> response = exchangeRatesRepoImp.convert("USD", "EUR", -100);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void saveExchangeRateToDatabaseThatTestCorrectSql() {
        ExchangeRate er = new ExchangeRate("USD", "EUR", 0.8);
        exchangeRatesRepoImp.saveExchangeRateToDatabase(er);
        verify(jdbcTemplate).update("INSERT INTO exchange_rates (base, code, rate) VALUES (?, ?, ?)",
                er.getBase(), er.getCode(), er.getRate());
        
    }


    @Test
    public void saveShouldDeleteExistingRatesAndSaveNewOnes() throws JsonMappingException, JsonProcessingException {
        // Mock the API response
        String apiResponse = "{\"base\":\"USD\",\"rates\":{\"EUR\":0.85,\"JPY\":110.0}}";
        ResponseEntity<String> mockApiResponse = new ResponseEntity<>(apiResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(mockApiResponse);

        // Mock the database operations
        // doNothing().when(jdbcTemplate).update(anyString());
        // doNothing().when(jdbcTemplate).update(anyString(), any(Object[].class));

        // Call the method under test
        ResponseEntity<String> response = exchangeRatesRepoImp.save();

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(jdbcTemplate).update(eq("DELETE FROM exchange_rates")); // Verify delete operation
        verify(jdbcTemplate).update(startsWith("INSERT INTO exchange_rates"));
    }


    @Test
    public void findByCodeFromCSVFileShouldReturnExchangeRate() throws IOException {
        String code = "EUR";
        when(fileReaderRepo.readLines(anyString()))
            .thenReturn(Arrays.asList("USD,EUR,0.85", "USD,JPY,110.0"));

        ExchangeRate result = exchangeRatesRepoImp.findByCodeFromCSVFile(code);

        assertNotNull(result);
        assertEquals("EUR", result.getCode());
        assertEquals(0.85, result.getRate());
    }

    // @Test
    // public void findByBaseTestShouldReturnNullWhenCodeIsNotString() {
    //     // exchangeRatesRepoImp = new ExchangeRatesRepoImp();
    //     String code = "123";
    //     when(exchangeRatesRepo.findByBase(code)).thenReturn(null);
    //     assertEquals(null, exchangeRatesRepoImp.findByBase(code));
    // }

    // @Test
    // public void saveCurrenciesTest() {
    //     // exchangeRatesRepoImp = new ExchangeRatesRepoImp();
    //     ExchangeRate er = new ExchangeRate("USD", "EUR", 0.8);
    //     // when(exchangeRatesRepo.saveCurrencies(any(List.class))).thenReturn(ResponseEntity.ok().body(null));
    //     exchangeRatesRepoImp.saveExchangeRateToDatabase(er);
    //     verify(jdbcTemplate).update("INSERT INTO exchange_rates (base, code, rate) VALUES (?, ?, ?)",
    //             er.getBase(), er.getCode(), er.getRate());

    // }

    // @Test
    // public void convertTestShouldReturnInternalServerErrorWhenFromIsNull() {
    //     // exchangeRatesRepoImp = new ExchangeRatesRepoImp();
    //     String from = null;
    //     String to = "EUR";
    //     double amount = 100;
    //     // when(exchangeRatesRepo.convert(from, to, amount)).thenReturn(null);
    //     // assertEquals(null, exchangeRatesRepoImp.convert(from, to, amount));
    //     when(exchangeRatesRepo.convert(from, to, amount)).thenReturn(ResponseEntity.status(500).body(null));
    //     assertEquals(ResponseEntity.status(500).body(null), exchangeRatesRepoImp.convert(from, to, amount));
    // }

    // @Test
    // public void convertTestShouldReturnInternalServerErrorWhenToIsNull() {
    //     // exchangeRatesRepoImp = new ExchangeRatesRepoImp();
    //     String from = "USD";
    //     String to = null;
    //     double amount = 100;
    //     // when(exchangeRatesRepo.convert(from, to, amount)).thenReturn(null);
    //     // assertEquals(null, exchangeRatesRepoImp.convert(from, to, amount));
    
    //     when(exchangeRatesRepo.convert(from, to, amount)).thenReturn(ResponseEntity.status(500).body(null));
    //     assertEquals(ResponseEntity.status(500).body(null), exchangeRatesRepoImp.convert(from, to, amount));
    // }

    // @Test
    // public void convertTestShouldReturnInternalServerErrorWhenAmountIsNegative() {
    //     // exchangeRatesRepoImp = new ExchangeRatesRepoImp();
    //     String from = "USD";
    //     String to = "EUR";
    //     double amount = -100;
    //     // when(exchangeRatesRepo.convert(from, to, amount)).thenReturn(null);
    //     // assertEquals(null, exchangeRatesRepoImp.convert(from, to, amount));
    
    //     when(exchangeRatesRepo.convert(from, to, amount)).thenReturn(ResponseEntity.status(500).body(null));
    //     assertEquals(ResponseEntity.status(500).body(null), exchangeRatesRepoImp.convert(from, to, amount));
    // }

    // @Test
    // public void convertTestShouldReturnResponseOkWhenFromAndToAreSame() {
    //     // exchangeRatesRepoImp = new ExchangeRatesRepoImp();
    //     String from = "USD";
    //     String to = "USD";
    //     double amount = 100;
    //     // when(exchangeRatesRepo.convert(from, to, amount)).thenReturn(null);
    //     // assertEquals(null, exchangeRatesRepoImp.convert(from, to, amount));
    
    //     when(exchangeRatesRepo.convert(from, to, amount)).thenReturn(ResponseEntity.ok().body(anyDouble()));
    //     assertEquals(new ResponseEntity<>(100.0, HttpStatus.OK), exchangeRatesRepoImp.convert(from, to, amount));
    // }

    // @Test
    // public void convertTestShouldReturnResponseOkWhenFromAndToAreDifferent() {
    //     // ExchangeRatesRepoImp  exchangeRatesRepoImp = new ExchangeRatesRepoImp();
    //     String from = "USD";
    //     String to = "ILS";
    //     double amount = 100;
    
    //     when(exchangeRatesRepo.convert(from, to, amount)).thenReturn(ResponseEntity.ok().body(anyDouble()));
    //     assertEquals(new ResponseEntity<>(anyDouble(), HttpStatus.OK), exchangeRatesRepoImp.convert(from, to, amount));
    // }

    // @Test
    // public void getAllCodesTestShouldReturnInternalServerError() {
    //     // exchangeRatesRepoImp = new ExchangeRatesRepoImp();
    //     when(exchangeRatesRepo.getAllCodes()).thenReturn(ResponseEntity.status(500).body(null));
    //     assertEquals(ResponseEntity.status(500).body(null), exchangeRatesRepoImp.getAllCodes());
    // }

    // @Test
    // public void getAllCodesTestShouldReturnResponseOk() {
    //     // exchangeRatesRepoImp = new ExchangeRatesRepoImp();

    //     // Mock the behavior of getAllCodes on your mock repository
    //     when(exchangeRatesRepo.getAllCodes()).thenReturn(ResponseEntity.ok().body(null));

    //     ResponseEntity<List<String>> result = exchangeRatesRepoImp.getAllCodes();

    //     assertEquals(HttpStatus.OK, result.getStatusCode());
    //     assertNull(result.getBody());
    // }

}
