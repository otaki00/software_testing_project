package com.softwareproject.app.repo;

import java.util.List;

import com.softwareproject.app.entities.ExchangeRate;

public class InnerDatabaseReaderImp  implements InnerDatabaseReader{

    // create in memory database for testing 
    public final List<ExchangeRate> exchangeRates = List.of(
        new ExchangeRate("USD", 1.0),
        new ExchangeRate("EUR", 0.8),
        new ExchangeRate("GBP", 0.7),
        new ExchangeRate("CAD", 1.2),
        new ExchangeRate("AUD", 1.3),
        new ExchangeRate("JPY", 100.0),
        new ExchangeRate("CNY", 6.8),
        new ExchangeRate("INR", 74.0),
        new ExchangeRate("RUB", 75.0),
        new ExchangeRate("BRL", 5.6),
        new ExchangeRate("CHF", 0.9),
        new ExchangeRate("MXN", 22.0),
        new ExchangeRate("KRW", 1200.0),
        new ExchangeRate("TRY", 7.8),
        new ExchangeRate("IDR", 14000.0),
        new ExchangeRate("SAR", 3.75),
        new ExchangeRate("ZAR", 16.0),
        new ExchangeRate("HKD", 7.8),
        new ExchangeRate("NOK", 9.0),
        new ExchangeRate("NZD", 1.5),
        new ExchangeRate("SEK", 9.0),
        new ExchangeRate("SGD", 1.4),
        new ExchangeRate("TRY", 7.8),
        new ExchangeRate("ILS", 3.3),
        new ExchangeRate("PLN", 3.8),
        new ExchangeRate("PHP", 48.0),
        new ExchangeRate("CZK", 22.0),
        new ExchangeRate("CLP", 800.0),
        new ExchangeRate("AED", 3.7),
        new ExchangeRate("ARS", 75.0),
        new ExchangeRate("THB", 30.0),
        new ExchangeRate("COP", 3800.0),
        new ExchangeRate("EGP", 16.0),
        new ExchangeRate("KWD", 0.3),
        new ExchangeRate("QAR", 3.6),
        new ExchangeRate("MYR", 4.2),
        new ExchangeRate("NGN", 380.0),
        new ExchangeRate("RON", 4.1),
        new ExchangeRate("DZD", 130.0));

    @Override
    public ExchangeRate findByBase(String code) {
        
        if(code == null){
            return null;
        }

        if(code == ""){
            return null;
        }

        for (ExchangeRate exchangeRate : exchangeRates) {
            if(exchangeRate.getCode().equals(code)){
                return exchangeRate;
            }
        }
        return null;
    }

    @Override
    public List<String> getAllCodes() {
        
        List<String> codes = List.of(
            "USD",
            "EUR",
            "GBP",
            "CAD",
            "AUD",
            "JPY",
            "CNY",
            "INR",
            "RUB",
            "BRL",
            "CHF",
            "MXN",
            "KRW",
            "TRY",
            "IDR",
            "SAR",
            "ZAR",
            "HKD",
            "NOK",
            "NZD",
            "SEK",
            "SGD",
            "TRY",
            "ILS",
            "PLN",
            "PHP",
            "CZK",
            "CLP",
            "AED",
            "ARS",
            "THB",
            "COP",
            "EGP",
            "KWD",
            "QAR",
            "MYR",
            "NGN",
            "RON",
            "DZD");

        return codes;
    }

}
