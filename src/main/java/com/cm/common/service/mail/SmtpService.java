package com.cm.common.service.mail;

import com.cm.common.model.enumeration.NotificationType;

import javax.mail.MessagingException;
import java.util.Set;

public interface SmtpService {

    void sendMessage(final NotificationType notificationType, final String messageBody, final Set<String> recipient) throws MessagingException;

}
