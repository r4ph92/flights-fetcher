# Flights Fetcher

A Spring Boot application that fetches real-time flight information for given coordinates using the Aviation Edge API and publishes the data to an MQTT broker.

## Features

- **Scheduled Flight Fetching**: Automatically polls for flights at configurable intervals (default: 60 seconds)
- **Dynamic Coordinate Updates**: REST API endpoint to change search coordinates on-the-fly
- **Progressive Distance Search**: Searches for flights at increasing distances (5, 10, 25, 50 km) until a valid flight is found
- **MQTT Publishing**: Publishes flight data to MQTT broker using dual implementation (Spring Integration + HiveMQ)
- **Aviation Edge Integration**: Retrieves live flight data from Aviation Edge API

## Prerequisites

- Java 21
- Maven 3.6+
- Aviation Edge API key (sign up at [aviation-edge.com](https://aviation-edge.com))
- MQTT broker access (default configuration uses HiveMQ Cloud)
### Aviation Edge
I want to sincerely thank Aviation Edge for providing me with a free student API key. Their support allowed me to build a realistic project using real-world aviation data. Without their collaboration, this project would not have been able to reach the same level of quality and precision.
#### Why
This project is directly linked to flight tracking and data visualization. Aviation Edge enabled me to:
Work with real data instead of simulated datasets
Test ideas based on real flight information
Learn and experiment with modern aviation data infrastructures
Their generosity towards students is meaningful and greatly appreciated.

#### About
Aviation Edge is a professional API solution that provides live aviation data from all around the world. Through simple HTTP requests, you can access information such as:
Real-time flights and positions
Airline details (IATA / ICAO)
Airports and runways
Aircraft types and characteristics
Flight routes and schedules
Cities, countries, timezones, and more
Typical request example:

GET https://aviation-edge.com/v2/public/flights?key=YOUR_API_KEY

Responses are returned in JSON, which makes it easy to parse from any language (JavaScript, Java, Python, etc.).


## Configuration

Update `src/main/resources/application.properties` with your credentials:

```properties
# Default search coordinates
default.lng=-73.7423
default.lat=45.4678
default.job-rate=60000

# Aviation Edge API
aviation-edge.api-key=YOUR_API_KEY_HERE

# MQTT Broker
mqtt.broker-host=your-broker-host
mqtt.broker-port=8883
mqtt.username=your-username
mqtt.password=your-password
mqtt.flights-topic=topic/iot/flights
```

For local development, create `application-local.properties` to override settings.

## Building and Running

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run

# Run with local profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Package as executable JAR
./mvnw package
java -jar target/flights-fetcher-0.0.1-SNAPSHOT.jar
```

## Testing

```bash
# Run all tests
./mvnw test

# Run integration tests only
./mvnw test -Dtest="*IT"

# Run specific test
./mvnw test -Dtest=FlightInfoServiceIT
```

Integration tests use WireMock to mock the Aviation Edge API.

## Architecture

The application follows a layered architecture:

- **Service Layer** (`FlightInfoService`): Business logic and orchestration
- **Integration Layer** (`AviationEdgeService`): External API communication
- **Messaging Layer** (`MqttService`, `MqttPublisher`): MQTT publishing
- **Scheduled Jobs** (`FlightFetcher`): Periodic flight data fetching

## How It Works

1. The `FlightFetcher` scheduled job runs every minute (configurable)
2. It calls `FlightInfoService` with current coordinates
3. The service queries `AviationEdgeService` with progressive distances (5, 10, 25, 50 km)
4. First valid flight response is formatted and published to MQTT
5. If no flights found, publishes "No flight!" message

## MQTT Message Format

Flight messages are published in a formatted string containing:
- Flight number (IATA/ICAO)
- Aircraft registration and type
- Airline codes
- Departure and arrival airports
- Current position (lat/lng/altitude)
- Speed and direction
- Flight status

## Technology Stack

- Spring Boot 3.5.7
- Spring Integration (MQTT)
- Eclipse Paho MQTT Client
- HiveMQ MQTT Client
- Lombok
- WireMock (testing)
- Maven

## License

This project is part of a CEGEP educational program.
