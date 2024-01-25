package com.sergeev.currency.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sergeev.currency.client.HttpCbrCurrencyRateClient;
import com.sergeev.currency.schema.ValCurs;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toMap;

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
        ValCurs response = unmarshall(xml);
        return response.getValute().stream()
                .collect(toMap(ValCurs.Valute::getCharCode, item -> parseWithLocale(item.getValue())));
    }

    private ValCurs unmarshall(String xml) {
        try (StringReader reader = new StringReader(xml)){
            JAXBContext context = JAXBContext.newInstance(ValCurs.class);
            return (ValCurs) context.createUnmarshaller().unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal parseWithLocale(String currency) {
        try {
            double value = NumberFormat.getNumberInstance(Locale.getDefault()).parse(currency).doubleValue();
            return BigDecimal.valueOf(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


}
