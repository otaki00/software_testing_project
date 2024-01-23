package com.softwareproject.app.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class ExchangeRatesRepoImpTest {
    
    @Mock 
    private ExchangeRatesRepoImp exchangeRatesRepoImp;

    @BeforeEach
    void setUp() {
        exchangeRatesRepoImp = new ExchangeRatesRepoImp();
    }

    @Test
    public void findByBaseTest() {
        String code = "USD";
        when(exchangeRatesRepoImp.findByBase(code)).thenReturn(null);
        assertEquals(null, exchangeRatesRepoImp.findByBase(code));
    }

}
