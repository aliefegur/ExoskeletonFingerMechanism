#include <WiFi.h>
#include <ESPAsyncWebServer.h>
#include <ArduinoJson.h>
#include <ESP32Servo.h>

enum class ServoState {
  Open, Closed
};

constexpr char* ssid = "EXOSKELETON_WIFI";
constexpr char* password = "FENG498EXO";

AsyncWebServer server(80);

Servo servo1;
Servo servo2;

constexpr int servoPin1 = 3;
constexpr int servoPin2 = 4;

constexpr int SERVO1_OPEN = 0;
constexpr int SERVO1_CLOSE = 50;
constexpr int SERVO2_OPEN = 70;
constexpr int SERVO2_CLOSE = 0;

ServoState servoState;

// -------------------------
// WIFI
// -------------------------
void connectWiFi() {
  WiFi.mode(WIFI_AP);
  WiFi.softAP(ssid, password);

  Serial.println("SoftAP started!");
  Serial.print("IP address: ");
  Serial.println(WiFi.softAPIP());
}

// -------------------------
// SERVOS
// -------------------------
void setupServos() {
  servo1.attach(servoPin1);
  servo2.attach(servoPin2);

  servo1.write(SERVO1_OPEN);
  servo2.write(SERVO2_OPEN);

  servoState = ServoState::Open;
}

// -------------------------
// MOTION ENGINE
// -------------------------
void moveServoSmooth(Servo& servo, int from, int to, float duration) {

  int steps = 50;
  float dt = duration / steps;

  for (int i = 0; i <= steps; i++) {

    float t = (float)i / steps;
    int angle = from + (to - from) * t;

    servo.write(angle);

    delay(dt * 1000);
  }
}

void moveOpen(float duration) {
  if (servoState != ServoState::Open) {
    servoState = ServoState::Open;
    moveServoSmooth(servo1, SERVO1_CLOSE, SERVO1_OPEN, duration);
    moveServoSmooth(servo2, SERVO2_CLOSE, SERVO2_OPEN, duration);
  }
}

void moveClose(float duration) {
  if (servoState != ServoState::Closed) {
    servoState = ServoState::Closed;
    moveServoSmooth(servo1, SERVO1_OPEN, SERVO1_CLOSE, duration);
    moveServoSmooth(servo2, SERVO2_OPEN, SERVO2_CLOSE, duration);
  }
}

void setupServer() {
  server.on("/ping", HTTP_GET, [](AsyncWebServerRequest* request) {
    request->send(200, "text/plain", "OK");
    Serial.printf("\nNEW REQUEST ON '/ping'!\n");
  });

  server.on(
    "/move", HTTP_POST,
    [](AsyncWebServerRequest* request) {},
    NULL,
    [](AsyncWebServerRequest* request, uint8_t* data, size_t len, size_t index, size_t total) {
      StaticJsonDocument<200> doc;
      deserializeJson(doc, data);

      String mode = doc["mode"];
      float duration = doc["duration"];

      if (mode == "OPEN") {
        moveOpen(duration);
      } else if (mode == "CLOSE") {
        moveClose(duration);
      }

      Serial.printf("\nNEW REQUEST ON '/move'!\nmode: %s\nduration: %f\n", mode.c_str(), duration);

      request->send(200, "text/plain", "OK");
    });

  server.begin();
}

// -------------------------
// SETUP
// -------------------------
void setup() {
  Serial.begin(115200);
  Serial.println("Serial port started at 115200 baud!");

  connectWiFi();
  setupServos();
  setupServer();
}

void loop() {
  // Async server -> empty
}