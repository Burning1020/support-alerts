package org.edgexfoundry.service;

public interface CommandExecutor {

    public void SendTo(String receiver, String contextType, String context);

}
