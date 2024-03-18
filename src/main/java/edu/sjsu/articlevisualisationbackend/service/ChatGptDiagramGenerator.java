package edu.sjsu.articlevisualisationbackend.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import edu.sjsu.articlevisualisationbackend.service.exception.InvalidChatGptGeneration;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ChatGptDiagramGenerator {

    private OpenAiService service;
    private ChatCompletionRequest chatCompletionRequest;
    private List<ChatMessage> messageRecord = new ArrayList<>();

    private String pdfText;
    private JSONObject chatGptPrompt;



    private final String OPERATION_USR_MSG_JSON_KEY = "prompt_msg";
    private final String OPERATION_FEW_SHOT_JSON_KEY = "few_shot";
    private final String OPERATION_FEW_SHOT_PROMPT_JSON_KEY = "prompt_msg";
    private final String OPERATION_FEW_SHOT_RESPONSE_JSON_KEY = "response";



    public ChatGptDiagramGenerator(String pdfText) throws IOException {
        this.initOpenAiService();
        this.initChatCompletionRequest();
        this.loadJsonPrompt();
        this.initSystemMessage();

        this.pdfText = pdfText;
    }

    public void setPdfText(String pdfText) {
        this.pdfText = pdfText;
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
        String jsonContent;
        ClassPathResource resource = new ClassPathResource("prompt.json");

        try (InputStream inputStream = resource.getInputStream()) {
            jsonContent = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }

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



    private String makeRequest(
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

        return this.makeRequest(
                OPERATION_JSON_KEY,
                this.pdfText,
                true
        );

    }

    private String makeMermaidCodeRequest(String keywordText) {
        final String OPERATION_JSON_KEY = "generate_mermaid_using_keyword";

        return this.makeRequest(
                OPERATION_JSON_KEY,
                keywordText,
                false
        );
    }

    private String removeMermaidCodeHeadFoot(String mermaidCode){
        final String MERMAID_CODE_HEAD = "```mermaid";
        final String MERMAID_CODE_FOOT = "```";

        return mermaidCode.replace(MERMAID_CODE_HEAD, "").replace(MERMAID_CODE_FOOT, "");
    }

    private boolean validateMermaidCode(String mermaidCode){
        MermaidValidationApiCaller mermaidValidationApiCaller = new MermaidValidationApiCaller(mermaidCode);

        return mermaidValidationApiCaller.validate();
    }


    private String generateReliableMermaidCode(String keywords) throws InvalidChatGptGeneration {
        final int MAX_ATTEMPT_COUNT = 3;
        int attemptCount = 0;
        boolean isMermaidValid = false;
        String mermaidCode;

        do {
            mermaidCode = this.makeMermaidCodeRequest(keywords);
            mermaidCode = this.removeMermaidCodeHeadFoot(mermaidCode);
            isMermaidValid = this.validateMermaidCode(mermaidCode);
            attemptCount++;

        } while (!isMermaidValid && attemptCount < MAX_ATTEMPT_COUNT);

        if (isMermaidValid) {
            return mermaidCode;
        } else {
            throw new InvalidChatGptGeneration("Failed to generate mermaid code");
        }
    }

    public String generateMermaidCode() throws InvalidChatGptGeneration {
        final String KEYWORDS_FROM_PDF = this.makeKeywordsRequest();
        final String MERMAID_CODE = this.generateReliableMermaidCode(KEYWORDS_FROM_PDF);

        return this.removeMermaidCodeHeadFoot(MERMAID_CODE);
    }
}
