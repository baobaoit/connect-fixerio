package com.example.demo_fixerio.mail.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private String emailFrom;
    private Set<String> emailTo;
    private String subject;
    private String templateName;
    private Map<String, Object> templateProperties;
}
