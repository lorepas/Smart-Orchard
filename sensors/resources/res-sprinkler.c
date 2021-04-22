#include <stdlib.h>
#include <string.h>
#include "coap-engine.h"
#include "contiki.h"
#include "os/dev/leds.h"
#include "sys/stimer.h"

/*log conf*/
#include "sys/log.h"
#define LOG_MODULE "Sprinkler Actuator"
#define LOG_LEVEL LOG_LEVEL_DBG

//extern struct process sprinkler_node;
process_event_t POST_EVENT;
bool is_on = true;
int success = 0;
static struct stimer timer;
static struct stimer timer_blink;

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);

EVENT_RESOURCE(res_spri, "title=\"Sprinkler actuator: ?POST/PUT active=ON|OFF\";rt=\"Sprinkler\";obs",
	       	   res_get_handler,
               res_post_put_handler,
               res_post_put_handler,
               NULL,
               res_event_handler);

static void res_event_handler(void) {
	LOG_DBG("sending notification");
  	coap_notify_observers(&res_spri);
}

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	  if(request!=NULL){
		LOG_DBG("Received GET\n");
	  }

	  LOG_DBG("Active: %d \n", is_on);

	  char *active = NULL;

	if(is_on){
		active="ON";
	}
	else {
		active="OFF";
	}

	  unsigned int accept = -1;
	  coap_get_header_accept(request, &accept);

	  if(accept == TEXT_PLAIN) {
	    coap_set_header_content_format(response, TEXT_PLAIN);
	    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "active=%s", active);
	    coap_set_payload(response, (uint8_t *)buffer, strlen((char *)buffer));
	    
	  } else if(accept == APPLICATION_XML) {
	    coap_set_header_content_format(response, APPLICATION_XML);
	    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "<active=%s/>", active);
	    coap_set_payload(response, buffer, strlen((char *)buffer));
	    
	  } else if(accept == -1 || accept == APPLICATION_JSON) {
	    coap_set_header_content_format(response, APPLICATION_JSON);
	    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"active\":\"%s\"}", active);
	    coap_set_payload(response, buffer, strlen((char *)buffer));
	    
	  } else {
	    coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
	    const char *msg = "Supporting content-types text/plain, application/xml, and application/json";
	    coap_set_payload(response, msg, strlen(msg));
	  }
}

static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){  
	
	if(request!=NULL){
		LOG_DBG("Received POST/PUT\n");
	}

	size_t len = 0; 
	const char *control = NULL;
	
	if((len = coap_get_post_variable(request, "active", &control))){
		if(strncmp(control, "ON", len)== 0){
			is_on=true;
			stimer_set(&timer, CLOCK_SECOND*12);
			stimer_set(&timer_blink, CLOCK_SECOND*1);
			while(!stimer_expired(&timer)){
				if(stimer_expired(&timer_blink)){
					leds_toggle(LEDS_ALL);
					stimer_restart(&timer_blink);
				}
			}
			LOG_DBG("Sprinkler is ON\n");
			leds_set(LEDS_NUM_TO_MASK(LEDS_GREEN));
			success = 1;
		}else if(strncmp(control, "OFF", len)== 0){
			is_on=false;
			LOG_DBG("Sprinkler is OFF\n");
			leds_set(LEDS_NUM_TO_MASK(LEDS_RED));
			success = 1;
		}else{
			success = 0;
		}			
	}

	if (success==0){
		coap_set_status_code(response, BAD_REQUEST_4_00);
	}else{
		coap_set_status_code(response, CHANGED_2_04);
		//process_post(&irrigator_node,POST_EVENT,NULL); 
	}
	
}
