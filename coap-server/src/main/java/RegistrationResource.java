import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RegistrationResource extends CoapResource {
	
	private final static int MAX_SPRINKLER = 10;
	private int actualSprinkler = 0;
	
	public static final ArrayList<String> nodes = new ArrayList<String>(Arrays.asList("Insalata","Pomodoro"));

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
		System.out.println("RESPONSE:\n"+responseText);
	}

}
