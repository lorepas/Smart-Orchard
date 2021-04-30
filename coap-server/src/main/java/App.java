import java.util.*;
import java.util.Map.Entry;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class App {
	
	public static Map<String,Sprinkler> sprinkler = new HashMap<String,Sprinkler>();
	public static Map<String,HumiditySensor> hum_sensor = new HashMap<String,HumiditySensor>();
	public static Map<String,TemperatureSensor> temp_sensor = new HashMap<String,TemperatureSensor>();
	public static boolean waitReg = true;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		startServer();
		while(true) {
			if(waitReg) {
				System.out.println("-------- WAITING RESOURCE REGISTRATION --------\n");
			}
			commandLine();
			Scanner command = new Scanner(System.in);
			int command_code = command.nextInt();
			switch(command_code) {
				case(0):
					System.out.println("-------- THIS IS A LIST OF REGISTERED RESOURCE: --------\n");
					showRegisteredResource();
					break;
				case(1):
					System.out.println("-------- THIS IS A LIST OF REGISTERED SPRINKLER: --------\n");
					showRegisteredSprinkler();
					System.out.println("Please, select a Sprinkler ID:\n");
					System.out.print(">>>>");
					int nodeId = command.nextInt();
					changeSprinklerStatus(nodeId);
					break;
				case(2):
					System.out.println("Which resources are you looking for?\n");
					System.out.println("0 - Sprinklers\n");
					System.out.println("1 - Humidity sensors\n");
					System.out.println("2 - Temperature sensors\n");
					System.out.print(">>>>");
					int res = command.nextInt();
					showResourcesState(res);
					break;
				default:
					System.out.println("-------- COMMAND WRONG! RETRY... --------\n");
					break;
			}
		}
	}
	
	public static void showRegisteredResource() {
		for(Map.Entry<String, Sprinkler> entry: sprinkler.entrySet())
			System.out.println(entry.getKey()+"->"+entry.getValue().toString());
		for(Map.Entry<String, HumiditySensor> entry: hum_sensor.entrySet() )
			System.out.println(entry.getKey()+"->"+entry.getValue().toString());
		for(Map.Entry<String, TemperatureSensor> entry: temp_sensor.entrySet() )
			System.out.println(entry.getKey()+"->"+entry.getValue().toString());
	}
	
	public static void showResourcesState(int resType) {
		if(resType==0) {
			String[] spri_keys = sprinkler.keySet().toArray(new String[0]);
			for(int i=0;i<spri_keys.length;i++) {
				CoapClient client = new CoapClient(sprinkler.get(spri_keys[i]).getResURI());
				CoapResponse res = client.get();
				String code = res.getCode().toString();
				if(!code.startsWith("2")) {
					System.err.println("Error with code: "+code);
					return;
				}
				String resText = res.getResponseText();
				resText = resText.replace("}", "");
				String[] splitRes = resText.split(":");
				String strValue = splitRes[1];
				boolean value=false;
				if(strValue.endsWith("N"))
					value=true;
				sprinkler.get(spri_keys[i]).setActive(value);
				System.out.println("SPRINKLER "+spri_keys[i]+ " IS: "+strValue);
			}
		}else if(resType==1) {
			String[] hum_keys = hum_sensor.keySet().toArray(new String[0]);
			for(int i=0;i<hum_keys.length;i++) {
				CoapClient client = new CoapClient(hum_sensor.get(hum_keys[i]).getResURI());
				CoapResponse res = client.get();
				String code = res.getCode().toString();
				if(!code.startsWith("2")) {
					System.err.println("Error with code: "+code);
					return;
				}
				String resText = res.getResponseText();
				resText = resText.replace("}", "");
				String[] splitRes = resText.split(":");
				int value = Integer.parseInt(splitRes[1]);
				hum_sensor.get(hum_keys[i]).setValue(value);
				System.out.println("HUMIDITY SENSOR "+hum_keys[i]+ " VALUE IS: "+value);
			}
		}else if(resType==2) {
			String[] temp_keys = temp_sensor.keySet().toArray(new String[0]);
			for(int i=0;i<temp_keys.length;i++) {
				CoapClient client = new CoapClient(temp_sensor.get(temp_keys[i]).getResURI());
				CoapResponse res = client.get();
				String code = res.getCode().toString();
				if(!code.startsWith("2")) {
					System.err.println("Error with code: "+code);
					return;
				}
				String resText = res.getResponseText();
				resText = resText.replace("}", "");
				String[] splitRes = resText.split(":");
				int value = Integer.parseInt(splitRes[1]);
				temp_sensor.get(temp_keys[i]).setValue(value);
				System.out.println("TEMPERATURE SENSOR "+temp_keys[i]+ " VALUE IS: "+value);
			}
		}
	}
	
	public static void showRegisteredSprinkler() {
		int id=0;
		for(Map.Entry<String, Sprinkler> entry: sprinkler.entrySet() ) {
			System.out.println(id+") "+entry.getKey()+"->"+entry.getValue().toString());
			id++;
		}
	}
	
	public static void startServer() {
		new Thread(){
			public void run() {
				MyServer s = new MyServer();
				s.startServer();
			}
		}.start();
	}
	
	public static void commandLine() {
		System.out.println("\n***********************\n");
		System.out.println("****** MAIN MENU ******\n");
		System.out.println("***********************\n");
		System.out.println("Please insert a command:...\n");
		System.out.println("0 - Show all registered resources\n");
		System.out.println("1 - Change sprinkler status\n");
		System.out.println("2 - Show resources status\n");
		System.out.print(">>>>");
	}

	
	public static void changeSprinklerStatus(int id) {
		if(id >= sprinkler.entrySet().size() || id < 0) {
			System.out.println("-------- NODE IS NOT PRESENT! RETRY... --------\n");
			commandLine();
		}
		String[] keys = sprinkler.keySet().toArray(new String[0]);
		boolean state = sprinkler.get(keys[id]).isActive();
		CoapClient client = new CoapClient(sprinkler.get(keys[id]).getResURI());
		String str_state = (state==true ? "OFF" : "ON");
		
		CoapResponse response = client.post("active="+str_state, MediaTypeRegistry.TEXT_PLAIN);
		
		String code = response.getCode().toString();
		if(!code.startsWith("2")) {
			System.out.println("ERROR CODE: "+code);
			return;
		}
		sprinkler.get(keys[id]).setActive(!state);
		System.out.println("SPRINKLER IS NOW "+str_state);
	}

}
