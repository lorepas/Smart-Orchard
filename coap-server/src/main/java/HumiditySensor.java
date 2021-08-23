import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

public class HumiditySensor extends Resource {
	
	private int value;
	private int humidity_threshold;

	public HumiditySensor(String path, String add, String orchard) {
		super(path, add, orchard);
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
}
