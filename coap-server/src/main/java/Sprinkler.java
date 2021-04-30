
public class Sprinkler extends Resource {
	
	private boolean active;

	public Sprinkler(String path, String add) {
		super(path, add);
		// TODO Auto-generated constructor stub
		setActive(false);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String toString() {
		String s = ((this.active==true) ? "ON":"OFF");
		return "sprinkler is:\t"+s;
	}

}
