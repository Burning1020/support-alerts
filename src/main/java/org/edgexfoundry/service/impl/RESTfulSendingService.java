/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: support-notifications
 * @author: Cloud Tsai, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.service.impl;

import org.edgexfoundry.domain.notification.Channel;
import org.edgexfoundry.domain.notification.RESTfulChannel;
import org.edgexfoundry.domain.rule.Destination;
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
    public void send(Destination destination, Channel channel) {
        checkChannel(channel);

        RESTfulChannel restfulChannel = (RESTfulChannel) channel;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(destination.getContentType() == null ? MediaType.TEXT_PLAIN
                : MediaType.parseMediaType(destination.getContentType()));
        HttpEntity<String> request = new HttpEntity<>(destination.getContent(), headers);

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
