package ca.qc.cegep.ff.aviation;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import ca.qc.cegep.ff.AbstractIT;
import ca.qc.cegep.ff.aviation.model.FlightResponse;

import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.List;

public class AviationEdgeServiceIT extends AbstractIT {

    @Value("classpath:/aviationEdgeResponse.json")
    private Resource aviationEdgeResponseJson;

    @Test
    void testGetFlights() throws Exception {
        List<FlightResponse> expectedResponse = setupAviationMock();
        List<FlightResponse> response = aviationEdgeService.getFlights(5);
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(expectedResponse.getFirst(), response.getFirst());
    }

    @Test
    void testGetFlightsFull() throws Exception {
        stubFor(get(urlMatching("/api/flights.*")).willReturn(aResponse()
                .withStatus(200)
                .withHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .withBody(aviationEdgeResponseJson.getInputStream().readAllBytes())));

        List<FlightResponse> response = aviationEdgeService.getFlights(5);
        assertNotNull(response);
        assertEquals(20, response.size());
    }
}
