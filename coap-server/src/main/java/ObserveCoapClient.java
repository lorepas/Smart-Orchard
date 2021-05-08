import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.sql.Timestamp;

import org.eclipse.californium.core.*;
import org.json.simple.*;

public class ObserveCoapClient extends CoapClient {
	private Sprinkler sprinkler;
	private HumiditySensor hum_sen;
	private TemperatureSensor tem_sen;
	String key_hum;
	String key_tem;
	String key_spr;
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
					JSONObject jsonOb = (JSONObject) JSONValue.parseWithException(response.getResponseText());
					if(jsonOb.containsKey("hum_value")) {
						int hum_value = Integer.parseInt(jsonOb.get("hum_value").toString());
						int hum_thr = Integer.parseInt(jsonOb.get("thr_hum").toString());
						for(String s: App.hum_sensor.keySet()) {
							if(hum_sen.equals(App.hum_sensor.get(s)))
								key_hum = s;
						}
						App.hum_sensor.get(key_hum).setHumidity_threshold(hum_thr);
						App.hum_sensor.get(key_hum).setValue(hum_value);
					}else if(jsonOb.containsKey("temp_value")) {
						int temp_value = Integer.parseInt(jsonOb.get("temp_value").toString());
						int temp_thr = Integer.parseInt(jsonOb.get("thr_tmp").toString());
						for(String s: App.temp_sensor.keySet()) {
							if(tem_sen.equals(App.temp_sensor.get(s)))
								key_tem = s;
						}
						App.temp_sensor.get(key_tem).setTemperature_threshold(temp_thr);
						App.temp_sensor.get(key_tem).setValue(temp_value);
					}else if(jsonOb.containsKey("active")) {
						String sprinkling = jsonOb.get("sprinkling").toString();
						String active = jsonOb.get("active").toString();
						for(String s: App.sprinkler.keySet()) {
							if(sprinkler.equals(App.sprinkler.get(s)))
								key_spr = s;
						}
						if(active.contains("ON"))
							App.sprinkler.get(key_spr).setActive(true);
						else
							App.sprinkler.get(key_spr).setActive(false);
						
						if(sprinkling.contains("YES"))
							App.sprinkler.get(key_spr).setSprinkling(true);
						else
							App.sprinkler.get(key_spr).setSprinkling(false);
						
					}
					
					if(App.obs==true) {
						Date date = new Date();
						long time = date.getTime();
						Timestamp t = new Timestamp(time);
						System.out.println("---------------------");
						System.out.println("--------- TIMESTAMP || "+t);
						System.out.println("---------------------");
						printAll();
						//System.out.println("SPRINKLER "+sprinkler+ " -> "+App.sprinkler.get(key_spr).toString());
						//System.out.println("HUMIDITY SENSOR "+key_hum+ " -> "+App.hum_sensor.get(key_hum).toString());
						//System.out.println("TEMPERATURE SENSOR "+key_tem+ " -> "+App.temp_sensor.get(key_tem).toString());
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
	
	public void printAll() {
		String[] spri_keys = App.sprinkler.keySet().toArray(new String[0]);
		String[] hum_keys = App.hum_sensor.keySet().toArray(new String[0]);
		String[] temp_keys = App.temp_sensor.keySet().toArray(new String[0]);
		
		for(int i=0;i<spri_keys.length;i++)
			System.out.println("SPRINKLER "+spri_keys[i]+ " -> "+App.sprinkler.get(spri_keys[i]).toString());
		
		for(int i=0;i<hum_keys.length;i++)
			System.out.println("HUMIDITY SENSOR "+hum_keys[i]+ " -> "+App.hum_sensor.get(hum_keys[i]).toString());
		
		for(int i=0;i<temp_keys.length;i++) 
			System.out.println("TEMPERATURE SENSOR "+temp_keys[i]+ " -> "+App.temp_sensor.get(temp_keys[i]).toString());
	}
}
