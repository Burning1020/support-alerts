package org.edgexfoundry.domain;

import org.springframework.web.bind.annotation.RequestMethod;

public class RESTfulChannel extends Channel {
    private String url;
    private String contentType;
    private RequestMethod httpMethod;

    public RESTfulChannel() {
        super.type = ChannelType.REST;
        this.httpMethod = RequestMethod.POST;
    }

    public RESTfulChannel(String url) {
        this();
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public RESTfulChannel(String url, RequestMethod httpMethod) {
        this();
        this.url = url;
        this.httpMethod = httpMethod;
    }

    public RESTfulChannel(String url, String httpMethod) {
        this();
        this.url = url;
        this.httpMethod = RequestMethod.valueOf(httpMethod.toUpperCase());
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequestMethod getHttpMethod() {
        return this.httpMethod;
    }

    public void setHttpMethod(RequestMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public String toString() {
        return "RESTfulChannel{" +
                "url='" + url + '\'' +
                ", contentType='" + contentType + '\'' +
                ", httpMethod=" + httpMethod +
                '}';
    }
}
