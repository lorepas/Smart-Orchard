
public class Resource {
	private String path;
	private String add;
	
	public Resource(String path, String add) {
		super();
		this.path = path;
		this.add = add;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getAdd() {
		return add;
	}
	
	public void setAdd(String add) {
		this.add = add;
	}
	
	public String getResURI() {
		return "coap://["+this.add+"]:5683/"+this.path;
	}
}
