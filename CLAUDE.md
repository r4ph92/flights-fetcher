# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Flights Fetcher is a Spring Boot application that fetches flight information for given coordinates using the Aviation Edge API and publishes the data to an MQTT broker. The application runs scheduled jobs and provides REST endpoints for dynamic coordinate updates.

## Build and Test Commands

```bash
# Build the project
./mvnw clean install

# Run tests
./mvnw test

# Run integration tests only
./mvnw test -Dtest="*IT"

# Run a specific test
./mvnw test -Dtest=FlightInfoServiceIT

# Run the application
./mvnw spring-boot:run

# Run with local profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Package as JAR
./mvnw package
```

## Architecture

### Core Components

**FlightFetcher (job/FlightFetcher.java)**
- Scheduled job that runs at intervals defined by `default.job-rate` property (default: 60000ms)
- Maintains current coordinates (lat/lng) initialized from Config
- Coordinates can be updated dynamically via REST endpoint
- Delegates to FlightInfoService for actual flight fetching and publishing

**FlightInfoService (service/FlightInfoService.java)**
- Orchestrates flight data retrieval and MQTT publishing
- Implements progressive distance search: tries distances [5, 10, 25, 50] km until valid flight found
- Validates flight responses before publishing
- Publishes to both Spring Integration MQTT (MqttPublisher) and HiveMQ client (MqttService)

**AviationEdgeService (aviation/AviationEdgeService.java)**
- Encapsulates Aviation Edge API integration
- Uses Spring RestClient for HTTP communication
- Builds requests with lat, lng, distance, and API key parameters
- Returns List<FlightResponse> from external API

**MqttPublisher (mqtt/MqttPublisher.java)**
- Spring Integration MessagingGateway interface
- Publishes to `mqttOutboundChannel` configured in MqttConfig
- Used for framework-based MQTT messaging

**MqttService (mqtt/MqttService.java)**
- HiveMQ MQTT5 blocking client implementation
- Connects/disconnects for each message (stateless approach)
- Uses SSL connection to broker
- Publishes to topic defined in `mqtt.flights-topic`

**CoordinatesController (http/CoordinatesController.java)**
- REST endpoint: POST /api/iot/coordinates?lng=X&lat=Y
- Updates FlightFetcher coordinates and triggers immediate refresh
- Context path is /api/iot (defined in application.properties)

### Data Flow

1. FlightFetcher scheduled job or REST endpoint triggers refresh
2. FlightInfoService.fetchAndPublishFlight() is called with coordinates
3. Service iterates through distance values (5, 10, 25, 50 km)
4. For each distance, AviationEdgeService queries Aviation Edge API
5. First valid flight response is selected
6. Message formatted and published via both MQTT implementations
7. If no valid flight found, publishes "No flight!" message

### Configuration

**application.properties:**
- Server runs on port 8080 with context path `/api/iot`
- Default coordinates: lat=45.4678, lng=-73.7423 (Montreal area)
- Job refresh rate: 60000ms (1 minute)
- MQTT broker: HiveMQ Cloud instance with SSL on port 8883
- Aviation Edge API: requires API key in `aviation-edge.api-key`

**Config classes:**
- Config.java: Binds `default.*` properties
- AviationEdgeClientConfig: Binds `aviation-edge.*` properties
- MqttClientConfig: Binds `mqtt.*` properties
- MqttConfig: Configures Spring Integration MQTT adapter

### Testing

**AbstractIT base class:**
- Provides WireMock setup for Aviation Edge API mocking
- Configures test properties with random port for WireMock
- Includes helper method `setupAviationMock()` to stub flight responses
- Disables HTTP keep-alive for WireMock to prevent connection pool issues
- Mocks `mqttOutboundChannel` as MockitoSpyBean for verification

**Test utilities:**
- MqttPublisherTester: Manual MQTT publishing tool
- MqttSubscriberTester: Manual MQTT subscription tool

Integration tests use `@SpringBootTest` with WireMock for external API simulation.

## Technology Stack

- Java 21
- Spring Boot 3.5.7
- Spring Integration (MQTT support)
- Eclipse Paho MQTT client
- HiveMQ MQTT client
- Lombok for boilerplate reduction
- WireMock for integration testing
- Maven build system