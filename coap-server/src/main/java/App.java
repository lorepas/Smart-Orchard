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
				case(1):
					System.out.println("-------- THIS IS A LIST OF ACTIVE NODES: --------\n");
					for(String s: RegistrationResource.nodes) {
						System.out.println("- "+s+"\n");
					}
					System.out.println("Please, select a NodeID:...\n");
					System.out.print(">>>>");
					int nodeId = command.nextInt();
					if(nodeId<0 || nodeId>10) {
						System.out.println("-------- NODE ID NOT VALID! RETRY... --------\n");
						break;
					}
					changeSprinklerStatus(nodeId);
					break;
				default:
					System.out.println("-------- COMMAND WRONG! RETRY... --------\n");
					break;
			}
					
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
		System.out.println("***********************\n");
		System.out.println("****** MAIN MENU ******\n");
		System.out.println("***********************\n");
		System.out.println("Please insert a command:...\n");
		System.out.println("1 - Change sprinkler status\n");
		System.out.print(">>>>");
	}
	
	public static void changeSprinklerStatus(int id) {
		String key = RegistrationResource.nodes.get(id);
		System.out.println(key);
		boolean state = sprinkler.get(key).isActive();
		
		CoapClient client = new CoapClient(sprinkler.get(key).getResURI());
		state = (state==true ? false : true);
		
		CoapResponse response = client.post("active="+state, MediaTypeRegistry.TEXT_PLAIN);
		
		String code = response.getCode().toString();
		if(!code.startsWith("2")) {
			System.out.println("ERROR CODE: "+code);
			return;
		}
		sprinkler.get(key).setActive(state);
		System.out.println("SPRINKLER IS NOW "+ (state==true ? "ACTIVE":"NOT ACTIVE"));
	}

}
