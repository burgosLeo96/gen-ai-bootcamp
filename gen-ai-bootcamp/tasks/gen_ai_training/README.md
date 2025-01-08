# AI Chat, Currency Conversion and Embeddings Service

This project is a Spring Boot application that provides AI chat completion, currency conversion and embeddings services. It leverages Microsoft's Semantic Kernel to handle AI-related tasks.

## Features

- **AI Chat Completion**: Uses the `ChatCompletionService` to generate chat responses based on user prompts.
- **Currency Conversion**: Converts currency amounts using real-time exchange rates.
- **Embeddings API**: Builds, stores, and searches for embeddings in a QDrant DB instance.

## Technologies Used

- Java
- Spring Boot
- Maven
- Semantic Kernel

## Project Structure

- `config`: Contains configuration classes for setting up the Semantic Kernel and other beans.
- `service`: Contains service classes for chat completion and currency conversion.
- `api`: Contains REST controllers for handling API requests.

## Semantic Kernel Integration

The project integrates Semantic Kernel to provide AI chat completion services. The `Kernel` is configured with multiple AI services and plugins to handle various tasks.

### Key Components

- **Kernel Configuration**: The `SemanticKernelConfig` class configures the `Kernel` with necessary services and plugins.
- **Chat Completion Service**: The `AzureChatCompletionService` class uses the `ChatCompletionService` from Semantic Kernel to generate chat responses.
- **Invocation Context**: The `InvocationContext` is used to manage the context of AI service invocations.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker running in your local machine to run TestContainer

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/burgosLeo96/gen-ai-bootcamp.git
    cd gen-ai-bootcamp
    ```
   <br>

2. Add the following environment variables to the project:
   - `EPAM_DIAL_KEY`: The API key for the Azure Chat Completion service.
   - `CURRENCY_CONVERTER_API_KEY`: The API key for the Currency Layer service. One API Key should be retrieved after subscribing to https://currencyfreaks.com/

    <br>

3. Build the project:
    ```sh
    mvn clean install
    ```
   <br>

4. Run the application:
    ```sh
    mvn spring-boot:run
    ```

### Usage

- **Chat Completion**: Send a POST request to `/v1/chat` with a `prompt` parameter to get AI-generated chat responses.


- **Currency Conversion**: Ask the model to convert from once currency to other. Send the request in the following format: **amount** **fromCurrency** to **toCurrency**.

  Example:
  Convert 100 USD to EUR


- **Embeddings API**: The application provides an API to build, store, and search embeddings.
   - Build and Store Embedding: To build and store an embedding, send a POST request to /v1/embeddings with the following JSON body:
        ```json
        {
        "text": "Your text here"
        }
        ```

   - Search for Closest Embeddings: To search for the closest embedding, send a GET request to /v1/embeddings with the text query parameter:
       ```sh
       /v1/embeddings?text=Your text here
       ```

   - Build Embedding: To build an embedding without storing it, send a POST request to /v1/embeddings/build with the following JSON body:
        ```json
        {
        "text": "Your text here"
        }
        ```

<br>

## License

This project is licensed under the MIT License.