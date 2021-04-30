import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RegistrationResource extends CoapResource {

	public RegistrationResource(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public RegistrationResource(String name, boolean visible) {
		super(name, visible);
		// TODO Auto-generated constructor stub
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
		for(int i=1; i< res.length; i++) {
			try {
				String[] parameters = res[i].split(";");
				String path = parameters[0].split("<")[1].split(">")[0];
				String name = path.split("/")[1];
				if(name.compareTo("hum")==0) {
					HumiditySensor newHum = new HumiditySensor(path,addr.getHostAddress());
					App.hum_sensor.put(name,newHum);
				}else if(name.compareTo("temp")==0) {
					TemperatureSensor newTem = new TemperatureSensor(path,addr.getHostAddress());
					App.temp_sensor.put(name, newTem);
				}else if(name.compareTo("sprinkler")==0) {
					Sprinkler newSprin = new Sprinkler(path, addr.getHostAddress());
					App.sprinkler.put(name,newSprin);
				}
			App.waitReg = false;	
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
