package com.cm.common.model.enumeration;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;

public enum NotificationType {

    ACCOUNT_ACTIVATION("account-activation-email-template", "account.activation.email.subject"),
    ACCOUNT_DELETION_WARNING("account-deletion-warning-template", "account.deletion.warning.email.subject"),
    PASSWORD_REST("password-reset-email-template", "Password reset notification");

    private String templateName;
    private String subject;

    NotificationType(final String templateName, final String subject) {
        this.templateName = templateName;
        this.subject = subject;
    }

    public String getTemplateName() {
        return templateName;
    }

    @SneakyThrows
    public String getSubject() {
        InputStream resource = new ClassPathResource("template/text/email-text.properties").getInputStream();
        Properties props = new Properties();
        props.load(resource);
        return props.getProperty(this.subject);
    }
}
