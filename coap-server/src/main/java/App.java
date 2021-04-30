import java.util.*;

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
			commandLine();
			if(waitReg)
				System.out.println("-------- WAITING RESOURCE REGISTRATION --------\n");
			System.out.print(">>>>");
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
					System.out.println("Please, select a Sprinkler ID:...\n");
					System.out.print(">>>>");
					int nodeId = command.nextInt();
					changeSprinklerStatus(nodeId,"sprinkler");
					break;
				default:
					System.out.println("-------- COMMAND WRONG! RETRY... --------\n");
					break;
			}
					
		}
	}
	
	public static void showRegisteredResource() {
		for(Map.Entry<String, Sprinkler> entry: sprinkler.entrySet() )
			System.out.println(entry.getKey()+"_"+entry.getValue().getAdd().toString()+"->"+entry.getValue().toString());
		for(Map.Entry<String, HumiditySensor> entry: hum_sensor.entrySet() )
			System.out.println(entry.getKey()+"_"+entry.getValue().getAdd().toString()+"->"+entry.getValue().toString());
		for(Map.Entry<String, TemperatureSensor> entry: temp_sensor.entrySet() )
			System.out.println(entry.getKey()+"_"+entry.getValue().getAdd().toString()+"->"+entry.getValue().toString());
	}
	
	public static void showRegisteredSprinkler() {
		int id=0;
		for(Map.Entry<String, Sprinkler> entry: sprinkler.entrySet() )
			System.out.println(id+") "+entry.getKey()+"_"+entry.getValue().getAdd().toString()+"->"+entry.getValue().toString());
			id++;
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
		System.out.println("***********************\n");
		System.out.println("****** MAIN MENU ******\n");
		System.out.println("***********************\n");
		System.out.println("Please insert a command:...\n");
		System.out.println("0 - Show all registered resources\n");
		System.out.println("1 - Change sprinkler status\n");
	}

	
	public static void changeSprinklerStatus(int id, String res) {
		boolean state = sprinkler.get(res).isActive();
		CoapClient client = new CoapClient(sprinkler.get(res).getResURI());
		String str_state = (state==true ? "OFF" : "ON");
		
		CoapResponse response = client.post("active="+str_state, MediaTypeRegistry.TEXT_PLAIN);
		
		String code = response.getCode().toString();
		if(!code.startsWith("2")) {
			System.out.println("ERROR CODE: "+code);
			return;
		}
		sprinkler.get("sprinkler").setActive(!state);
		System.out.println("SPRINKLER IS NOW "+str_state);
	}

}
