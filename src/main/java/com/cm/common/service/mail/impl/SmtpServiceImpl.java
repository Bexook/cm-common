package com.cm.common.service.mail.impl;

import com.cm.common.exception.SystemException;
import com.cm.common.model.enumeration.NotificationType;
import com.cm.common.service.mail.SmtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Slf4j
@Service
public class SmtpServiceImpl implements SmtpService {
    @Value("${mail.send.from}")
    private String from;
    @Value("${mail.smtp.host}")
    private String mailSenderServer;
    @Value("${mail.smtp.username:}")
    private String username;
    @Value("${mail.smtp.password:}")
    private String password;

    private JavaMailSender javaMailSender  =  new JavaMailSenderImpl();

    @Override
    public void sendMessage(final NotificationType notificationType, final String messageBody, final Set<String> recipients) throws MessagingException {
        log.info("Building message with rendered template: {}", messageBody);
        final Session session = getMailSederSession();
        final Transport transport = session.getTransport();
        final InternetAddress[] receiversAddress = recipients.stream().map(r -> {
            try {
                return new InternetAddress(r);
            } catch (AddressException e) {
                log.info("Not able to create Internet Address for {}  user email", r);
                throw new SystemException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }).distinct().toArray(InternetAddress[]::new);
        final MimeMessage message = new MimeMessage(session);
        message.setContent(messageBody, "text/html");
        message.setFrom(new InternetAddress(from));
        message.addRecipients(Message.RecipientType.TO, receiversAddress);
        message.setSubject(notificationType.getSubject());
        transport.connect();
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
        log.info("Sent message successfully....");
    }


    private Session getMailSederSession() {
        final Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", mailSenderServer);
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }
}
