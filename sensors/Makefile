CONTIKI_PROJECT = sensor
all: $(CONTIKI_PROJECT)
CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"
CONTIKI=../../../contiki-ng

# Do not try to build on Sky because of code size limitation
PLATFORMS_EXCLUDE = sky z1

include $(CONTIKI)/Makefile.identify-target
MODULES_REL += $(TARGET)

# Include CoAP resources
MODULES_REL += ./resources

# Include the CoAP implementation
include $(CONTIKI)/Makefile.dir-variables
MODULES += $(CONTIKI_NG_APP_LAYER_DIR)/coap

include $(CONTIKI)/Makefile.include
