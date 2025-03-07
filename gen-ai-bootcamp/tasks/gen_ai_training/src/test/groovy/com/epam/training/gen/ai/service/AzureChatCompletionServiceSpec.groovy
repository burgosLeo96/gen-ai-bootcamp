package com.epam.training.gen.ai.service

import com.epam.training.gen.ai.service.chatcompletion.AzureChatCompletionService
import com.microsoft.semantickernel.Kernel
import com.microsoft.semantickernel.orchestration.InvocationContext
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent
import reactor.core.publisher.Mono
import spock.lang.Specification

class AzureChatCompletionServiceSpec extends Specification {

    private Kernel kernel
    private InvocationContext invocationContext
    private ChatCompletionService chatCompletionService
    private AzureChatCompletionService azureChatCompletionService

    def setup() {
        kernel = Mock(Kernel)
        invocationContext = Mock(InvocationContext)
        chatCompletionService = Mock(ChatCompletionService)
        azureChatCompletionService = new AzureChatCompletionService(chatCompletionService, invocationContext, kernel)
    }

    def 'should process message contents successfully'() {
        given:
        var prompt = 'This is another test prompt'
        var mockResponseList = [new ChatMessageContent<>(AuthorRole.ASSISTANT, 'This is a test response')]
        var mockResponseMono = Mono.just(mockResponseList)

        when:
        var response = azureChatCompletionService.getChatResponse(prompt)

        then:
        1 * chatCompletionService.getChatMessageContentsAsync(*_) >> mockResponseMono
        response.size() == 1
        response[0] == 'This is a test response'
    }

    def 'should return empty list when response is empty'() {
        given:
        var prompt = 'This is another test prompt'

        when:
        var response = azureChatCompletionService.getChatResponse(prompt)

        then:
        1 * chatCompletionService.getChatMessageContentsAsync(*_) >> Mono.justOrEmpty((Object) null)
        response.size() == 0
    }

    def 'should return only the messages in the response that were generated by the assistant'() {
        given:
        var mockResponseList = [
                new ChatMessageContent<>(AuthorRole.USER, 'Hi, I am an user'),
                new ChatMessageContent<>(AuthorRole.ASSISTANT, null),
                new ChatMessageContent<>(AuthorRole.ASSISTANT, 'This message was generated by the assistant')
        ]
        var mockResponseMono = Mono.just(mockResponseList)

        when:
        var response = azureChatCompletionService.getChatResponse('This is my test prompt')

        then:
        1 * chatCompletionService.getChatMessageContentsAsync(*_) >> mockResponseMono
        response.size() == 1
        response[0] == 'This message was generated by the assistant'
    }

}
