package com.cm.common.service.notification.impl;

import com.cm.common.exception.SystemException;
import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.model.enumeration.NotificationType;
import com.cm.common.model.enumeration.TokenType;
import com.cm.common.service.mail.SmtpService;
import com.cm.common.service.notification.NotificationService;
import com.cm.common.service.render.TemplateRenderService;
import com.cm.common.service.token.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final String ACTIVATION_SOURCE_PATH = "/api/user/account/activate/{activationToken}";

    private final String RESET_PASSWORD_SOURCE_PATH = "/api/user/account/activate/{activationToken}";
    @Value("${client-application.url}")
    private String clientUrl;
    @Value("${client-application.port}")
    private Integer clientPort;
    @Autowired
    private TemplateRenderService templateRenderService;
    @Autowired
    private SmtpService smtpService;
    @Autowired
    private TokenService tokenService;

    private Map<NotificationType, NotificationMessageParametersBuilder> notificationMessageParamsBuilder = new HashMap<>();

    {
        notificationMessageParamsBuilder.put(NotificationType.ACCOUNT_ACTIVATION, this::generateAccountActivationMessageParams);
        notificationMessageParamsBuilder.put(NotificationType.ACCOUNT_DELETION_WARNING, this::generateDeletionAccountWarningMessageParams);
        notificationMessageParamsBuilder.put(NotificationType.PASSWORD_REST, this::generatePasswordResetMessageParams);
    }

    @Override
    @PreAuthorize("@userAccessValidation.isAdmin() || @userAccessValidation.scheduledJob() || @userAccessValidation.isAnonymous()")
    public void generateAndSendTokenMessage(final AppUserDTO appUser, final NotificationType notificationType) {
        final Context context = new Context();
        notificationMessageParamsBuilder.get(notificationType).buildParams(appUser, context);
        final String emailBody = templateRenderService.renderTemplate(notificationType, context);
        try {
            smtpService.sendMessage(notificationType, emailBody, Set.of(appUser.getEmail()));
        } catch (final MessagingException e) {
            log.info("Activation message sending to user: {} FAILED. With body: {}", appUser.getEmail(), emailBody);
            throw new SystemException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private void generateDeletionAccountWarningMessageParams(final AppUserDTO appUser, final Context context) {
        final String token = tokenService.getTokenByUserId(appUser.getId(), TokenType.ACCOUNT_ACTIVATION_TOKEN);
        final String accountActivationUrl = generateUrlWithParams(ACTIVATION_SOURCE_PATH, Map.of("activationToken", token));
        context.setVariables(Map.of("username", appUser.getFirstName(),
                "activationLink", accountActivationUrl));
    }


    private void generateAccountActivationMessageParams(final AppUserDTO appUser, final Context context) {
        final String token = tokenService.generateToken(appUser, TokenType.ACCOUNT_ACTIVATION_TOKEN);
        final String accountActivationUrl = generateUrlWithParams(ACTIVATION_SOURCE_PATH, Map.of("activationToken", token));
        context.setVariables(Map.of("username", appUser.getFirstName(),
                "activationLink", accountActivationUrl));
    }

    private void generatePasswordResetMessageParams(final AppUserDTO appUser, final Context context) {
        final String token = tokenService.generateToken(appUser, TokenType.PASSWORD_RESET_TOKEN);
        final String passwordResetUrl = generateUrlWithParams(RESET_PASSWORD_SOURCE_PATH, Map.of("resetToken", token));
        context.setVariables(Map.of("username", appUser.getFirstName(),
                "resetPasswordLink", passwordResetUrl));
    }

    private String generateUrlWithParams(final String sourcePath, final Map<String, Object> params) {
        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .port(clientPort)
                .host(clientUrl)
                .path(sourcePath)
                .build(params)
                .toString();
    }


    @FunctionalInterface
    interface NotificationMessageParametersBuilder {

        void buildParams(final AppUserDTO appUser, final Context context);

    }
}
