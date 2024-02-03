# Electrical-switches
## purpose
The purpose of this program is to prevent dorms from forgetting to turn out the lights when they go to bed.It's very irritating.
## Project Profile
In this project, I used an esp32 development board, a cloud server, and a mobile app. First of all, the esp32 dev board serves to connect to WiFi and control the servos based on the information received from the cloud server. Secondly, the cloud server has a public ip which can be used as a relay for the communication between the mobile app and the esp32 board, which has great advantages, on the one hand, it enlarges the effective range of the control system, and at the same time, the cloud server shares part of the load of the esp32, which effectively improves the endurance. Finally, the role of the mobile app is to facilitate the user's control, if you want to embed it into other applications, you just need to follow the rules to launch a POST request to the cloud server
## point of attention
Please note that there are a few things that need to be changed.
1. the WiFi password and name in lines 8 and 9 of src/main.cpp. need to be changed.
2. the ip address on lines 32 and 76 of src/main.cpp. needs to be changed, this address should point to your cloud server.
3. in python/run.py, the port number located in the last line needs to be changed as per your requirement and also after modification, please adjust your firewall and policy group settings.
4. in app/app/src/main/java/com/sblgy/sw/
MainActivity.java, the url on line 22 needs to be changed to the url of the real cloud server.
<div align="center">

| Project APP                                          | ITEM                                         |
|---------------------------------------------------------|------------------------------------------------------------|
|![image](https://github.com/lglglglgy/Electrical-switches/assets/129643128/b1c6cb76-8cc3-4988-94a4-36862ff5bff9)|![image](https://github.com/lglglglgy/Electrical-switches/assets/129643128/770e3982-6c2b-4b94-b68f-7d92df9f6787)|

</div>
