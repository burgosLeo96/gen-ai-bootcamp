package com.epam.training.gen.ai.service.currencyconversion.model;

import java.math.BigDecimal;
import java.util.Map;

public record ConversionResponse(String base, Map<String, BigDecimal> rates) {
}
