package org.edgexfoundry.domain.notification;

import java.util.Arrays;

public class EmailChannel extends Channel {
    private String[] mailAddresses;

    public EmailChannel() {
        super.type = ChannelType.EMAIL;
    }

    public String[] getMailAddresses() {
        return this.mailAddresses;
    }

    public void setMailAddresses(String[] mailAddresses) {
        this.mailAddresses = mailAddresses;
    }

    public String toString() {
        return "EmailChannel [mailAddresses=" + Arrays.toString(this.mailAddresses) + "]";
    }
}
