package com.sergeev.currency.client;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CbrCurrencyRateClientImpl implements HttpCbrCurrencyRateClient {

    private static final String DATA_PATTERN = "dd/MM/yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATA_PATTERN);

    @Override
    public String requestByDate(LocalDate date) {
        var baseUrl = "https://cbr.ru/scripts/XML_daily.asp";
        var client = HttpClient.newHttpClient();
        var url = buildRequestUrl(baseUrl, date);
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildRequestUrl(String baseUrl, LocalDate date) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl).queryParam("date_req", DATE_TIME_FORMATTER).build().toUriString();
    }
}
