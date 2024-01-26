package com.softwareproject.app.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ResponseMsg<T> {
    private String message;
    // make data 
    private T data;
}
