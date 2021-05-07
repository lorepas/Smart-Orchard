import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

public class TemperatureSensor extends Resource {
	
	private int value;
	private int temperature_threshold;

	public TemperatureSensor(String path, String add, String orchard) {
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
		return "temperature sensor set to: "+this.getValue()+" with a threshold setted to: "+this.getTemperature_threshold();
	}

	public int getTemperature_threshold() {
		return temperature_threshold;
	}

	public void setTemperature_threshold(int temperature_threshold) {
		this.temperature_threshold = temperature_threshold;
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
		int thrTmp = Integer.parseInt(thrValue);
		this.setValue(value);
		this.setTemperature_threshold(thrTmp);
	}*/
}
