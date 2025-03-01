package com.example.demo_fixerio.mail;

import com.example.demo_fixerio.mail.model.EmailMessage;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public final class EmailMessageHelper {
    private EmailMessageHelper() {
        throw new UnsupportedOperationException();
    }

    public static EmailFromBuilder builder() {
        return new Builder();
    }

    public interface EmailFromBuilder {
        EmailToBuilder emailFrom(String emailFrom);
    }

    public interface EmailToBuilder {
        SubjectBuilder emailTo(Set<String> emailTo);
    }

    public interface SubjectBuilder {
        TemplateNameBuilder subject(Supplier<String> subjectSupplier);
    }

    public interface TemplateNameBuilder {
        TemplatePropertiesBuilder templateName(String templateName);
    }

    public interface TemplatePropertiesBuilder {
        EmailMessageBuild templateProperties(Supplier<Map<String, Object>> templatePropertiesSupplier);
    }

    public interface EmailMessageBuild {
        EmailMessage build();
    }

    public static class Builder implements EmailFromBuilder, EmailToBuilder, SubjectBuilder, TemplateNameBuilder,
            TemplatePropertiesBuilder, EmailMessageBuild {

        private String emailFrom;
        private Set<String> emailTo;
        private String subject;
        private String templateName;
        private Map<String, Object> templateProperties;

        @Override
        public EmailToBuilder emailFrom(String emailFrom) {
            this.emailFrom = emailFrom;
            return this;
        }

        @Override
        public SubjectBuilder emailTo(Set<String> emailTo) {
            this.emailTo = emailTo;
            return this;
        }

        @Override
        public TemplateNameBuilder subject(Supplier<String> subjectSupplier) {
            Objects.requireNonNull(subjectSupplier);
            this.subject = subjectSupplier.get();
            return this;
        }

        @Override
        public TemplatePropertiesBuilder templateName(String templateName) {
            this.templateName = templateName;
            return this;
        }

        @Override
        public EmailMessageBuild templateProperties(Supplier<Map<String, Object>> templatePropertiesSupplier) {
            Objects.requireNonNull(templatePropertiesSupplier);
            this.templateProperties = templatePropertiesSupplier.get();
            return this;
        }

        @Override
        public EmailMessage build() {
            return EmailMessage.builder()
                    .emailFrom(emailFrom)
                    .emailTo(emailTo)
                    .subject(subject)
                    .templateName(templateName)
                    .templateProperties(templateProperties)
                    .build();
        }
    }
}
