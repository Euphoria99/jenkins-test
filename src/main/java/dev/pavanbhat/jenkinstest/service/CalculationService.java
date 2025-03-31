package dev.pavanbhat.jenkinstest.service;

import org.springframework.stereotype.Service;

@Service
public class CalculationService {
    
    public int add(int a, int b) {
        return a + b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public boolean isEven(int number) {
        return number % 2 == 0;
    }
}
