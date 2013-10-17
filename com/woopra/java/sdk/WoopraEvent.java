package com.woopra.java.sdk;

import com.woopra.json.JSONObject;

/**
 * A custom event class for Woopra
 * @author Antoine Chkaiban
 * @version 2013-09-30
 */
public class WoopraEvent {
	
	/**
	 * The Event's name
	 */
	public String name;
	
	
	/**
	 * The event's properties
	 */
	public JSONObject properties = new JSONObject();
	
	/**
	 * Public Constructor
	 */
	public WoopraEvent() {}
	
	/**
	 * Public Constructor
	 * @param eventName
	 */
	public WoopraEvent(String eventName) {
		this.name = eventName;
	}
	
	/**
	 * Public Constructor
	 * @param eventName
	 * @param properties 	(Object[][]) 2D Array containing Arrays of size 2 where:<br>
	 * 				key (String) - property name<br>
	 * 				value (String, int, boolean) - property value<br>
	 */
	public WoopraEvent(String eventName, Object[][] properties) {
		this.name = eventName;
		for(Object[] keyValue : properties) {
			this.properties.put((String) keyValue[0], keyValue[1]);
		}
	}
	
	/**
	 * Set the event's name.
	 * @param eventName
	 * @return WoopraEvent
	 */
	public WoopraEvent setName(String eventName) {
		this.name = eventName;
		return this;
	}
	
	/**
	 * Set one event property
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, Object value) {
		this.properties.put(key, value);
	}
	
	/** 
	 * Return the event properties as a JSONObject
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.properties.toString();
	}
}