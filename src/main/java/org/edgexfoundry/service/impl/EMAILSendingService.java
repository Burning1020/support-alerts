package org.edgexfoundry.service.impl;

import org.edgexfoundry.config.MailChannelProperties;
import org.edgexfoundry.domain.Channel;
import org.edgexfoundry.domain.EmailChannel;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.service.SendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service("EMAILSendingService")
public class EMAILSendingService implements SendingService {

    private final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

    @Autowired
    private MailChannelProperties mailChannelProps;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void send(String content, Channel channel) {
        checkChannel(channel);

        logger.info("EMAILSendingService is starting sending "
                + content + " to channel: " + channel.toString());

        EmailChannel emailChannel = (EmailChannel) channel;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mailChannelProps.getSender());
        msg.setSubject(mailChannelProps.getSubject());
        msg.setTo(emailChannel.getMailAddresses());
        msg.setText(content);

        logger.debug("sending mailConfig to " + Arrays.toString(emailChannel.getMailAddresses()));
        logger.debug("mailConfig content is: " + content);

        try {
            mailSender.send(msg);
        } catch (MailException e) {
            logger.error(e.getMessage(), e);
        }

        logger.debug("the mailConfig is sent to the SMTP server");
    }

    protected void checkChannel(Channel channel) {
        if (!(channel instanceof EmailChannel)) {
            logger.error("EMAILSendingService got an incorrect channel: " + channel);
            throw new DataValidationException(channel + " is not an email channel");
        }

        EmailChannel emailChannel = (EmailChannel) channel;
        if (emailChannel.getMailAddresses() == null) {
            logger.error("EMAILSendingService got a null emaill address");
            throw new DataValidationException(channel + " contains null email address");
        }
    }

}
