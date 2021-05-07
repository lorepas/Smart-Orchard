import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

public class HumiditySensor extends Resource {
	
	private int value;
	private int humidity_threshold;

	public HumiditySensor(String path, String add, String orchard) {
		super(path, add, orchard);
		// TODO Auto-generated constructor stub
	}

	public int getValue() {
		return value;
	}
	
	public void setValue(int v) {
		this.value = v;
	}
	
	public String toString() {
		return "humidity sensor set to: "+this.getValue()+" with a threshold setted to: "+this.getHumidity_threshold();
	}

	public int getHumidity_threshold() {
		return humidity_threshold;
	}

	public void setHumidity_threshold(int humidity_threshold) {
		this.humidity_threshold = humidity_threshold;
	}
	
	/*public void getAllValuesCOAP() {
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
		String thrValue = splitRes2[1];
		strValue = strValue.substring(1,strValue.length()-1); //delete double quotes
		thrValue = thrValue.substring(1,thrValue.length()-1);
		int value = Integer.parseInt(strValue);
		int thrHum = Integer.parseInt(thrValue);
		this.setValue(value);
		this.setHumidity_threshold(thrHum);
	}*/

}
