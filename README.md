# Electrical-switches
## purpose
The purpose of this program is to prevent dorms from forgetting to turn out the lights when they go to bed.It's very irritating.
## Project Profile
In this project, I used an esp32 development board, a cloud server, and a mobile app. First of all, the esp32 dev board serves to connect to WiFi and control the servos based on the information received from the cloud server. Secondly, the cloud server has a public ip which can be used as a relay for the communication between the mobile app and the esp32 board, which has great advantages, on the one hand, it enlarges the effective range of the control system, and at the same time, the cloud server shares part of the load of the esp32, which effectively improves the endurance. Finally, the role of the mobile app is to facilitate the user's control, if you want to embed it into other applications, you just need to follow the rules to launch a POST request to the cloud server
