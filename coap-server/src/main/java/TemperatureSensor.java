
public class TemperatureSensor extends Resource {
	
	private int value;

	public TemperatureSensor(String path, String add) {
		super(path, add);
		// TODO Auto-generated constructor stub
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		return "temperature sensor set to:\t"+this.getValue();
	}
}
