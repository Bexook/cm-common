package com.cm.common.service.notification;

import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.model.enumeration.NotificationType;

import java.net.UnknownHostException;

public interface NotificationService {

    void generateAndSendTokenMessage(final AppUserDTO appUser, final NotificationType notificationType);



}

