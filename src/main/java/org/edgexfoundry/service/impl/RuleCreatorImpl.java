package org.edgexfoundry.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.alibaba.fastjson.JSON;
import org.edgexfoundry.domain.*;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.service.RuleCreator;
import org.springframework.beans.factory.annotation.Value;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.stereotype.Service;

import static com.alibaba.fastjson.serializer.SerializerFeature.UseSingleQuotes;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteClassName;

@Service("ruleCreator")
public class RuleCreatorImpl implements RuleCreator {

    private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(RuleCreatorImpl.class);

    @Value("${rules.template.path}")
    private String templateLocation;

    @Value("${rules.template.name}")
    private String templateName;

    @Value("${rules.template.encoding}")
    private String templateEncoding;

    private Configuration cfg;

    @PostConstruct
    @Override
    public void init() {
        try {
            cfg = new Configuration(Configuration.getVersion());
            cfg.setDirectoryForTemplateLoading(new File(templateLocation));
            cfg.setDefaultEncoding(templateEncoding);
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        } catch (IOException e) {
            logger.error("Problem getting rule template location." + e.getMessage());
            throw new ServiceException(e);
        }
    }

    private Map<String, Object> createMap(Rule rule) {
        Map<String, Object> map = new HashMap<>();
        map.put("ruleName", rule.getName());
        map.put("condDevice", rule.getCondition().getDevice());
        map.put("valueChecks", rule.getCondition().getChecks());
        String destination = JSON.toJSONString(rule.getDestination(), UseSingleQuotes, WriteClassName);
        map.put("destination", destination);
//        Channel channel = rule.getDestination().getChannel();
//        if (channel.getType() == ChannelType.EMAIL) {
//            EmailChannel emailChannel = (EmailChannel) channel;
//            map.put("type", "EMAIL");
//            map.put("mailAddress", emailChannel.getMailAddress());
//        } else if (channel.getType() == ChannelType.REST) {
//            RESTfulChannel resTfulChannel = (RESTfulChannel) channel;
//            map.put("type", "REST");
//            map.put("url", resTfulChannel.getUrl());
//            map.put("httpMethod", resTfulChannel.getHttpMethod());
//            map.put("contentType", resTfulChannel.getContentType());
//        }
        map.put("log", rule.getLog());
        return map;
    }


    public String createDroolRule(Rule rule) {
        try {
            Template temp = cfg.getTemplate(templateName);
            Writer out = new StringWriter();
            temp.process(createMap(rule), out);
            return out.toString();
        } catch (IOException iE) {
            logger.error("Problem getting rule template file." + iE.getMessage());
            throw new ServiceException(iE);
        } catch (TemplateException tE) {
            logger.error("Problem writing Drool rule." + tE.getMessage());
            throw new ServiceException(tE);
        } catch (Exception e) {
            logger.error("Problem creating rule: " + e.getMessage());
            throw e;
        }
    }
}
