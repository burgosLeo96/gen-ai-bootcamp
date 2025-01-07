package com.epam.training.gen.ai.config.restclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${currency-converter-api-key}")
    private String apiKey;

    private static final String CURRENCY_CONVERSION_BASE_URL = "https://api.currencyfreaks.com/v2.0/rates/latest";

    @Bean
    public RestClient currencyConversionRestClient() {
        return RestClient.builder().baseUrl(String.format("%s?apikey=%s", CURRENCY_CONVERSION_BASE_URL, apiKey)).build();
    }

}
