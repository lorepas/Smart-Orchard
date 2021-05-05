#include <stdio.h>
#include <string.h>
#include "coap-engine.h"
#include "coap.h"
#include "os/dev/leds.h"

#include"time.h"

#include "sys/log.h"
#define LOG_MODULE "Temperature sensor"
#define LOG_LEVEL LOG_LEVEL_DBG

int temp_value = 20;
bool ideal_temp = false;
int threshold_temp = 15;


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_post_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);

EVENT_RESOURCE(res_temp,
               "title=\"Temperature Sensor: ?POST/PUT value=<value>\";rt=\"temperature sensor\";obs",
               res_get_handler,
               res_post_put_handler,
               res_post_put_handler,
               NULL,
               res_event_handler);


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	if(request !=NULL){
		LOG_DBG("Received GET\n");
	}

	unsigned int accept = -1;
	  coap_get_header_accept(request, &accept);

	  if(accept == TEXT_PLAIN) {
	    coap_set_header_content_format(response, TEXT_PLAIN);
	    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "value=%d,thr_tmp=%d", temp_value,threshold_temp);
	    coap_set_payload(response, (uint8_t *)buffer, strlen((char *)buffer));
	    
	  } else if(accept == APPLICATION_XML) {
	    coap_set_header_content_format(response, APPLICATION_XML);
	    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "<value=\"%d\"/><thr_tmp=%d/>", temp_value,threshold_temp);
	    coap_set_payload(response, buffer, strlen((char *)buffer));
	    
	  } else if(accept == -1 || accept == APPLICATION_JSON) {
	    coap_set_header_content_format(response, APPLICATION_JSON);
	    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"value\":\"%d\",\"thr_tmp\":\"%d\"}", temp_value,threshold_temp);
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
	const char *text = NULL;
	char temp[32];
    memset(temp, 0, 32);
	
	len = coap_get_post_variable(request, "thr_tmp", &text);
	if(len > 0 && len < 32) {
		memcpy(temp, text, len);
		threshold_temp = atoi(temp);
		LOG_DBG("Temperature threshold setted to: %d\n",threshold_temp);
		char msg[50];
	    memset(msg, 0, 50);
		sprintf(msg, "Temperature threshold setted to %d", threshold_temp);
		int length=sizeof(msg);
		coap_set_header_content_format(response, TEXT_PLAIN);
		coap_set_header_etag(response, (uint8_t *)&length, 1);
		coap_set_payload(response, msg, length);
		coap_set_status_code(response, CHANGED_2_04);
	}else{
		coap_set_status_code(response, BAD_REQUEST_4_00);
	}
	
}


static void res_event_handler(void) {
	LOG_DBG("Sending notification");
  	coap_notify_observers(&res_temp);
}
