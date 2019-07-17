package org.edgexfoundry.service.impl;

import org.edgexfoundry.domain.Channel;
import org.edgexfoundry.domain.RESTfulChannel;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.service.SendingService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service("RESTfulSendingService")
public class RESTfulSendingService implements SendingService {

    private final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

    @Override
    public void send(String content, Channel channel) {
        checkChannel(channel);

        logger.info("RESTfulSendingService is starting sending "
                + content + " to channel: " + channel.toString());


        RESTfulChannel restfulChannel = (RESTfulChannel) channel;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(restfulChannel.getContentType() == null ? MediaType.TEXT_PLAIN
                : MediaType.parseMediaType(restfulChannel.getContentType()));
        HttpEntity<String> request = new HttpEntity<>(content, headers);

        logger.debug("sending notification to " + restfulChannel.getUrl() + " by HTTP "
                + restfulChannel.getHttpMethod() + " method");

        try {
            ResponseEntity<String> response = restTemplate.exchange(URI.create(restfulChannel.getUrl()),
                    HttpMethod.resolve(restfulChannel.getHttpMethod().toString()), request, String.class);

            logger.debug("got response status code: " + response.getStatusCode() + " with content: "
                    + response.getBody());

        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected void checkChannel(Channel channel) {
        if (!(channel instanceof RESTfulChannel)) {
            logger.error("RESTfulSendingService got an incorrect channel: " + channel);
            throw new DataValidationException(channel + " is not an RESTful channel");
        }

        RESTfulChannel restfulChannel = (RESTfulChannel) channel;

        if (restfulChannel.getUrl() == null || restfulChannel.getHttpMethod() == null) {
            logger.error("RESTfulSendingService got an incorrect channel: " + channel);
            throw new DataValidationException("RESTfulChannel contains null properties");
        }

    }
}
