package com.example.demo_fixerio.config.mail;

public final class Constants {
    private Constants() {
        throw new UnsupportedOperationException();
    }

    public static final boolean HTML_TEXT = true;

    //<editor-fold desc="Thymeleaf">
    public static final String THYMELEAF_TEMPLATE_PREFIX = "mail/";
    public static final String THYMELEAF_TEMPLATE_SUFFIX = ".html";
    //</editor-fold>

    //<editor-fold desc="Email">
    public static final String PROP_MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    public static final String PROP_MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String PROP_MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String PROP_MAIL_SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";
    //</editor-fold>

    //email subject constants
    public static final String GET_EXCHANGE_RATE_ERROR_SUBJECT = "EWM - Failed to get exchange rates";

    //email template name constants
    public static final String GET_FIXERIO_EXCHANGE_RATE_ERROR = "get-fixerio-exchange-rate-error";
}
