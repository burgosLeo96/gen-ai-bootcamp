package com.epam.training.gen.ai.config;

import com.epam.training.gen.ai.config.plugin.LightsPlugin;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SemanticKernelConfig {

    @Bean
    public Kernel semanticKernel(ChatCompletionService chatCompletionService) {
        KernelPlugin lightPlugin = KernelPluginFactory.createFromObject(new LightsPlugin(),
                "LightsPlugin");

        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .withPlugin(lightPlugin)
                .build();
    }

    @Bean
    public InvocationContext invocationContext() {
        return new InvocationContext.Builder()
                .withReturnMode(InvocationReturnMode.LAST_MESSAGE_ONLY)
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .build();
    }
}
