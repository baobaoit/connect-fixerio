package com.example.demo_fixerio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static com.example.demo_fixerio.config.mail.Constants.PROP_MAIL_SMTP_AUTH;
import static com.example.demo_fixerio.config.mail.Constants.PROP_MAIL_SMTP_STARTTLS_ENABLE;
import static com.example.demo_fixerio.config.mail.Constants.PROP_MAIL_SMTP_STARTTLS_REQUIRED;
import static com.example.demo_fixerio.config.mail.Constants.PROP_MAIL_TRANSPORT_PROTOCOL;

@Configuration
public class JavaMailSenderConfiguration {

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String userName;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties." + PROP_MAIL_TRANSPORT_PROTOCOL + "}")
    private String mailTransportProtocol;

    @Value("${spring.mail.properties." + PROP_MAIL_SMTP_AUTH + "}")
    private String mailSmtpAuth;

    @Value("${spring.mail.properties." + PROP_MAIL_SMTP_STARTTLS_ENABLE + "}")
    private String mailSmtpStartTlsEnable;

    @Value("${spring.mail.properties." + PROP_MAIL_SMTP_STARTTLS_REQUIRED + "}")
    private String mailSmtpStartTlsRequired;

    @Value("${spring.mail.protocol}")
    private String mailProtocol;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);

        mailSender.setUsername(userName);
        mailSender.setPassword(password);

        Properties properties = mailSender.getJavaMailProperties();
        properties.setProperty(PROP_MAIL_TRANSPORT_PROTOCOL, mailTransportProtocol);
        properties.setProperty(PROP_MAIL_SMTP_AUTH, mailSmtpAuth);
        properties.setProperty(PROP_MAIL_SMTP_STARTTLS_ENABLE, mailSmtpStartTlsEnable);
        properties.setProperty(PROP_MAIL_SMTP_STARTTLS_REQUIRED, mailSmtpStartTlsRequired);

        mailSender.setProtocol(mailProtocol);

        return mailSender;
    }
}
