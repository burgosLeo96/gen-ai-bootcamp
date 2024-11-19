package com.epam.training.gen.ai.api

import com.epam.training.gen.ai.service.AzureChatCompletionService
import org.springframework.http.HttpStatusCode
import spock.lang.Specification

class ChatControllerSpec extends Specification {

    private AzureChatCompletionService azureChatCompletionService
    private ChatController chatController

    def setup() {
        azureChatCompletionService = Mock(AzureChatCompletionService)
        chatController = new ChatController(azureChatCompletionService)
    }

    def 'should return API response after calling completionService'() {
        given:
        def prompt = 'This is a test prompt, please confirm that you received it.'

        when:
        def response = chatController.generateChat(prompt)

        then:
        1 * azureChatCompletionService.getChatResponse(prompt) >> ['Confirmed!']
        response.statusCode.value() == 200 &&
        response.body &&
        response.body.content() == ['Confirmed!']
    }
}
