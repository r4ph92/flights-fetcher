# Flights Fetcher

A Java Spring Boot application that fetches current flights in a specified area and publishes flight information to an MQTT topic using a scheduled job.

## Features

- **REST API** to receive and update coordinates (latitude, longitude, radius)
- **Scheduled Job** that periodically fetches flights in the specified area
- **OpenSky Network Integration** to retrieve real-time flight data
- **MQTT Publisher** to publish flight information to a configured MQTT topic
- **Configurable** via `application.yml` for easy customization

## Technology Stack

- Java 17
- Spring Boot 3.1.5
- Spring Integration MQTT
- Eclipse Paho MQTT Client
- OpenSky Network API
- Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MQTT Broker (e.g., Mosquitto) running on `tcp://localhost:1883` (or configure your own)

## Configuration

Edit `src/main/resources/application.yml` to configure:

```yaml
# MQTT Configuration
mqtt:
  broker-url: tcp://localhost:1883  # Your MQTT broker URL
  client-id: flights-fetcher-client
  username:                          # Optional: MQTT username
  password:                          # Optional: MQTT password
  topic: flights/data                # Topic to publish flight data

# Flight Fetcher Configuration
flight:
  api:
    url: https://opensky-network.org/api
  scheduler:
    fixed-rate: 300000               # Run every 5 minutes (in milliseconds)
  coordinates:
    latitude: 40.7128                # Default latitude (New York)
    longitude: -74.0060              # Default longitude
    radius: 50.0                     # Search radius in kilometers
```

## Building the Application

```bash
mvn clean package
```

## Running the Application

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/flights-fetcher-1.0.0.jar
```

## API Endpoints

### Get Current Coordinates

```bash
GET http://localhost:8080/api/coordinates
```

Response:
```json
{
  "latitude": 40.7128,
  "longitude": -74.0060,
  "radius": 50.0
}
```

### Update Coordinates

```bash
POST http://localhost:8080/api/coordinates
Content-Type: application/json

{
  "latitude": 51.5074,
  "longitude": -0.1278,
  "radius": 75.0
}
```

Response:
```json
{
  "message": "Coordinates updated successfully",
  "coordinates": {
    "latitude": 51.5074,
    "longitude": -0.1278,
    "radius": 75.0
  }
}
```

### Partially Update Coordinates

```bash
PATCH http://localhost:8080/api/coordinates
Content-Type: application/json

{
  "latitude": 48.8566,
  "radius": 100.0
}
```

## Flight Data Format

Flight data is published to the MQTT topic in JSON format:

```json
{
  "icao24": "abc123",
  "callsign": "UAL123",
  "originCountry": "United States",
  "longitude": -74.0060,
  "latitude": 40.7128,
  "baroAltitude": 10000.0,
  "velocity": 250.5,
  "heading": 180.0,
  "verticalRate": 0.0,
  "timePosition": 1699564800,
  "lastContact": 1699564800,
  "onGround": false
}
```

## How It Works

1. **Scheduled Job**: Every 5 minutes (configurable), the application fetches flights in the configured area
2. **OpenSky Network API**: Queries the OpenSky Network API with bounding box coordinates
3. **Data Processing**: Parses the flight data from the API response
4. **MQTT Publishing**: Publishes each flight as a JSON message to the configured MQTT topic

## Testing with MQTT

### Using Mosquitto

Install Mosquitto MQTT broker:

```bash
# Ubuntu/Debian
sudo apt-get install mosquitto mosquitto-clients

# macOS
brew install mosquitto
```

Subscribe to the flight data topic:

```bash
mosquitto_sub -h localhost -t flights/data -v
```

## Running Tests

```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/flightsfetcher/
│   │   ├── FlightsFetcherApplication.java
│   │   ├── client/
│   │   │   ├── FlightApiClient.java
│   │   │   └── OpenSkyResponse.java
│   │   ├── config/
│   │   │   └── MqttConfig.java
│   │   ├── controller/
│   │   │   └── CoordinatesController.java
│   │   ├── model/
│   │   │   ├── Coordinates.java
│   │   │   └── Flight.java
│   │   └── service/
│   │       ├── CoordinatesService.java
│   │       ├── FlightSchedulerService.java
│   │       └── MqttPublisherService.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/com/flightsfetcher/
        ├── FlightsFetcherApplicationTests.java
        ├── controller/
        │   └── CoordinatesControllerTest.java
        └── service/
            └── CoordinatesServiceTest.java
```

## License

This project is open source and available under the MIT License.
