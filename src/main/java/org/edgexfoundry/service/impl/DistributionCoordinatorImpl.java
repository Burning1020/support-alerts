package org.edgexfoundry.service.impl;

import com.alibaba.fastjson.JSON;
import org.edgexfoundry.domain.Channel;
import org.edgexfoundry.domain.ChannelType;
import org.edgexfoundry.domain.Destination;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.service.DistributionCoordinator;

import org.edgexfoundry.service.SendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class DistributionCoordinatorImpl implements DistributionCoordinator {

    private final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());


    @Autowired
    @Qualifier("RESTfulSendingService")
    private SendingService restfulSendingService;

    @Autowired
    @Qualifier("EMAILSendingService")
    private SendingService emailSendingService;

    @Async
    @Override
    public void distribute(String destinationStr) {
        Destination destination = JSON.parseObject(destinationStr, Destination.class);
        if (destination == null) {
            logger.error("DistributionCoordinator received a null destination");
            throw new DataValidationException("Notification is null");
        }
        logger.debug(
                "DistributionCoordinator start distributing destination: " + destination);
        for (Channel channel : destination.getChannels()) {
            sendViaChannel(destination.getContent(), channel);
        }
    }

    @Override
    public void sendViaChannel(String content, Channel channel) {
        logger.debug("sending  to chanel: "
                + channel.toString());
        if (channel.getType() == ChannelType.REST) {
            restfulSendingService.send(content, channel);
        } else if (channel.getType() == ChannelType.EMAIL) {
            emailSendingService.send(content, channel);
        }
    }
}
