package org.edgexfoundry.service.impl;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import org.edgexfoundry.domain.core.Event;
//import org.edgexfoundry.service.RuleEngine;
import org.edgexfoundry.service.RuleEngine;
import org.edgexfoundry.service.ZeroMQEventSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

/**
 * Export data message ingestion bean - gets messages out of ZeroMQ from export service.
 */
@Component
public class ZeroMQEventSubscriberImpl implements ZeroMQEventSubscriber {

    private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
                    .getEdgeXLogger(ZeroMQEventSubscriberImpl.class);

    @Value("${zeromq.port}")
    private String zeromqAddressPort;
    @Value("${zeromq.host}")
    private String zeromqAddress;

    @Autowired
    RuleEngine engine;

    private ZMQ.Socket subscriber;
    private ZMQ.Context context;

    {
        context = ZMQ.context(1);
    }

    @Override
    public void receive() {
        getSubscriber();
        byte[] exportBytes = null;
        Event event;
        logger.info("Watching for new exported Event messages...");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                exportBytes = subscriber.recv();
                event = toEvent(exportBytes);
                engine.execute(event);
                logger.info("Event sent to rules engine for device" + event.getDevice());
            }
        } catch (Exception e) {
            logger.error("Unable to receive messages via ZMQ: " + e.getMessage());
        }
        logger.error("Shutting off Event message watch due to error!");
        if (subscriber != null)
            subscriber.close();
        subscriber = null;
        // try to restart
        logger.debug("Attempting restart of Event message watch.");
        receive();
    }

    public String getZeromqAddress() {
        return zeromqAddress;
    }

    public void setZeromqAddress(String zeromqAddress) {
        this.zeromqAddress = zeromqAddress;
    }

    public String getZeromqAddressPort() {
        return zeromqAddressPort;
    }

    public void setZeromqAddressPort(String zeromqAddressPort) {
        this.zeromqAddressPort = zeromqAddressPort;
    }

    private ZMQ.Socket getSubscriber() {
        if (subscriber == null) {
            try {
                subscriber = context.socket(ZMQ.SUB);
                subscriber.connect(zeromqAddress + ":" + zeromqAddressPort);
                subscriber.subscribe("".getBytes());
            } catch (Exception e) {
                logger.error("Unable to get a ZMQ subscriber.  Error:  " + e);
                subscriber = null;
            }
        }
        return subscriber;
    }

    private static Event toEvent(byte[] eventBytes) throws IOException, ClassNotFoundException {
        try {
            Gson gson = new Gson();
            String json = new String(eventBytes);
            return gson.fromJson(json, Event.class);
        } catch (Exception e) {
            // Try to degrade to deprecated serialization functionality gracefully
            ByteArrayInputStream bis = new ByteArrayInputStream(eventBytes);
            ObjectInput in = new ObjectInputStream(bis);
            Event event = (Event) in.readObject();
            return event;
        }
    }
}
