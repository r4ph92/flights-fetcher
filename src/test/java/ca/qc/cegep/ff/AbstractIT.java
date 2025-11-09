package ca.qc.cegep.ff;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import ca.qc.cegep.ff.aviation.AviationEdgeService;
import ca.qc.cegep.ff.aviation.model.Aircraft;
import ca.qc.cegep.ff.aviation.model.Codes;
import ca.qc.cegep.ff.aviation.model.Flight;
import ca.qc.cegep.ff.aviation.model.FlightResponse;
import ca.qc.cegep.ff.aviation.model.Geography;
import ca.qc.cegep.ff.aviation.model.Speed;
import ca.qc.cegep.ff.aviation.model.System;
import ca.qc.cegep.ff.mqtt.MqttPublisher;
import ca.qc.cegep.ff.service.FlightInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;

import wiremock.org.apache.hc.core5.http.HttpHeaders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                classes = {
                        AbstractIT.TestConfig.class
                },
                properties = {
                        "aviation-edge.api-key=TDB",
                        "aviation-edge.api-context=/api",
                        "aviation-edge.host=localhost:${wiremock.server.port}",
                        "aviation-edge.protocol=http",
                        "mqtt.username=pubSub",
                        "mqtt.flights-topic=topic/iot/flights/test"
                })
@AutoConfigureWireMock(port = 0)
public class AbstractIT {
    @Autowired
    protected FlightInfoService flightInfoService;
    @Autowired
    protected AviationEdgeService aviationEdgeService;
    @Autowired
    protected MqttPublisher mqttPublisher;

    @MockitoSpyBean
    protected MessageChannel mqttOutboundChannel;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected List<FlightResponse> setupAviationMock() throws Exception {
        List<FlightResponse> expectedResponse = List.of(buildFlightResponse());

        stubFor(get(urlMatching("/api/flights.*")).willReturn(aResponse()
                .withStatus(200)
                .withHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .withBody(objectMapper.writeValueAsString(expectedResponse))));
        return expectedResponse;
    }

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public WireMockConfigurationCustomizer customizer() {
            return config -> config.extensions(
                    new ResponseTemplateTransformer(true),
                    new ResponseDefinitionTransformer() {
                        @Override
                        public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, FileSource files, Parameters parameters) {
                            // Prevent connections to Wiremock from being re-used by the http client connection pool by adding a header to Wiremock responses
                            return ResponseDefinitionBuilder.like(responseDefinition)
                                    .withHeader(HttpHeaders.CONNECTION, "close")
                                    .build();
                        }

                        @Override
                        public String getName() {
                            return "keep-alive-disabler";
                        }
                    });
        }
    }

    private FlightResponse buildFlightResponse() {
        return FlightResponse.builder()
                .status("en-route")
                .aircraft(Aircraft.builder()
                        .icao24("78160E")
                        .regNumber("B-208X")
                        .iataCode("B789")
                        .icaoCode("B789")
                        .build())
                .airline(Codes.builder()
                        .iataCode("FM")
                        .icaoCode("CSH")
                        .build())
                .arrival(Codes.builder()
                        .iataCode("PVG")
                        .icaoCode("ZSPD")
                        .build())
                .departure(Codes.builder()
                        .iataCode("MEL")
                        .icaoCode("YMML")
                        .build())
                .flight(Flight.builder()
                        .iataNumber("MU740")
                        .icaoNumber("CES740")
                        .number("740")
                        .build())
                .geography(Geography.builder()
                        .altitude(7604.76)
                        .direction(333.92)
                        .latitude(-36.5964)
                        .longitude(143.667)
                        .build())
                .speed(Speed.builder()
                        .horizontal(880.452)
                        .isGround(0D)
                        .vspeed(22.248)
                        .build())
                .system(System.builder()
                        .squawk(1247)
                        .updated(1737027892L)
                        .build())
                .build();
    }
}
