package edu.sjsu.articlevisualisationbackend.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.io.IOException;

public class ChatGptDiagramGeneratorTest {
    @InjectMocks
    private ChatGptDiagramGenerator chatGptDiagramGenerator;

    @Test
    public void testGenerateDiagram() throws IOException {
        final String ARTICLE = "What I Learned in my First 3 Months as a Freelance Data Scientist. I recently decided it was time to make a change in my life. I have been working since I was 14 years old (tell me you are from Gen X without telling me you are from Gen X) and in all of these decades I have always worked for someone else. I very much remember the dot com bust in the early 2000’s when there were plenty of tech layoffs, but it felt mild relative to what is going on today. Perceiving that company loyalty is a thing of the past along with the desire to be my own boss, I decided that it was time to start my own company to do data science and freelancing. What I have discovered in the past 3 months of doing so is that going solo is very viable, even during times of layoffs, with proper planning. So I wanted to write about my experience getting started in the hopes that it can help some others out there who might be having a hard time finding the next thing. This is written from a US perspective, so there are some mechanics that will likely be different in other countries, but the general ideas remain the same.\n" +
                "\n" +
                "Before I begin, I want to give a shout out to Brett Trainor, who created an online group called “The Corporate Escapee.” Early in my journey I came across him on TikTok and his videos and other content of his I found really gave me the motivation and courage to take the leap. Check him out!\n" +
                "\n" +
                "\n" +
                "Photo by Etienne Girardet on Unsplash\n" +
                "Taking the leap is the hardest part\n" +
                "Let’s face it. Many (most?) of us are raised with the idea that working for a company means job security. So many (most?) of us, when we move between jobs, are moving between companies rather than opportunities. There can be great stability in working for a company. You are guaranteed certain benefits like a paycheck of a known amount provided at regular intervals. You get other things too like health insurance, career guidance and management, training opportunities, retirement savings, etc. In going out on your own, none of those things are guaranteed. So it is no wonder that many (most?) people think of companies as being the most stable and reliable places to work.\n" +
                "\n" +
                "This is also rooted in history. For a significant portion of the 20th century retirement was based on a pension and your pension was based on how many years you worked for a company and your salary during that time. The idea was that you were guaranteed income from the company after your retirement. In some cases your pension could even be passed down to your children. It was a great deal! But like the dot com collapse in the early 2000’s, many pension programs were dissolved due to bankruptcy, mismanagement of funds, or other catastrophic investment losses, leading to the loss of retirement by hard working employees. The rise of the 401k retirement system was upon us. That system resulted in a retirement income that was portable, meaning you didn’t have to work your entire career for the same employer to have decent retirement savings. Hence, workers no longer have to rely on a company benefit (pension) to be able to retire.";

        ChatGptDiagramGenerator chatGptDiagramGenerator = new ChatGptDiagramGenerator(ARTICLE);

        final String mermaidCode = chatGptDiagramGenerator.generateMermaidCode();

        System.out.println(mermaidCode);
    }
}
