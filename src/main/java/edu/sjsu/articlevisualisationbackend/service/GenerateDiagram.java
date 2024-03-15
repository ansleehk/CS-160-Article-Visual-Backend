package edu.sjsu.articlevisualisationbackend.service;

import com.theokanning.openai.gpt3.Gpt3;
import com.theokanning.openai.gpt3.models.CompletionRequest;
import com.theokanning.openai.gpt3.models.CompletionResponse;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;

public class GenerateDiagram {

    public static void main(String[] args) {
        String text = "pdf text";

        String apiKey = "KEY";

        // Initialize OpenAI client
        Gpt3 gpt3 = new Gpt3(apiKey);

        // Generate keywords using the language model
        List<String> keywords = extractKeywords(gpt3, text);

        // Generate Mermaid code for concept map using keywords
        String mermaidCode = generateMermaidCode(gpt3, keywords);
        
        System.out.println("Mermaid Code:");
        System.out.println(mermaidCode);
        //return mermaidCode;
    }
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        dotenv.get("OPENAI_KEY");
    }
    private static List<String> extractKeywords(Gpt3 gpt3, String text) {
        // Define prompt with text and task description
        String prompt = "Extract keywords from the following text:\n\""
                + text
                + "\"\n\nKeywords:";

        // Set up completion request
        CompletionRequest completionRequest = new CompletionRequest.Builder()
                .prompt(prompt)
                .maxTokens(50)
                .build();

        // Generate keywords using the language model
        CompletionResponse completionResponse = gpt3.createCompletion(completionRequest);

        // Extract keywords from response
        List<String> keywords = parseKeywords(completionResponse.getText());

        return keywords;
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

    private static String generateMermaidCode(Gpt3 gpt3, List<String> keywords) {
        // Prompt OpenAI to generate Mermaid code for concept map
        StringBuilder prompt = new StringBuilder("Generate Mermaid code for a concept map using the following keywords:\n");
        for (String keyword : keywords) {
            prompt.append("- ").append(keyword).append("\n");
        }

        // Set up completion request
        CompletionRequest completionRequest = new CompletionRequest.Builder()
                .prompt(prompt.toString())
                .maxTokens(200)
                .build();

        // Generate concept map using the language model
        CompletionResponse completionResponse = gpt3.createCompletion(completionRequest);

        return completionResponse.getText();
    }
}
