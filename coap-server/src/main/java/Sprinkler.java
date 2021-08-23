import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

public class Sprinkler extends Resource {
	
	private boolean active;
	private boolean sprinkling;

	public Sprinkler(String path, String add, String orchard) {
		super(path, add, orchard);
		// TODO Auto-generated constructor stub
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isSprinkling() {
		return sprinkling;
	}

	public void setSprinkling(boolean sprinkling) {
		this.sprinkling = sprinkling;
	}
	
	public String toString() {
		if(this.active==true) {
			if(this.sprinkling==true) {
				return "sprinkler is ON and IS SPRINKLING";
			}else {
				return "sprinkler is ON but IS NOT SPRINKLING";
			}
		}else {
			return "sprinkler is OFF";
		}
	}

}
