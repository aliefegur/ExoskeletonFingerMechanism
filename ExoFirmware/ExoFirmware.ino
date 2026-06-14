#include <WiFi.h>
#include <ESPAsyncWebServer.h>
#include <ArduinoJson.h>
#include <ESP32Servo.h>

const char* ssid = "EXOSKELETON_WIFI";
const char* password = "FENG498EXO";

AsyncWebServer server(80);

Servo servo1;
Servo servo2;

int servoPin1 = 3;
int servoPin2 = 4;

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

  servo1.write(90);
  servo2.write(90);
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
  moveServoSmooth(servo1, 90, 0, duration);
  moveServoSmooth(servo2, 90, 180, duration);
}

void moveClose(float duration) {
  moveServoSmooth(servo1, 0, 90, duration);
  moveServoSmooth(servo2, 180, 90, duration);
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

      /*if (mode == "OPEN") {
        moveOpen(duration);
      } else if (mode == "CLOSE") {
        moveClose(duration);
      }*/
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