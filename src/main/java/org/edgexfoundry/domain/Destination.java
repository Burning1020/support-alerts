package org.edgexfoundry.domain;

import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Destination {

    private String content;
    private Channel[] channels;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Channel[] getChannels() {
        return channels;
    }

    public void setChannels(Channel[] channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "content='" + content + '\'' +
                ", channels=" + Arrays.toString(channels) +
                '}';
    }
}
