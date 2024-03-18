package edu.sjsu.articlevisualisationbackend.service.exception;

public class InvalidChatGptGeneration extends Exception{
    public InvalidChatGptGeneration(String message) {
        super(message);
    }
}
