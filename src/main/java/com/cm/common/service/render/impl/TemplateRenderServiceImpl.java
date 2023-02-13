package com.cm.common.service.render.impl;

import com.cm.common.model.enumeration.NotificationType;
import com.cm.common.service.render.TemplateRenderService;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Service
public class TemplateRenderServiceImpl implements TemplateRenderService {


    private Context context = new Context();

    @Override
    public String renderTemplate(final NotificationType notificationType, final Context context) {
        return templateEngine().process(notificationType.getTemplateName(), context);
    }

    private SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(templateResolver());
        return templateEngine;
    }

    private ITemplateResolver templateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("template/email/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(1);
        return resolver;
    }


}
