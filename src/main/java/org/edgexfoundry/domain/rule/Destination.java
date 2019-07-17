package org.edgexfoundry.domain.rule;

import org.springframework.stereotype.Component;

@Component
public class Destination {

    private String receiver;
    private String content;
    private String contentType;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    @Override
    public String toString() {
        return "Destination{" +
                "receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
