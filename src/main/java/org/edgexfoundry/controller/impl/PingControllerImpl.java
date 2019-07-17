package org.edgexfoundry.controller.impl;

import org.edgexfoundry.controller.PingController;
import org.edgexfoundry.exception.controller.ServiceException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ping")
public class PingControllerImpl implements PingController {

    private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
                    .getEdgeXLogger(PingControllerImpl.class);

    /**
     * Test service providing an indication that the service is available.
     *
     * @throws ServcieException (HTTP 503) for unknown or unanticipated issues
     */
    @Override
    @RequestMapping(method = RequestMethod.GET)
    public String ping() {
        try {
            return "pong";
        } catch (Exception e) {
            logger.error("Error on ping:  " + e.getMessage());
            throw new ServiceException(e);
        }
    }
}
