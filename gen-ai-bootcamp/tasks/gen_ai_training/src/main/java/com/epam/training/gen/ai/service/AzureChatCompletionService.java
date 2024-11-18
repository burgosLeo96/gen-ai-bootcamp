package com.epam.training.gen.ai.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureChatCompletionService {

    private final ChatHistory chatHistory = new ChatHistory();
    private final ChatCompletionService chatCompletionService;
    private final InvocationContext invocationContext;
    private final Kernel kernel;

    public List<String> getChatResponse(String prompt) {
        log.info("Processing prompt request");
        this.addUserPromptToHistory(prompt);
        var chatResponse = chatCompletionService.getChatMessageContentsAsync(chatHistory, kernel, invocationContext).block();
        log.info("Chat response received. Processing assistant messages");
        var assistantResponse = this.processChatResponse(chatResponse);

        return this.getAssistantResponseMessagesContent(assistantResponse);
    }

    private List<ChatMessageContent<?>> processChatResponse(List<ChatMessageContent<?>> chatMessageContentList) {
        if(chatMessageContentList == null) {
            log.warn("Chat response is null. Returning empty list");
            return List.of();
        }

        List<ChatMessageContent<?>> messagesContent = chatMessageContentList
            .stream()
            .filter(this::messageGeneratedByAssistant)
            .toList();

        this.addAssistantResponsesToHistory(messagesContent);

        return messagesContent;
    }

    private List<String> getAssistantResponseMessagesContent(List<ChatMessageContent<?>> messageContents){
        log.info("Extracting assistant messages content");
        return messageContents
                .stream()
                .map(ChatMessageContent::getContent)
                .filter(Objects::nonNull)
                .flatMap(message -> Arrays.stream(message.split("\n+")))
                .toList();
    }

    private boolean messageGeneratedByAssistant(ChatMessageContent<?> messageContent) {
        return messageContent.getAuthorRole() == AuthorRole.ASSISTANT && messageContent.getContent() != null;
    }

    private void addAssistantResponsesToHistory(List<ChatMessageContent<?>> messagesContent) {
        this.chatHistory.addAll(messagesContent);
    }

    private void addUserPromptToHistory(String prompt) {
        this.chatHistory.addUserMessage(prompt);
    }
}
