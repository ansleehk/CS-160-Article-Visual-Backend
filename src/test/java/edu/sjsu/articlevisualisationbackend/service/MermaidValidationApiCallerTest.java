package edu.sjsu.articlevisualisationbackend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MermaidValidationApiCallerTest {


    @Test
    public void testValidMermaid(){
        final String MERMAID_CODE = "graph TD\nA[Christmas] -->|Get money| B(Go shopping)\nB --> C{Let me think}\nC -->|One| D[Laptop]\nC -->|Two| E[iPhone]\nC -->|Three| F[fa:fa-car Car]";
        final boolean EXPECTED_OUTPUT = true;

        MermaidValidationApiCaller mermaidValidationApiCaller = new MermaidValidationApiCaller(MERMAID_CODE);

        final boolean test = mermaidValidationApiCaller.validate();

        assertEquals(EXPECTED_OUTPUT, test);

    }

    @Test
    public void testInvalidMermaid(){
        final String MERMAID_CODE = "graph TD\nA[Christmas] -->|Get money| B(Go shopping)\nB --> C{Let me think}\nC -->|One| D[Laptop]\nC -->|Two| E[iPhone]\nC -->|Three| Ffa:fa-car Car]";
        final boolean EXPECTED_OUTPUT = false;

        MermaidValidationApiCaller mermaidValidationApiCaller = new MermaidValidationApiCaller(MERMAID_CODE);

        final boolean test = mermaidValidationApiCaller.validate();

        assertEquals(EXPECTED_OUTPUT, test);

        assertEquals(EXPECTED_OUTPUT, test);

    }
}
