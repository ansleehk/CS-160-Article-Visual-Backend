package edu.sjsu.articlevisualisationbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class ArticleVisualisationBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArticleVisualisationBackendApplication.class, args);
	}

}
