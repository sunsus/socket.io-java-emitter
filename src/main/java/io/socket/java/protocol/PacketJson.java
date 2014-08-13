package io.socket.java.protocol;

import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.msgpack.packer.Packer;


public class PacketJson extends Packet {
	private Object data;
	private String event; 
	
	public PacketJson() {}
	
	protected void packData(Packer packer) throws IOException {
		packer.writeArrayBegin(2);
		packer.write(event);
		if(data instanceof JSONObject) {
			this.parseObject(((JSONObject)data), packer);
		} else if(data instanceof JSONArray) {
			this.parseArray(((JSONArray)data), packer);
		}
		packer.writeArrayEnd(true);
	}

	private void parseObject(JSONObject json, Packer packer) throws JSONException, IOException {
	    @SuppressWarnings("unchecked")
		Iterator<String> keys = json.keys();
	    
	    packer.writeMapBegin(json.length());
	    while(keys.hasNext()){
	        String key = keys.next();
        	Object obj = json.get(key); 
            packer.write(key);
    		if(obj instanceof JSONArray) {
    			this.parseArray(json.getJSONArray(key), packer);
    		} else if(obj instanceof JSONObject) {
    			this.parseObject(json.getJSONObject(key), packer);
    		} else {
    			
    			this.paseObject(obj, packer);
    		}
	    }
	    packer.writeMapEnd(true);
	}
	
	private void parseArray(JSONArray arr, Packer packer) throws JSONException, IOException {
		packer.writeArrayBegin(arr.length());
    	for (int i = 0; i < arr.length(); i++) {
    		if(arr.get(i) instanceof JSONArray) {
    			this.parseArray(arr.getJSONArray(i), packer);
    		} else if(arr.get(i) instanceof JSONObject) {
    			this.parseObject(arr.getJSONObject(i), packer);
    		} else {
    			this.paseObject(arr.get(i), packer);
    		}
    	}
    	packer.writeArrayEnd(true);
	}
	
	private void paseObject(Object obj,  Packer packer) throws IOException {
        if(obj instanceof Integer){
        	packer.write(((Integer)obj));
        } else if (obj instanceof String) {
        	packer.write(((String)obj));
        } else if (obj instanceof Boolean) {
        	packer.write(((Boolean)obj));
        } else if (obj instanceof Double) {
        	packer.write(((Double)obj));
        } 
	}
	
	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(JSONObject data) {
		this.data = data;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(JSONArray data) {
		this.data = data;
	}


	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}
	
	
	
}
