
public class Sprinkler extends Resource {
	
	private boolean active;

	public Sprinkler(String path, String add, String orchard) {
		super(path, add, orchard);
		// TODO Auto-generated constructor stub
		setActive(true);
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
