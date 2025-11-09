package com.flightsfetcher.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightsfetcher.model.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MqttPublisherService {

    private static final Logger logger = LoggerFactory.getLogger(MqttPublisherService.class);

    @Autowired
    private MessageChannel mqttOutboundChannel;

    @Value("${mqtt.topic}")
    private String topic;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Publish a single flight to MQTT topic
     */
    public void publishFlight(Flight flight) {
        try {
            String json = objectMapper.writeValueAsString(flight);
            mqttOutboundChannel.send(MessageBuilder.withPayload(json).build());
            logger.debug("Published flight {} to MQTT topic {}", flight.getIcao24(), topic);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing flight to JSON: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error publishing flight to MQTT: {}", e.getMessage());
        }
    }

    /**
     * Publish multiple flights to MQTT topic
     */
    public void publishFlights(List<Flight> flights) {
        if (flights == null || flights.isEmpty()) {
            logger.info("No flights to publish");
            return;
        }

        logger.info("Publishing {} flights to MQTT topic {}", flights.size(), topic);
        flights.forEach(this::publishFlight);
        logger.info("Successfully published {} flights", flights.size());
    }
}
