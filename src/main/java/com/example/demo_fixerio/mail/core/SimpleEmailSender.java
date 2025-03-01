package com.example.demo_fixerio.mail.core;

import com.example.demo_fixerio.mail.model.EmailMessage;

import javax.mail.MessagingException;


public interface SimpleEmailSender {
    void sendEmail(EmailMessage emailMessage) throws MessagingException;
}
