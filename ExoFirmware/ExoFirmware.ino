#include <ESP32Servo.h>
#include <WiFi.h>
#include <ESPAsyncWebServer.h>
#include <ArduinoJson.h>

enum class ServoState {
  Open,
  Closed,
  Actuating
};

struct MotionCommand {
  int start1;
  int target1;

  int start2;
  int target2;

  uint32_t startTime;
  uint32_t durationMs;

  bool active;
};

constexpr int servoPin1 = 3;
constexpr int servoPin2 = 4;

constexpr int SERVO1_OPEN = 0;
constexpr int SERVO1_CLOSE = 50;
constexpr int SERVO2_OPEN = 70;
constexpr int SERVO2_CLOSE = 0;

Servo servo1;
Servo servo2;

ServoState servoState;
MotionCommand motion;

constexpr char* ssid = "EXOSKELETON_WIFI";
constexpr char* password = "FENG498EXO";

AsyncWebServer server(80);

void setupServos();
void startMotion(int from1, int to1, int from2, int to2, float duration);
void moveOpen(float duration);
void moveClose(float duration);
void updateMotion();

void setupWiFi();
void setupServer();

void setup() {
  Serial.begin(115200);
  Serial.println("Serial port started at 115200 baud!");

  setupWiFi();
  setupServos();
  setupServer();
}

void loop() {
  updateMotion();
}

void setupServos() {
  servo1.attach(servoPin1);
  servo2.attach(servoPin2);

  servo1.write(SERVO1_OPEN);
  servo2.write(SERVO2_OPEN);

  servoState = ServoState::Open;
}

void startMotion(
  int from1,
  int to1,
  int from2,
  int to2,
  float duration) {
  motion.start1 = from1;
  motion.target1 = to1;

  motion.start2 = from2;
  motion.target2 = to2;

  motion.startTime = millis();
  motion.durationMs = duration * 1000.0f;

  motion.active = true;

  servoState = ServoState::Actuating;
}

void moveOpen(float duration) {
  if (servoState == ServoState::Closed) {
    startMotion(
      SERVO1_CLOSE,
      SERVO1_OPEN,
      SERVO2_CLOSE,
      SERVO2_OPEN,
      duration);
  }
}

void moveClose(float duration) {
  if (servoState == ServoState::Open) {
    startMotion(
      SERVO1_OPEN,
      SERVO1_CLOSE,
      SERVO2_OPEN,
      SERVO2_CLOSE,
      duration);
  }
}

void updateMotion() {
  if (!motion.active)
    return;

  uint32_t elapsed = millis() - motion.startTime;

  float t = (float)elapsed / (float)motion.durationMs;
  float smoothT = t * t * (3.0f - 2.0f * t);

  if (t > 1.0f)
    t = 1.0f;

  int angle1 = motion.start1 + (motion.target1 - motion.start1) * smoothT;
  int angle2 = motion.start2 + (motion.target2 - motion.start2) * smoothT;

  servo1.write(angle1);
  servo2.write(angle2);

  if (t >= 1.0f) {
    motion.active = false;

    if (motion.target1 == SERVO1_OPEN)
      servoState = ServoState::Open;
    else
      servoState = ServoState::Closed;
  }
}

void setupWiFi() {
  WiFi.mode(WIFI_AP);
  WiFi.softAP(ssid, password);

  Serial.println("SoftAP started!");
  Serial.print("IP address: ");
  Serial.println(WiFi.softAPIP());
}

void setupServer() {
  server.on("/ping", HTTP_GET, [](AsyncWebServerRequest* request) {
    Serial.printf("\nNEW REQUEST ON '/ping'!\n");
    request->send(200, "text/plain", "OK");
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

      Serial.printf("\nNEW REQUEST ON '/move'!\nmode: %s\nduration: %f\n", mode.c_str(), duration);

      if (mode == "OPEN") {
        moveOpen(duration);
      } else if (mode == "CLOSE") {
        moveClose(duration);
      }

      request->send(200, "text/plain", "OK");
    });

  server.begin();
}
