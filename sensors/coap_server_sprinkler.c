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

extern coap_resource_t  res_temp;

#define SERVER_EP "coap://[fd00::1]:5683"
char *service_registration = "/registration";

bool registered = false; 
static struct etimer timer;
#define THRESHOLD_TEMP_MIN 5
#define THRESHOLD_TEMP_MAX 20
static int upper = 45;
static int lower = -10;
extern int temp_value;
extern bool ideal_temp;
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


PROCESS_THREAD(sprinkler_node, ev, data){

  static coap_endpoint_t server_ep;
  static coap_message_t request[1];    

  PROCESS_BEGIN();

	LOG_INFO("Starting...\n");
  
	//activate resource
	coap_activate_resource(&res_temp, "temperature-sensor");

	//populate endpoint datastructure
	coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

	/* prepare request, TID is set by COAP_BLOCKING_REQUEST() */
	LOG_INFO("registering...\n");
	coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
	coap_set_header_uri_path(request, service_registration);
/*
	while(!registered){	
		COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);	
    }
*/                
	LOG_INFO("registered!\n");
	etimer_set(&timer, CLOCK_SECOND*10);

	while(true){
		
		PROCESS_WAIT_EVENT();
		if(ev == PROCESS_EVENT_TIMER){  ///  

			temp_value = (rand()%(upper - lower + 1)) + lower;
			LOG_DBG("temperature: %d\n", temp_value);
			if(temp_value <= THRESHOLD_TEMP_MIN || temp_value >= THRESHOLD_TEMP_MAX){
				ideal_temp = false;
				leds_on(LEDS_NUM_TO_MASK(LEDS_RED));
				leds_off(LEDS_NUM_TO_MASK(LEDS_GREEN));
			}
			if(temp_value>THRESHOLD_TEMP_MIN && temp_value<THRESHOLD_TEMP_MAX){
				ideal_temp = true;
				leds_on(LEDS_NUM_TO_MASK(LEDS_GREEN));
				leds_off(LEDS_NUM_TO_MASK(LEDS_RED));
			}

			res_temp.trigger();
			etimer_reset(&timer);
			LOG_DBG("Triggered update\n");
		}
/*
		if(ev == POST_EVENT){
			if(is_auto){//if in automatic mode
				if(is_sunset && !is_humid){	//if dark, not humid
					status=true;
					leds_on(LEDS_NUM_TO_MASK(LEDS_GREEN));
				}
				else {			
					status=false;
					leds_off(LEDS_NUM_TO_MASK(LEDS_GREEN));
				}
			}
		}
*/
	}
	
  PROCESS_END();
}