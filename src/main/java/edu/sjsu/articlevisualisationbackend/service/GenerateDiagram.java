package edu.sjsu.articlevisualisationbackend.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GenerateDiagram {

    public static void main(String[] args) {
        String text = "The POSIX thread libraries are a C/C++ thread API based on standards. It enables the creation of a new concurrent process flow. It works well on multi-processor or multi-core systems, where the process flow may be scheduled to execute on another processor, increasing speed through parallel or distributed processing. Because the system does not create a new system, virtual memory space and environment for the process, threads needless overhead than “forking” or creating a new process. While multiprocessor systems are the most effective, benefits can also be obtained on uniprocessor systems that leverage delay in I/O and other system processes that may impede process execution.";
        String apiKey = env();

        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(30));

        // Generate keywords using the language model
        List<String> keywords = extractKeywords(service, text);

        // Generate Mermaid code for concept map using keywords
        String mermaidCode = generateMermaidCode(service, keywords);

        System.out.println(mermaidCode);
        //return mermaidCode;
    }
    public static String env() {
        Dotenv dotenv = Dotenv.load();
        return dotenv.get("OPENAI_KEY");
    }
    private static List<String> extractKeywords(OpenAiService service, String text) {
        // Define prompt with text and task description

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "Extract keywords from the following text:\n\""
                + text
                + "\"\n\nKeywords:");
        messages.add(userMessage);
        // Set up completion request
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(50)
                .logitBias(new HashMap<>())
                .build();

        ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
        messages.add(responseMessage);
        // Extract keywords from response
        List<String> keywords = parseKeywords(responseMessage.getContent());
        return parseKeywords(keywords.toString());
    }


   private static List<String> parseKeywords(String response) {
        // Extract keywords from response
        List<String> keywords = new ArrayList<>();
        String[] lines = response.split("\n");
        for (String line : lines) {
            if (!line.startsWith("Keyword:")) continue;
            String keyword = line.substring("Keyword:".length()).trim();
            keywords.add(keyword);
        }
        return keywords;
    }

    private static String generateMermaidCode(OpenAiService service, List<String> keywords) {

        // Prompt OpenAI to generate Mermaid code for concept map
        StringBuilder prompt = new StringBuilder("Generate Mermaid code for a concept map using the following keywords:\n");
        for (String keyword : keywords) {
            prompt.append("- ").append(keyword).append("\n");
        }
        ChatMessage promptMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt.toString());


        // Set up completion request
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(Collections.singletonList(promptMessage))
                .n(1)
                .maxTokens(50)
                .logitBias(new HashMap<>())
                .build();

        // Generate concept map using the language model
        ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();

        // Extract code from response
        return responseMessage.getContent();

    }
}
