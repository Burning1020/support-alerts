package org.edgexfoundry.service.impl;

import com.alibaba.fastjson.JSON;
import org.edgexfoundry.domain.rule.Destination;
import org.edgexfoundry.service.CommandExecutor;
import org.edgexfoundry.service.DistributionCoordinator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service("commandExecutor")
public class CommandExecutorImpl implements CommandExecutor {

    @Autowired
    private DistributionCoordinator distributionCoordinator;

    @Autowired
    private Destination destination;

    private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
                    .getEdgeXLogger(CommandExecutorImpl.class);

    @Async
    @Override
    public void SendTo(String receiver, String contextType, String context) {
        destination.setReceiver(receiver);
        destination.setContentType(contextType);
        destination.setContent(context);
        try {
            distributionCoordinator.distribute(destination);
        } catch (Exception exception) {
            logger.error("Problem sending notification " + exception);
        }
    }
}
