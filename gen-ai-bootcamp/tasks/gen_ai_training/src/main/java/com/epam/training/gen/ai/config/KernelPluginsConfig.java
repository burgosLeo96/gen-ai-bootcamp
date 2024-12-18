package com.epam.training.gen.ai.config;

import com.epam.training.gen.ai.plugins.CurrencyConverterPlugin;
import com.epam.training.gen.ai.service.currencyconversion.CurrencyConversionService;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KernelPluginsConfig {

    private static final String CURRENCY_CONVERTER_PLUGIN = "currencyConverterPlugin";

    @Bean
    public KernelPlugin currencyConverterPlugin(CurrencyConversionService currencyConversionService) {
        return KernelPluginFactory.createFromObject(new CurrencyConverterPlugin(currencyConversionService), CURRENCY_CONVERTER_PLUGIN);
    }

}
