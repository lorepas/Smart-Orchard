import org.eclipse.californium.core.*;
import org.json.simple.*;

public class ObserveCoapClient extends CoapClient {
	private Sprinkler sprinkler;
	private HumiditySensor hum_sen;
	private TemperatureSensor tem_sen;
	CoapObserveRelation cor;
	
	public ObserveCoapClient(Sprinkler s) {
		super(s.getResURI());
		this.sprinkler = s;
	}
	
	public ObserveCoapClient(HumiditySensor hs) {
		super(hs.getResURI());
		this.hum_sen = hs;
	}
	
	public ObserveCoapClient(TemperatureSensor ts) {
		super(ts.getResURI());
		this.tem_sen = ts;
	}
	
	public void startCoapObserve() {
		cor = this.observe(new CoapHandler() {
			public void onLoad(CoapResponse response) {
				try {
					String s = "";
					if(App.obs==true) {
						JSONObject jsonOb = (JSONObject) JSONValue.parseWithException(response.getResponseText());
						System.out.println(jsonOb.toJSONString());
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			public void onError() {
				System.out.println("-------- REQ TIMEOUT OR REJECT OBSERVING --------\n");
			}
		});
	}
	
	
}
