package com.mediinbusan.backend;

import com.mediinbusan.backend.diagnosischat.client.GeminiProperties;
import com.mediinbusan.backend.document.client.ClovaOcrProperties;
import com.mediinbusan.backend.document.client.PapagoTranslationProperties;
import com.mediinbusan.backend.document.validation.DocumentOcrProperties;
import com.mediinbusan.backend.wellness.service.WellnessIngestionProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    WellnessIngestionProperties.class,
    ClovaOcrProperties.class,
    DocumentOcrProperties.class,
    PapagoTranslationProperties.class,
    GeminiProperties.class
})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
