
public class TemperatureSensor extends Resource {
	
	private int value;

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
		return "temperature sensor set to:\t"+this.getValue();
	}
}
