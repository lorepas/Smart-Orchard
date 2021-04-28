import java.util.*;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class App {
	
	public static Map<String,Sprinkler> sprinkler = new HashMap<String,Sprinkler>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		startServer();
		while(true) {
			commandLine();
			Scanner command = new Scanner(System.in);
			int command_code = command.nextInt();
			switch(command_code) {
				case(0):
					System.out.println("-------- THIS IS A LIST OF REGISTERED RESOURCE: --------\n");
					showRegisteredResource();
					break;
				case(1):
					System.out.println("-------- THIS IS A LIST OF ACTIVE NODES: --------\n");
					System.out.println("Please, select a NodeID:...\n");
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
			System.out.println(entry.getValue().toString());
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
		System.out.print(">>>>");
	}

	
	public static void changeSprinklerStatus(int id, String res) {
		boolean state = sprinkler.get(res).isActive();
		
		CoapClient client = new CoapClient(sprinkler.get(res).getResURI());
		//CoapClient client = new CoapClient("coap://[fd00::202:2:2:2]:5683/sprinkler");
		state = (state==true ? false : true);
		
		CoapResponse response = client.post("active="+state, MediaTypeRegistry.TEXT_PLAIN);
		
		String code = response.getCode().toString();
		if(!code.startsWith("2")) {
			System.out.println("ERROR CODE: "+code);
			return;
		}
		sprinkler.get("sprinkler").setActive(state);
		System.out.println("SPRINKLER IS NOW "+ (state==true ? "ACTIVE":"NOT ACTIVE"));
	}

}
