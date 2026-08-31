package com.mediinbusan.backend.legal;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
public class PrivacyPolicyController {

    private static final MediaType HTML_UTF8 = new MediaType(
        MediaType.TEXT_HTML,
        StandardCharsets.UTF_8
    );

    private final Resource privacyPolicy = new ClassPathResource("static/privacy.html");

    @GetMapping(value = "/privacy", produces = "text/html;charset=UTF-8")
    public ResponseEntity<Resource> privacyPolicy() {
        return ResponseEntity.ok()
            .contentType(HTML_UTF8)
            .body(privacyPolicy);
    }
}
