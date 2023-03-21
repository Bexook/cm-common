package com.cm.common.service.mail.impl;

import com.cm.common.exception.SystemException;
import com.cm.common.model.enumeration.NotificationType;
import com.cm.common.service.mail.SmtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Set;

@Slf4j
@Service
public class SmtpServiceImpl implements SmtpService {

    @Value("${mail.send.from: andrik.mykytyn@gmail.com}")
    private String from;
    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private Integer port;
    @Value("${mail.smtp.username:}")
    private String username;
    @Value("${mail.smtp.password:}")
    private String password;

    @Override
    public void sendMessage(final NotificationType notificationType, final String messageBody, final Set<String> recipients) throws MessagingException {
        log.info("Building message with rendered template: {}", messageBody);
        final JavaMailSender sender = getMailSederSession();
        final InternetAddress[] receiversAddress = recipients.stream().map(r -> {
            try {
                return new InternetAddress(r);
            } catch (AddressException e) {
                log.info("Not able to create Internet Address for {}  user email", r);
                throw new SystemException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }).distinct().toArray(InternetAddress[]::new);
        final MimeMessage message = sender.createMimeMessage();
        message.setContent(messageBody, "text/html");
        message.setFrom(new InternetAddress(from));
        message.addRecipients(Message.RecipientType.TO, receiversAddress);
        message.setSubject(notificationType.getSubject());
        sender.send(message);
        log.info("Sent message successfully....");
    }


    private JavaMailSender getMailSederSession() {
        final JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setPassword(password);
        sender.setUsername(username);
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        return sender;
    }
}
