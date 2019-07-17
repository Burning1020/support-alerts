package org.edgexfoundry.service;


import org.edgexfoundry.domain.Channel;

public interface DistributionCoordinator {

    public void distribute(String destinationStr);

    public void sendViaChannel(String content, Channel channel);

}
