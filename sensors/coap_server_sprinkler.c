#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "coap-blocking-api.h"
#include "os/dev/leds.h"
/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_DBG
extern process_event_t POST_EVENT;
extern coap_resource_t res_hum;
extern coap_resource_t  res_temp;
extern coap_resource_t res_spri;

#define SERVER_EP "coap://[fd00::1]:5683"
char *service_registration = "registration";

bool registered = false; 
static struct etimer timer;
extern int temp_value;
extern int hum_value;
extern bool is_on;
extern int threshold_temp;
extern int threshold_hum;
extern bool is_sprinkling;
static int temp_val_init;
static int rnd_val_temp;
static int rnd_val_hum;
static int handler = -1;
static int count = 0;
PROCESS(sprinkler_node, "Sprinkler");
AUTOSTART_PROCESSES(&sprinkler_node);

/* This function will be passed to COAP_BLOCKING_REQUEST() to handle responses. */
void client_chunk_handler(coap_message_t *response){
  
	const uint8_t *chunk;

	if(response == NULL) {
		puts("Request timed out");
	return;
	}

	if(!registered)
	registered = true;

	int len = coap_get_payload(response, &chunk);
	printf("|%.*s", len, (char *)chunk);
}

int handle_humidity(){
	int ret;
	if(handler == -1) //during app warm up	
		return 50;
	else if(handler == 0){//higher decrease (from 1 to 8 percentuals points)
		rnd_val_hum = rand()% 8 + 1;
		ret = hum_value - rnd_val_hum;
		if(ret < 0)
			ret = 0;
	}else if(handler == 1){//higher increase (from 1 to 8 percentuals points)
		rnd_val_hum = rand()% 8 + 1;
		ret = hum_value + rnd_val_hum;
		if(ret > 100)
			ret = 100;
	}else if(handler == 2){//lower increase (from 0 to 3 percentuals points)
		rnd_val_hum = rand()% 3;
		ret = hum_value + rnd_val_hum;
		if(ret > 100)
			ret = 100;
	}
	return ret;
}	


PROCESS_THREAD(sprinkler_node, ev, data){

  static coap_endpoint_t server_ep;
  static coap_message_t request[1];    

  PROCESS_BEGIN();

	LOG_INFO("Starting...\n");
  
	//activate resource
	coap_activate_resource(&res_temp, "temp");
	coap_activate_resource(&res_hum, "hum");
	coap_activate_resource(&res_spri, "sprinkler");

	//populate endpoint datastructure
	coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

	/* prepare request, TID is set by COAP_BLOCKING_REQUEST() */
	LOG_INFO("registering...\n");
	coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
	coap_set_header_uri_path(request, service_registration);

	while(!registered){	
		COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);	
    	}

	LOG_INFO("init leds to red...\n");
	leds_set(LEDS_NUM_TO_MASK(LEDS_RED));

	LOG_INFO("registered!\n");
	etimer_set(&timer, CLOCK_SECOND*10);
	temp_val_init = temp_value;
	while(true){
		
		PROCESS_WAIT_EVENT();
		if(ev == PROCESS_EVENT_TIMER){  ///  
			count++;
			//after 10 iterations there is a change in climate
			if(count == 10){
				temp_val_init = (rand() % (38 - 10 + 1)) + 10; //temperature [10°,38°]
				hum_value = (rand() % (80 - 20 + 1)) + 20; //humidity [20%,80%]
				count = 0;
			}
			//temperature vary from 1 to 3 degree (randomly increase or decrease)
			rnd_val_temp = rand()% 3 + 1;
			if((rand()%2+1) % 2 == 0)
				temp_value = temp_val_init + rnd_val_temp;
			else
				temp_value = temp_val_init - rnd_val_temp;

			hum_value = handle_humidity();
			LOG_DBG("temperature: %d\t threshold set to: %d\n", temp_value,threshold_temp);
			LOG_DBG("humidity: %d\t threshold set to: %d\n", hum_value,threshold_hum);
			//if sprinkler is manually off, grass humidity decrease
			if(!is_on){
				handler = 0;
				leds_set(LEDS_NUM_TO_MASK(LEDS_RED)); 
			}else{
				//if grass humidity is higher than the threshold
				if(hum_value >= threshold_hum){
					//if temperature is higher the threshold sprinkler is on and grass humidity increases gradually
					if(temp_value >= threshold_temp){
						handler = 2;
						leds_set(LEDS_NUM_TO_MASK(LEDS_GREEN));
						is_sprinkling=true;
					// if temperature is lower the threshold sprinkler is off and grass humidity decreases
					} else{
						handler = 0;
						leds_set(LEDS_NUM_TO_MASK(LEDS_YELLOW));
						is_sprinkling=false;
					}
				}
				//if grass humidity is lower than the threshold in any case sprinkler is on
				if(hum_value < threshold_hum){
					//if temperature is higher than the threshold, grass humidity increases gradually
					if(temp_value >= threshold_temp){
						handler = 2;
					//if temperature is lower than the thresold, grass humidity increases
					} else{
						handler = 1;
					}
					leds_set(LEDS_NUM_TO_MASK(LEDS_GREEN));
					is_sprinkling=true;
				}
			}
			res_spri.trigger();
			res_temp.trigger();
			res_hum.trigger();
			etimer_reset(&timer);
			LOG_DBG("Triggered update\n");
		}
	}
	
  PROCESS_END();
}
