package org.edgexfoundry.controller;

public interface PingController {

    /**
     * Test service providing an indication that the service is available.
     *
     * @throws ServcieException (HTTP 503) for unknown or unanticipated issues
     */
    String ping();
}
