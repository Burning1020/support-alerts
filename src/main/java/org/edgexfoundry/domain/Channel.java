package org.edgexfoundry.domain;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import java.util.Iterator;

@JsonTypeInfo(
        use = Id.NAME,
        property = "type"
)
@JsonSubTypes({@Type(
        value = RESTfulChannel.class,
        name = "REST"
), @Type(
        value = EmailChannel.class,
        name = "EMAIL"
)})
public abstract class Channel {
    protected ChannelType type;

    public Channel() {
    }

    public ChannelType getType() {
        return this.type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

}
