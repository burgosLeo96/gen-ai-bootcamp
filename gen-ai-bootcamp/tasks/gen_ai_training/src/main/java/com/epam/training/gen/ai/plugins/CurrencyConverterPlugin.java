package com.epam.training.gen.ai.plugins;

import com.epam.training.gen.ai.service.currencyconversion.CurrencyConversionService;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;

import java.math.BigDecimal;

public class CurrencyConverterPlugin {

    private final CurrencyConversionService currencyConversionService;

    public CurrencyConverterPlugin(CurrencyConversionService currencyConversionService) {
        this.currencyConversionService = currencyConversionService;
    }

    @DefineKernelFunction(
            name = "convertCurrency",
            description = "Converts the amount from one currency to another",
            returnType = "java.math.BigDecimal")

    public BigDecimal convertCurrency(
            @KernelFunctionParameter(
                    name = "amount",
                    description = "the currency amount to convert") String amount,

            @KernelFunctionParameter(
                    name = "fromCurrency",
                    description = "the source currency") String fromCurrency,

            @KernelFunctionParameter(
                    name = "toCurrency",
                    description = "the target currency") String toCurrency) {

        return currencyConversionService.convert(fromCurrency, amount, toCurrency);
    }

}
