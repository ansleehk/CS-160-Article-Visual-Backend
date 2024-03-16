package edu.sjsu.articlevisualisationbackend.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenerateDiagram {

    public static void main(String[] args) {
        String text = "pdf text";
        String apiKey = env();

        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(30));

        // Generate Mermaid code for concept map using keywords
        String mermaidCode = generateMermaidCode(service, text);

        System.out.println(mermaidCode);
        //return mermaidCode;
    }
    public static String env() {
        Dotenv dotenv = Dotenv.load();
        return dotenv.get("OPENAI_KEY");
    }
    private static String generateMermaidCode(OpenAiService service, String text) {
        // define the prompt
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "Use the following text to extract maximum of 5-7 keywords along with the relationship between them. " +
                "Then generate mermaid code to make a concept map with that information. Make sure that there are linkages between the concepts on the map. " +
                " The lines of the code should be formatted like this \"xxxxx -- xxxxx xxx xxxx xx xxxx --> xxxxx\" Make it so that together they will look like sentences that make sense. " +
                "Reply with only the mermaid code: "
                + text);
        messages.add(userMessage);

        // Set up completion request
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                //.maxTokens(150)
                .logitBias(new HashMap<>())
                .build();

        // get the response
        ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();

        // return the code
        return responseMessage.getContent();
    }
}
