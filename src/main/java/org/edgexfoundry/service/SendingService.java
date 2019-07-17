package org.edgexfoundry.service;

import org.edgexfoundry.domain.Channel;

public interface SendingService {

    public void send(String content, Channel channel);

}
