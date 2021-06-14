# Smart Cultivation

The aim of this project is to develop an IoT application to monitor the orchard in a smart way. In particular, thanks to both sensors and actuator that communicate with a Cloud Computing application, the farmer can monitor his orchards.

## 1. Introduction

With this application the farmer can adjust the irrigation of the crop in a smart way. 
- If a sensor detects a temperature or humidity higher or lower than a certain threshold, it starts irrigating or stops irrigation automatically. The farmer can also turn off the actuator so she/he can manually controls the irrigation of the cultivation.
- The farmer can deploy as many nodes as she/he wants, i.e. in a small farm with only two lines of cultivations the farmer can think to deploy two nodes. 
- Each node works either as sensor and as actuator.
- Each node can be customized by the farmer, in particular he can fix the temperature and humidity threshold from which turn on or turn off the actuator.

# Deployment and execution

We work on Ubuntu VM in which is pre-installed Contiki-NG and we only simulate the motes work in Cooja. For this reason we have first to run the container with the `contikier` command and then in the path `tools/cooja` run the app with the `ant run` command. Once in Cooja, we can deploy the nodes in two ways:
1. Open a preconfigured simulation (file with .csc extension).
2. Open a new simulation, install first the border-router from the file `border-router.c` and then as many nodes as you wants from the file `coap_server_sprinkler.c`. Then we have to right click on the border-router, set it as SERVER and click on START.

At this point, we have to open a new terminal to start the border router. As we have done before, we have to run the container and then must go into the `border-router's project folder`. At this point we have to run the `make TARGET=cooja connect-border-router` command. Now, the border-router is active and ready to perfrom its actions. At this point we can start the simulation.

After starting the simulation, we can open our Cloud Computing application. Open in terminal the main folder, go into `coap-server folder`  and perform the following command:
```
java -jar target/coap-server-0.0.1-SNAPSHOT.jar
```

# Tutorial

## 1. Deployment

![deployment] (images/img1.png "Deployment")

## 2. 