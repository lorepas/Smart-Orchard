
public class HumiditySensor extends Resource {
	
	private int value;

	public HumiditySensor(String path, String add) {
		super(path, add);
		// TODO Auto-generated constructor stub
	}

	public int getValue() {
		return value;
	}
	
	public void setValue(int v) {
		this.value = v;
	}
	
	public String toString() {
		return "humidity sensor set to:\t"+this.getValue();
	}

}
