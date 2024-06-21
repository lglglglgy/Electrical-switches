#include <Arduino.h>
#include <ESP32Servo.h>
#include <WiFi.h>
#include "time.h"
#include <SPI.h>
#include <TFT_eSPI.h>
int pos = 0;
const int LED=2;

TFT_eSPI tft = TFT_eSPI();
void setup() {
  pinMode(LED , OUTPUT);
  digitalWrite(LED , HIGH);
  Serial.begin(115200);
  Serial.println("Starting");
  
  tft.init();
  tft.setRotation(1);
}

void loop() {
  tft.fillScreen(TFT_BLACK);
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextSize(8);
  tft.setCursor(0, 0);
  tft.println("Hello World");
  delay(2000);
  Serial.println("Hello World");
  tft.fillScreen(TFT_BLACK);
  delay(2000);
}