
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
}
