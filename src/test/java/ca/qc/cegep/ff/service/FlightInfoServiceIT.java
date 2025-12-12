package ca.qc.cegep.ff.service;

import ca.qc.cegep.ff.AbstractIT;

import org.junit.jupiter.api.Test;

public class FlightInfoServiceIT extends AbstractIT {
    @Test
    void testGetFlightInfo() throws Exception {
        setupAviationMock();
        flightInfoService.fetchAndPublishFlight();
        //verify(mqttOutboundChannel, times(1)).send(any(), anyLong());
    }
}
