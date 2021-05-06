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
	
	public void getAllValuesCOAP() {
		CoapClient client = new CoapClient(this.getResURI());
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
		this.setActive(value);
		this.setSprinkling(sprinkling);
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
