#include <WiFi.h>
#include <PubSubClient.h>

#include <Adafruit_GFX.h>
#include <Adafruit_NeoMatrix.h>
#include <Adafruit_NeoPixel.h>

#define PIN 5

#define MATRIX_WIDTH 64
#define MATRIX_HEIGHT 16
#define TILE_WIDTH 16
#define TILE_HEIGHT 16

Adafruit_NeoMatrix matrix = Adafruit_NeoMatrix(
    TILE_WIDTH, TILE_HEIGHT, 4, 1, PIN,
    NEO_TILE_TOP + NEO_TILE_LEFT + NEO_TILE_ROWS + NEO_TILE_PROGRESSIVE +
    NEO_MATRIX_BOTTOM + NEO_MATRIX_LEFT + NEO_MATRIX_ROWS + NEO_MATRIX_ZIGZAG,
    NEO_GRB + NEO_KHZ800
);

const char* ssid = "Cegeplabs";
const char* password = "Cegepdept";
const char* mqtt_server   = "broker.emqx.io";
const uint16_t mqtt_port  = 1883;
const char* mqtt_clientId = "ESP32_Matrix_1";
const char* mqtt_topic    = "topic/flights/iot";
int idx = 0;

WiFiClient espClient;
PubSubClient client(espClient);

String currentText = "FLIGHTS!!!";
String lastText    = ""; 

const uint16_t colors[] = {
    matrix.Color(255, 0, 0), matrix.Color(0, 255, 0), matrix.Color(0, 0, 255)
};


void connectWiFi() {
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(300);
        Serial.print(".");
    }
}


void mqttCallback(char* topic, byte* payload, unsigned int length) {
    String msg;

    for (unsigned int i = 0; i < length; i++) {
        msg += (char)payload[i];
    }

    currentText = msg; 
}

void reconnectMQTT() {
    while (!client.connected()) {
        Serial.print("Connecting to MQTT...");
        if (client.connect(mqtt_clientId)) {
            Serial.println("connected");
            client.subscribe(mqtt_topic);
        } else {
            Serial.print("Failed, rc=");
            Serial.print(client.state());
            Serial.println(" retrying in 5 sec");
            delay(5000);
        }
    }
}

void setup() {
    Serial.begin(115200);

    matrix.begin();
    matrix.setTextWrap(true);
    matrix.setBrightness(10);
    matrix.setTextColor(colors[idx]);

    connectWiFi();

    client.setServer(mqtt_server, mqtt_port);
    client.setCallback(mqttCallback);
}


void loop() {
    if (!client.connected()) {
        reconnectMQTT();
    }
    client.loop();

    if (currentText != lastText) {
        lastText = currentText;

        matrix.fillScreen(0);
        matrix.setCursor(0, 0);
        matrix.setTextColor(colors[idx]);
        matrix.print(currentText);
        matrix.show();
        idx = (idx + 1) % 3;
    }

    delay(10);
}


