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
 * @microservice: support-destinations
 * @author: Cloud Tsai, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.service.impl;

import org.edgexfoundry.dao.SubscriptionDAO;
import org.edgexfoundry.domain.notification.Channel;
import org.edgexfoundry.domain.notification.ChannelType;
import org.edgexfoundry.domain.notification.Subscription;
import org.edgexfoundry.domain.rule.Destination;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.service.DistributionCoordinator;

import org.edgexfoundry.service.SendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DistributionCoordinatorImpl implements DistributionCoordinator {

    private final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

    @Autowired
    private SubscriptionDAO subscriptionDAO;

    @Autowired
    @Qualifier("RESTfulSendingService")
    private SendingService restfulSendingService;

    @Autowired
    @Qualifier("EMAILSendingService")
    private SendingService emailSendingService;

    @Async
    @Override
    public void distribute(Destination destination) {
        if (destination == null) {
            logger.error("DistributionCoordinator received a null object");
            throw new DataValidationException("Notification is null");
        }
        logger.debug(
                "DistributionCoordinator start distributing destination: " + destination.toString());

        Subscription subscription;
        try {
            subscription = subscriptionDAO
                    .findByReceiverIgnoreCase(destination.getReceiver());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
        sendViaChannel(destination, subscription.getChannel());
    }

    @Override
    public void sendViaChannel(Destination destination, Channel channel) {
        if (channel == null) {
            logger.error("DistributionCoordinator received a null channel");
            throw new DataValidationException("channel is null");
        }
        logger.debug("sending destination receiver=" + destination.getReceiver() + " to chanel: "
                + channel.toString());
        if (channel.getType() == ChannelType.REST) {
            restfulSendingService.send(destination, channel);
        } else if (channel.getType() == ChannelType.EMAIL) {
            emailSendingService.send(destination, channel);
        }
    }
}
