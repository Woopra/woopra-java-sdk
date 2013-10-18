package com.woopra.java.sdk;

import java.util.HashMap;

public class WoopraVisitor {
	
	public static String EMAIL = "email";
	public static String UNIQUE_ID = "uniqueId";
	
	protected HashMap<String, Object> properties = new HashMap<String, Object>();
	private String ipAddress = "";
	private String cookieValue = "";
	private String userAgent = "";
	
	public WoopraVisitor(String identifier, String value) {
		if (identifier.equals(WoopraVisitor.EMAIL)) {
			properties.put(WoopraVisitor.EMAIL, value);
			this.cookieValue = String.valueOf(Math.abs(value.hashCode()));
		} else if (identifier.equals(WoopraVisitor.UNIQUE_ID)) {
			this.cookieValue = value;
		}
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setProperty(String key, Object value) {
		this.properties.put(key, value);
	}
	
	public String toString() {
		return this.properties.toString();
	}
}
