import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RegistrationResource extends CoapResource {
	
	private int counter=0;
	public static ArrayList<Resource> resources = new ArrayList<Resource>(3);

	public RegistrationResource(String name) {
		super(name);
	}

	public RegistrationResource(String name, boolean visible) {
		super(name, visible);
	}
	
	public void handleGET(CoapExchange exchange) {
		exchange.accept();
		InetAddress addr = exchange.getSourceAddress();
		CoapClient client = new CoapClient("coap://["+addr.getHostAddress()+"]:5683/.well-known/core");
		CoapResponse response = client.get();
		String code = response.getCode().toString();
		
		if(!code.startsWith("2")) {
			System.err.println("Error with code: "+code);
			return;
		}
		
		String responseText = response.getResponseText();
		String[] res = responseText.split(",");
		String orchard_type= App.orchards.get(counter++);
		for(int i=1; i< res.length; i++) {
			try {
				String[] parameters = res[i].split(";");
				String path = parameters[0].split("<")[1].split(">")[0];
				String name = path.split("/")[1];
				if(name.compareTo("hum")==0) {
					HumiditySensor newHum = new HumiditySensor(path,addr.getHostAddress(),orchard_type);
					App.hum_sensor.put(name+"_"+addr.getHostAddress(),newHum);
					App.obsClient.put(name+"_"+addr.getHostAddress(), new ObserveCoapClient(newHum));
					App.obsClient.get(name+"_"+addr.getHostAddress()).startCoapObserve();
				}else if(name.compareTo("temp")==0) {
					TemperatureSensor newTem = new TemperatureSensor(path,addr.getHostAddress(),orchard_type);
					App.temp_sensor.put(name+"_"+addr.getHostAddress(), newTem);
					App.obsClient.put(name+"_"+addr.getHostAddress(), new ObserveCoapClient(newTem));
					App.obsClient.get(name+"_"+addr.getHostAddress()).startCoapObserve();
				}else if(name.compareTo("sprinkler")==0) {
					Sprinkler newSprin = new Sprinkler(path, addr.getHostAddress(),orchard_type);
					App.sprinkler.put(name+"_"+addr.getHostAddress(),newSprin);
					App.obsClient.put(name+"_"+addr.getHostAddress(), new ObserveCoapClient(newSprin));
					App.obsClient.get(name+"_"+addr.getHostAddress()).startCoapObserve();
				}

			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(counter==App.res_number)
			App.waitReg = false;
			
	}


}
