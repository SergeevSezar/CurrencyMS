package com.sergeev.currency.client;

import java.time.LocalDate;

public interface HttpCbrCurrencyRateClient {

    String requestByDate(LocalDate date);
}
