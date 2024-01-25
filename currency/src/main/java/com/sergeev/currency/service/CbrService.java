package com.sergeev.currency.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sergeev.currency.client.HttpCbrCurrencyRateClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class CbrService {

    private HttpCbrCurrencyRateClient client;
    private Cache<LocalDate, Map<String, BigDecimal>> cache;

    public CbrService(HttpCbrCurrencyRateClient client) {
        this.cache = CacheBuilder.newBuilder().build();
        this.client = client;
    }

    public BigDecimal requestByCurrencyCode(String code) {
        try {
            return cache.get(LocalDate.now(), this::callAllCurrencyQuotesByCurrentDate).get(code);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, BigDecimal> callAllCurrencyQuotesByCurrentDate() {
        var xml = client.requestByDate(LocalDate.now());

        return null;
    }
}
