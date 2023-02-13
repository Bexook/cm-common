package com.cm.common.service.render;

import com.cm.common.model.enumeration.NotificationType;
import org.thymeleaf.context.Context;

public interface TemplateRenderService {

    String renderTemplate(final NotificationType notificationType, final Context context);

}
