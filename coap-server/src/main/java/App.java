import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class App {
	
	public static Map<String,Sprinkler> sprinkler = new HashMap<String,Sprinkler>();
	public static Map<String,HumiditySensor> hum_sensor = new HashMap<String,HumiditySensor>();
	public static Map<String,TemperatureSensor> temp_sensor = new HashMap<String,TemperatureSensor>();
	public static boolean waitReg = true;
	public static String orchard_type = new String();
	public static int res_number = 0;
	
	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
		// TODO Auto-generated method stub
		System.out.println("-------- WELCOME TO OUR SMART-ORCHARD --------\n");
		System.out.print("First of all, tell us how many resources you want to deploy: ");
		while(res_number<=0) {
			BufferedReader num_res = new BufferedReader(new InputStreamReader(System.in));
			res_number = Integer.parseInt(num_res.readLine());
			if(res_number<=0)
				System.out.print("Please, try with a number greater than 0: ");
		}
		System.out.println("-------- WAITING RESOURCE REGISTRATION --------\n");
		startServer();

		while(true) {
			while(waitReg){
				TimeUnit.SECONDS.sleep(30);
			}
			initializeResources();
			commandLine();
			BufferedReader command = new BufferedReader(new InputStreamReader(System.in));
			int command_code = Integer.parseInt(command.readLine());
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
					BufferedReader command_type = new BufferedReader(new InputStreamReader(System.in));
					int nodeId = Integer.parseInt(command_type.readLine());
					changeSprinklerStatus(nodeId);
					break;
				case(2):
					System.out.println("Which resources are you looking for?\n");
					System.out.println("0 - Sprinklers\n");
					System.out.println("1 - Humidity sensors\n");
					System.out.println("2 - Temperature sensors\n");
					System.out.print(">>>>");
					BufferedReader command_res = new BufferedReader(new InputStreamReader(System.in));
					int res = Integer.parseInt(command_res.readLine());
					showResourcesState(res);
					break;
				case(3):
					System.out.println("Which sensors do you want to change threshold?\n");
					System.out.println("0 - Humidity sensors\n");
					System.out.println("1 - Temperature sensors\n");
					System.out.print(">>>>");
					BufferedReader command_sens = new BufferedReader(new InputStreamReader(System.in));
					int sens = Integer.parseInt(command_sens.readLine());
					changeResourcesStatus(sens);
					break;
				default:
					System.out.println("-------- COMMAND WRONG! RETRY... --------\n");
					break;
			}
		}
	}
	
	public static void commandLine() {
		System.out.println("\n***********************\n");
		System.out.println("****** MAIN MENU ******\n");
		System.out.println("***********************\n");
		System.out.println("Please insert a command:...\n");
		System.out.println("0 - Show all registered resources\n");
		System.out.println("1 - Change sprinkler status\n");
		System.out.println("2 - Show resources status\n");
		System.out.println("3 - Change sensors thresholds\n");
		System.out.print(">>>>");
	}
	
	public static void showRegisteredResource() {
		for(Map.Entry<String, Sprinkler> entry: sprinkler.entrySet())
			System.out.println(entry.getKey()+"->"+entry.getValue().getOrchard());
		for(Map.Entry<String, HumiditySensor> entry: hum_sensor.entrySet() )
			System.out.println(entry.getKey()+"->"+entry.getValue().getOrchard());
		for(Map.Entry<String, TemperatureSensor> entry: temp_sensor.entrySet() )
			System.out.println(entry.getKey()+"->"+entry.getValue().getOrchard());
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
				String[] split1 = resText.split(",");
				String[] splitRes1 = split1[0].split(":");
				String[] splitRes2 = split1[1].split(":");
				String strValue = splitRes1[1];
				String sprValue = splitRes2[1];
				strValue = strValue.substring(1,strValue.length()-1); //delete double quotes
				sprValue = sprValue.substring(1,sprValue.length()-1);
				boolean value=false;
				boolean sprinkling=false;
				if(strValue.endsWith("N")) //ON
					value=true;
				if(sprValue.endsWith("S")) //YES
					sprinkling=true;
				sprinkler.get(spri_keys[i]).setActive(value);
				sprinkler.get(spri_keys[i]).setSprinkling(sprinkling);
				System.out.println("SPRINKLER "+spri_keys[i]+ " -> "+sprinkler.get(spri_keys[i]).toString());
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
				String[] split1 = resText.split(",");
				String[] splitRes1 = split1[0].split(":");
				String[] splitRes2 = split1[1].split(":");
				String strValue = splitRes1[1];
				String thrValue = splitRes2[1];
				strValue = strValue.substring(1,strValue.length()-1); //delete double quotes
				thrValue = thrValue.substring(1,thrValue.length()-1);
				int value = Integer.parseInt(strValue);
				int thrHum = Integer.parseInt(thrValue);
				hum_sensor.get(hum_keys[i]).setValue(value);
				hum_sensor.get(hum_keys[i]).setHumidity_threshold(thrHum);
				System.out.println("HUMIDITY SENSOR "+hum_keys[i]+ " -> "+hum_sensor.get(hum_keys[i]).toString());
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
				String[] split1 = resText.split(",");
				String[] splitRes1 = split1[0].split(":");
				String[] splitRes2 = split1[1].split(":");
				String strValue = splitRes1[1];
				String thrValue = splitRes2[1];
				strValue = strValue.substring(1,strValue.length()-1); //delete double quotes
				thrValue = thrValue.substring(1,thrValue.length()-1);
				int value = Integer.parseInt(strValue);
				int thrTmp = Integer.parseInt(thrValue);
				temp_sensor.get(temp_keys[i]).setValue(value);
				temp_sensor.get(temp_keys[i]).setTemperature_threshold(thrTmp);
				System.out.println("TEMPERATURE SENSOR "+temp_keys[i]+ " -> "+temp_sensor.get(temp_keys[i]).toString());
			}
		}
	}
	
	public static void showRegisteredSprinkler() {
		int id=0;
		for(Map.Entry<String, Sprinkler> entry: sprinkler.entrySet() ) {
			System.out.println(id+") "+entry.getKey()+"->"+entry.getValue().toString()+" ("+entry.getValue().getOrchard()+")");
			id++;
		}
	}
	
	public static void showRegisteredHumiditySensor() {
		int id=0;
		for(Map.Entry<String, HumiditySensor> entry: hum_sensor.entrySet() ) {
			System.out.println(id+") "+entry.getKey()+"->"+entry.getValue().toString()+" ("+entry.getValue().getOrchard()+")");
			id++;
		}
	}
	
	
	public static void showRegisteredTemperatureSensor() {
		int id=0;
		for(Map.Entry<String, TemperatureSensor> entry: temp_sensor.entrySet() ) {
			System.out.println(id+") "+entry.getKey()+"->"+entry.getValue().toString()+" ("+entry.getValue().getOrchard()+")");
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
	
	public static void changeResourcesStatus(int res) throws NumberFormatException, IOException {
		if(res==0) {
			showRegisteredHumiditySensor();
		}else if(res==1) {
			showRegisteredTemperatureSensor();
		}else {
			System.out.println("-------- WRONG SELECTION! RETRY... --------\n");
			commandLine();
		}
		System.out.println("Please, select a resource ID:\n");
		System.out.print(">>>>");
		BufferedReader res_id = new BufferedReader(new InputStreamReader(System.in));
		int id = Integer.parseInt(res_id.readLine());
		if(res==0) {
			if(id >= hum_sensor.entrySet().size() || id < 0) {
				System.out.println("-------- NODE IS NOT PRESENT! RETRY... --------\n");
				commandLine();
			}
			String[] keys = hum_sensor.keySet().toArray(new String[0]);
			System.out.println("Please, digit a new humidity threshold (from 0 to 100):\n");
			System.out.print(">>>>");
			int thr_hum = -1;
			while(thr_hum<0||thr_hum>100) {
				BufferedReader res_thr = new BufferedReader(new InputStreamReader(System.in));
				thr_hum = Integer.parseInt(res_thr.readLine());
				if(res_number<0||thr_hum>100)
					System.out.print("Please, try with another number: ");
			}
			CoapClient client = new CoapClient(hum_sensor.get(keys[id]).getResURI());
			CoapResponse response = client.post("thr_hum="+thr_hum, MediaTypeRegistry.TEXT_PLAIN);
			String code = response.getCode().toString();
			if(!code.startsWith("2")) {
				System.out.println("ERROR CODE: "+code);
				return;
			}
			hum_sensor.get(keys[id]).setHumidity_threshold(thr_hum);
			System.out.println("HUMIDITY THRESHOLD SETTED TO "+thr_hum);
		}else if(res==1) {
			if(id >= temp_sensor.entrySet().size() || id < 0) {
				System.out.println("-------- NODE IS NOT PRESENT! RETRY... --------\n");
				commandLine();
			}
			String[] keys = temp_sensor.keySet().toArray(new String[0]);
			System.out.println("Please, digit a new temperature threshold (from 0 to 32):\n");
			System.out.print(">>>>");
			int thr_tmp = -1;
			while(thr_tmp<0||thr_tmp>32) {
				BufferedReader res_thr = new BufferedReader(new InputStreamReader(System.in));
				thr_tmp = Integer.parseInt(res_thr.readLine());
				if(res_number<0||thr_tmp>32)
					System.out.print("Please, try with another number: ");
			}
			CoapClient client = new CoapClient(temp_sensor.get(keys[id]).getResURI());
			CoapResponse response = client.post("thr_tmp="+thr_tmp, MediaTypeRegistry.TEXT_PLAIN);
			String code = response.getCode().toString();
			if(!code.startsWith("2")) {
				System.out.println("ERROR CODE: "+code);
				return;
			}
			temp_sensor.get(keys[id]).setTemperature_threshold(thr_tmp);
			System.out.println("TEMPERATURE THRESHOLD SETTED TO "+thr_tmp);
		}
	}
	
	public static void initializeResources() {
		String[] spri_keys = sprinkler.keySet().toArray(new String[0]);
		String[] hum_keys = hum_sensor.keySet().toArray(new String[0]);
		String[] temp_keys = temp_sensor.keySet().toArray(new String[0]);
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
			String[] split1 = resText.split(",");
			String[] splitRes1 = split1[0].split(":");
			String[] splitRes2 = split1[1].split(":");
			String strValue = splitRes1[1];
			String sprValue = splitRes2[1];
			strValue = strValue.substring(1,strValue.length()-1); //delete double quotes
			sprValue = sprValue.substring(1,sprValue.length()-1);
			boolean value=false;
			boolean sprinkling=false;
			if(strValue.endsWith("N")) //ON
				value=true;
			if(sprValue.endsWith("S")) //YES
				sprinkling=true;
			sprinkler.get(spri_keys[i]).setActive(value);
			sprinkler.get(spri_keys[i]).setSprinkling(sprinkling);
		}
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
			String[] split1 = resText.split(",");
			String[] splitRes1 = split1[0].split(":");
			String[] splitRes2 = split1[1].split(":");
			String strValue = splitRes1[1];
			String thrValue = splitRes2[1];
			strValue = strValue.substring(1,strValue.length()-1); //delete double quotes
			thrValue = thrValue.substring(1,thrValue.length()-1);
			int value = Integer.parseInt(strValue);
			int thrHum = Integer.parseInt(thrValue);
			hum_sensor.get(hum_keys[i]).setValue(value);
			hum_sensor.get(hum_keys[i]).setHumidity_threshold(thrHum);
		}		
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
			String[] split1 = resText.split(",");
			String[] splitRes1 = split1[0].split(":");
			String[] splitRes2 = split1[1].split(":");
			String strValue = splitRes1[1];
			String thrValue = splitRes2[1];
			strValue = strValue.substring(1,strValue.length()-1); //delete double quotes
			thrValue = thrValue.substring(1,thrValue.length()-1);
			int value = Integer.parseInt(strValue);
			int thrTmp = Integer.parseInt(thrValue);
			temp_sensor.get(temp_keys[i]).setValue(value);
			temp_sensor.get(temp_keys[i]).setTemperature_threshold(thrTmp);
		}
	}

}
