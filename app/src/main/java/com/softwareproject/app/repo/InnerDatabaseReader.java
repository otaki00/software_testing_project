package com.softwareproject.app.repo;

import java.util.List;

import com.softwareproject.app.entities.ExchangeRate;

public interface InnerDatabaseReader {

    ExchangeRate findByBase(String code);

    List<String> getAllCodes();
} 
