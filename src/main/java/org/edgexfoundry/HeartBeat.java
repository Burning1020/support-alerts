package org.edgexfoundry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class HeartBeat {

    private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
            org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(HeartBeat.class);

    @Value("${heart.beat.msg}")
    private String heartBeatMsg;

    @Scheduled(fixedRateString = "${heart.beat.time}")
    public void pulse() {
        logger.info(heartBeatMsg);
    }

}
