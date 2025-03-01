package com.example.demo_fixerio.mail.core;

import com.example.demo_fixerio.mail.model.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static com.example.demo_fixerio.config.mail.Constants.HTML_TEXT;

@Component
public class SimpleEmailSenderImpl implements SimpleEmailSender {

    private JavaMailSender javaMailSender;
    private TemplateEngine templateEngine;

    @Override
    @Async
    public void sendEmail(EmailMessage emailMessage) throws MessagingException {
        final AbstractContext context = new Context(Locale.ENGLISH);
        context.setVariables(emailMessage.getTemplateProperties());

        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
        message.setSubject(emailMessage.getSubject());
        message.setFrom(emailMessage.getEmailFrom());
        message.setTo(emailMessage.getEmailTo().toArray(new String[0]));

        final String text = templateEngine.process(emailMessage.getTemplateName(), context);
        message.setText(text, HTML_TEXT);

        javaMailSender.send(mimeMessage);
    }

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Autowired
    public void setTemplateEngine(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
}
