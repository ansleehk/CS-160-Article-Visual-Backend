package edu.sjsu.articlevisualisationbackend.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Paths;


public class ChatGptDiagramGenerator {

    OpenAiService service;
    ChatCompletionRequest chatCompletionRequest;
    List<ChatMessage> messageRecord = new ArrayList<>();
    String pdfText;
    JSONObject chatGptPrompt;

    final String OPERATION_USR_MSG_JSON_KEY = "prompt_msg";
    final String OPERATION_FEW_SHOT_JSON_KEY = "few_shot";
    final String OPERATION_FEW_SHOT_PROMPT_JSON_KEY = "prompt_msg";
    final String OPERATION_FEW_SHOT_RESPONSE_JSON_KEY = "response";



    public ChatGptDiagramGenerator(String PdfText) throws IOException {
        this.pdfText = PdfText;
        this.initOpenAiService();
        this.initChatCompletionRequest();
        this.loadJsonPrompt();
        this.initSystemMessage();
    }

    private void initOpenAiService() {
        String apiKey = getApiKeyFromEnv();
        this.service = new OpenAiService(
                this.getApiKey()
        );
    }

    private String getApiKey(){
        String apiKey;
        try{
            apiKey = getApiKeyFromEnvFile();
        } catch (Exception e) {
            System.out.println("Error reading from env file");
            apiKey = getApiKeyFromEnv();
        }
        return apiKey;
    }

    private String getApiKeyFromEnv(){
        final String OPENAI_KEY_ENV_VAR = "OPENAI_KEY";
        return System.getenv(OPENAI_KEY_ENV_VAR);
    }

    private String getApiKeyFromEnvFile() {
        final String OPENAI_KEY_ENV_VAR = "OPENAI_KEY";
        final String ENV_FILE_PATH = "src/main/resources/.env";

        Dotenv dotenv = Dotenv.configure()
                .filename(ENV_FILE_PATH)
                .load();
        return dotenv.get(OPENAI_KEY_ENV_VAR);
    }


    private void initChatCompletionRequest(){
        final String CHAT_GPT_MODEL = "gpt-3.5-turbo";

        this.chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model(CHAT_GPT_MODEL)
                .build();
    }


    private void loadJsonPrompt() throws IOException {
        final String PROMPT_FILE_PATH = "src/main/resources/prompt.json";
        String jsonContent = new String(Files.readAllBytes(Paths.get(PROMPT_FILE_PATH)));
        this.chatGptPrompt = new JSONObject(jsonContent);
    }

    private void initSystemMessage(){
        final String MESSAGE_JSON_KEY = "system_msg";

        ChatMessage chatMessage = new ChatMessage(
                ChatMessageRole.SYSTEM.value(),
                this.chatGptPrompt.get(MESSAGE_JSON_KEY).toString()
        );

        this.messageRecord.add(chatMessage);
    }

    private void addNormalChatMessage(
            String promptMessage,
            String promptMessageAppendix
    ) {
        ChatMessage chatUserMessage = new ChatMessage(
                ChatMessageRole.USER.value(),
                promptMessage + promptMessageAppendix
        );

        this.messageRecord.add(chatUserMessage);
    }

    private void addFewShotChatMessage(
            String promptMessage,
            String templateResponse
    ){
        ChatMessage chatUserMessage = new ChatMessage(
                ChatMessageRole.USER.value(),
                promptMessage
        );

        ChatMessage chatAssistantMessage = new ChatMessage(
                ChatMessageRole.ASSISTANT.value(),
                templateResponse
        );


        this.messageRecord.add(chatUserMessage);
        this.messageRecord.add(chatAssistantMessage);
    }

    private String sendAPIRequest(){
        ChatMessage responseMessage = this.service.createChatCompletion(this.chatCompletionRequest).getChoices().get(0).getMessage();


        return responseMessage.getContent();
    }



    private String sendRequest(
            String operationJsonKey,
            String promptMessageAppendix,
            boolean isHasFewShotResponse
    ){

        final JSONObject OPERATION_JSON_OBJECT = this.chatGptPrompt.getJSONObject(operationJsonKey);


        if (isHasFewShotResponse) {
            final JSONObject fewShotObject = OPERATION_JSON_OBJECT.getJSONObject(OPERATION_FEW_SHOT_JSON_KEY);

            final String promptMessage = fewShotObject.getString(OPERATION_FEW_SHOT_PROMPT_JSON_KEY);
            final String templateResponse = fewShotObject.getString(OPERATION_FEW_SHOT_RESPONSE_JSON_KEY);

            this.addFewShotChatMessage(promptMessage, templateResponse);
        }


        this.addNormalChatMessage(
                OPERATION_JSON_OBJECT.getString(OPERATION_USR_MSG_JSON_KEY),
                promptMessageAppendix
        );

        this.chatCompletionRequest.setMessages(this.messageRecord);


        return this.sendAPIRequest();
    }

    private String makeKeywordsRequest(){
        final String OPERATION_JSON_KEY = "get_keyword_from_pdf";

        return this.sendRequest(
                OPERATION_JSON_KEY,
                this.pdfText,
                true
        );

    }

    private String makeMermaidCodeRequest(String keywordText) {
        final String OPERATION_JSON_KEY = "generate_mermaid_using_keyword";

        return this.sendRequest(
                OPERATION_JSON_KEY,
                keywordText,
                false
        );
    }

    public String generateMermaidCode() {
        final String KEYWORDS_FROM_PDF = this.makeKeywordsRequest();
        final String MERMAID_CODE = this.makeMermaidCodeRequest(KEYWORDS_FROM_PDF);

        return MERMAID_CODE;
    }
}
