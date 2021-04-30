import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.CaliforniumLogger;
public class MyServer extends CoapServer {
	
	static {
		CaliforniumLogger.disableLogging();
	}
	
	public void startServer() {
		this.add(new RegistrationResource("registration"));
		this.start();
	}

}
