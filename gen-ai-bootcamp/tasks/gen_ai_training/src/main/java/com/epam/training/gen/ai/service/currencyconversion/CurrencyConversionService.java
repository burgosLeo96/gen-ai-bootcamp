package com.epam.training.gen.ai.service.currencyconversion;

import com.epam.training.gen.ai.service.currencyconversion.model.ConversionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyConversionService {

    private final RestClient restClient;

    public BigDecimal convert(String fromCurrency, String amount, String toCurrency) {
        ConversionResponse conversionResponse = getConversionRates(fromCurrency, toCurrency);
        Map<String, BigDecimal> conversionRates = conversionResponse.rates();

        return convertCurrency(fromCurrency, new BigDecimal(amount), toCurrency, conversionRates);
    }

    private BigDecimal convertCurrency(String fromCurrency, BigDecimal amount, String toCurrency, Map<String, BigDecimal> conversionRates) {
        BigDecimal fromCurrencyToUSD = convertFromCurrencyToUSD(fromCurrency, amount, conversionRates);
        return convertFromUSDToCurrency(toCurrency, fromCurrencyToUSD, conversionRates);
    }

    private BigDecimal convertFromUSDToCurrency(String toCurrency, BigDecimal amount, Map<String, BigDecimal> conversionRates) {
        BigDecimal toCurrencyRate = conversionRates.get(toCurrency);
        return amount.multiply(toCurrencyRate);
    }

    private BigDecimal convertFromCurrencyToUSD(String fromCurrency, BigDecimal amount, Map<String, BigDecimal> conversionRates) {
        BigDecimal fromCurrencyRate = conversionRates.get(fromCurrency);
        return amount.divide(fromCurrencyRate, RoundingMode.CEILING);
    }

    private ConversionResponse getConversionRates(String fromCurrency, String toCurrency) {

        return this.restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("symbols", String.format("%s,%s", fromCurrency, toCurrency))
                        .build())
                .retrieve()
                .body(ConversionResponse.class);
    }
}
