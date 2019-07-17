/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: support-notifications
 * @author: Cloud Tsai, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.service.impl;

import org.edgexfoundry.config.MailChannelProperties;
import org.edgexfoundry.domain.notification.Channel;
import org.edgexfoundry.domain.notification.EmailChannel;
import org.edgexfoundry.domain.rule.Destination;
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
    public void send(Destination destination, Channel channel) {
        checkChannel(channel);

        logger.info("EMAILSendingService is starting sending notification: receiver="
                + destination.getReceiver() + " to channel: " + channel.toString());

        EmailChannel emailChannel = (EmailChannel) channel;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mailChannelProps.getSender());
        msg.setSubject(mailChannelProps.getSubject());
        msg.setTo(emailChannel.getMailAddresses());
        msg.setText(destination.getContent());

        logger.debug("sending mailConfig to " + Arrays.toString(emailChannel.getMailAddresses()));
        logger.debug("mailConfig content is: " + destination.getContent());

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
