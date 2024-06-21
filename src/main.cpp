#include <Arduino.h>
#include <ESP32Servo.h>
#include <WiFi.h>
#include "time.h"
#include "sntp.h"
#include <HTTPClient.h>

const char* ssid       = "ChinaNet-2.4G-C6F0";
const char* password   = "include.";
const char* ntpServer1 = "ntp.aliyun.com";
const char* ntpServer2 = "ntp.tuna.tsinghua.edu.cn";
const long  gmtOffset_sec = 28800;
const int   daylightOffset_sec = 0;
const char* time_zone = "CST-8";
Servo myservo; 
HTTPClient http;
int pos = 0;
const int LED=2;
void printLocalTime()
{
  struct tm timeinfo;
  if(!getLocalTime(&timeinfo)){
    Serial.println("No time available (yet)");
    return;
  }
  Serial.println(&timeinfo, "%A, %B %d %Y %H:%M:%S");
}


void performPOSTRequest() {
  HTTPClient http2;
  http2.begin("http://192.168.1.8:5000/");
  http2.addHeader("Content-Type", "application/json");

  // Use double quotes for JSON keys and values
  String postData = "{\"code\": 300}";

  // Make the POST request
  int httpResponseCode = http2.POST(postData);

  if (httpResponseCode > 0) {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    String response = http2.getString();
    Serial.println(response);
  } else {
    Serial.print("Error on HTTP request. HTTP Response code: ");
    Serial.println(httpResponseCode);
  }
  http2.end();
}


void timeavailable(struct timeval *t)
{
  Serial.println("Got time adjustment from NTP!");
  printLocalTime();
}

void setup() {
  myservo.write(45);
  // pinMode(LED , OUTPUT);
  // digitalWrite(LED , HIGH);
  Serial.begin(115200);
  sntp_set_time_sync_notification_cb(timeavailable);
  myservo.attach(15);  
  sntp_servermode_dhcp(1); 
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer1, ntpServer2);
  Serial.printf("Connecting to %s ", ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.print(".");
  }
  Serial.println(" CONNECTED");
  http.begin("http://192.168.1.8:5000/");
  performPOSTRequest();
}

void open()
{
  for (; pos <= 90; pos += 1) { 
    myservo.write(pos); 
    delay(10);               
  }
  myservo.write(45); 
}

void close()
{
  for (; pos >= 0; pos -= 1) { 
    myservo.write(pos);              
    delay(10);                       
  }
  myservo.write(45); 
}



void enterDeepSleep() {
  Serial.println("Entering deep sleep...");
  //  digitalWrite(LED ,LOW);
  esp_deep_sleep(200e6); // Sleep for 2 seconds, you can adjust this based on your requirements
}

void loop() {
  int httpCode = http.GET(); // Initiate GET request

  if (httpCode > 0) {
    Serial.print("HTTP Response code: ");
    Serial.println(httpCode);

    if (httpCode == 200) {
      if (pos != 91) {
        Serial.println("Open");
        pos = 45;
        open();
      }
    } else if (httpCode == 400) {
      if (pos != -1) {
        Serial.println("Close");
        pos = 45;
        close();
      }
    } else if (httpCode == 401) {
      Serial.println("Force close");
      pos = 45;
      close();
      performPOSTRequest();
    } else if (httpCode == 201) {
      Serial.println("Force open");
      pos = 45;
      open();
      performPOSTRequest();
    }else if (httpCode == 300 )
      pos = 45;
  } else {
    Serial.printf("[HTTP] GET... failed, error: %s\n", http.errorToString(httpCode).c_str());
  }

  delay(3000);

  // Check current time and enter deep sleep if it's between 10 PM and 12 AM
  struct tm timeinfo;
  if (getLocalTime(&timeinfo)) {
    int currentHour = timeinfo.tm_hour;
    if (!(currentHour >= 23 || currentHour <= 1)) {
      enterDeepSleep();
    }
  }
}
