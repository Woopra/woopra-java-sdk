package com.woopra.java.sdk;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import com.woopra.json.JSONException;
import com.woopra.sss.test.AsyncClient;

/**
 * Woopra Java SDK
 * This class enables back-end tracking in Java for Woopra.
 * Even though the getInstance is implemented for this class, note that the design of
 * this object is such that a new instance should be created for each new request.
 * @author Antoine Chkaiban
 * @version 2013-09-30
 */
public class WoopraTracker {
	
	private static String SDK_ID = "java";
	protected static int defaultTimeout;
	private static HashMap<String, WoopraTracker> instances;
	private String domain;
	private int idleTimeout;

	
	static {
		WoopraTracker.defaultTimeout = 300000;
		WoopraTracker.instances = new HashMap<String, WoopraTracker>();
    }
	
	/**
	 * If an instance of WoopraTracker already exists, this method returns it.
	 * It creates a new instance otherwise.
	 * @return WoopraTracker
	 */
	public synchronized final static WoopraTracker getInstance(String domain) {
		WoopraTracker instance = WoopraTracker.instances.get(domain);
		if (instance != null) {
    		return instance;
    	} else {
    		instance = new WoopraTracker(domain);
    		WoopraTracker.instances.put(domain, instance);
    		return instance;
    	}
	}

	/**
	 * Public Constructor
	 */
	public WoopraTracker(String domain) {
		this.domain = domain;
		this.idleTimeout = WoopraTracker.defaultTimeout;
	}
	
	/**
	 * Tracks a custom event through the back-end.
	 * @param event	(WoopraEvent) the event to track
	 * @return WoopraTracker 
	 */
	public void track(WoopraVisitor visitor, WoopraEvent event) {
		if (visitor != null && event != null) {
			this.woopraHttpRequest(event, visitor);
		}
	}
	
	/**
	 * Pushes the identified user to Woopra through the back-end.
	 * Please note that the identified user is automatically pushed with any tracking event.
	 * Therefore, this method is useful if you need to identify a user without tracking any event.
	 */
	public void push(WoopraVisitor visitor) {
		if (visitor != null) {
			this.woopraHttpRequest(null, visitor);
		}
	}
	
	/**
	 * Get the value of the idle timeout (time in milliseconds after which the user is considered offline)
	 * @return idle timeout
	 */
	public int getIdle_timeout() {
		return idleTimeout;
	}

	/**
	 * Set the value of the idle timeout (time in milliseconds after which the user is considered offline)
	 * @param idle_timeout
	 */
	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	private void woopraHttpRequest(WoopraEvent event, WoopraVisitor visitor) {
		try {
			String baseUrl = "http://www.woopra.com/track/";
			String configParams = "?host=".concat(URLEncoder.encode((String) this.domain, "UTF-8"));
			configParams = configParams.concat("&cookie=").concat(URLEncoder.encode(visitor.getCookieValue(), "UTF-8"));
			if (! visitor.getIpAddress().equals("")) {
				configParams = configParams.concat("&ip=").concat(URLEncoder.encode(visitor.getIpAddress(), "UTF-8"));
			}
			configParams = configParams.concat("&timeout=").concat(URLEncoder.encode(String.valueOf(this.idleTimeout), "UTF-8"));
			String userParams = "";
			Iterator<String> userKeys = visitor.properties.keySet().iterator();
			while (userKeys.hasNext()) {
				String key = userKeys.next();
				String value = visitor.properties.get(key).toString();
				System.out.println(key.concat(" : ").concat(value));
				userParams = userParams.concat("&cv_").concat(URLEncoder.encode(key, "UTF-8")).concat("=").concat(URLEncoder.encode(value, "UTF-8"));
			}
			String url;
			if ( event == null ) {
				url = baseUrl.concat("identify/").concat(configParams).concat(userParams).concat("&ce__w_app=").concat(WoopraTracker.SDK_ID);
			} else {
				String eventParams = "";
				eventParams = eventParams.concat("&ce_name=").concat(URLEncoder.encode(event.name, "UTF-8"));
				@SuppressWarnings("unchecked")
				Iterator<String> eventKeys = event.properties.keys();
				while (eventKeys.hasNext()) {
					String key = eventKeys.next();
					String value = event.properties.get(key).toString();
					eventParams = eventParams.concat("&ce_").concat(URLEncoder.encode(key, "UTF-8")).concat("=").concat(URLEncoder.encode(value, "UTF-8"));
				}
				url = baseUrl.concat("ce/").concat(configParams).concat(userParams).concat(eventParams).concat("&ce__w_app=").concat(WoopraTracker.SDK_ID);
			}
			AsyncClient.getInstance().send(new URL(url), visitor.getUserAgent().equals("") ? null : new String[] {"User-Agent: ".concat(visitor.getUserAgent())});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
