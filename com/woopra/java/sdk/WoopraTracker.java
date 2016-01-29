package com.woopra.java.sdk;

import com.woopra.json.JSONException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Woopra Java SDK This class enables back-end tracking in Java for Woopra. Even
 * though the getInstance is implemented for this class, note that the design of
 * this object is such that a new instance should be created for each new
 * request.
 *
 * @author Antoine Chkaiban
 * @version 2013-09-30
 */
public class WoopraTracker {

    private static final String SDK_ID = "java";
    protected static int defaultTimeout = 300 * 1000;
    private static final Map<String, WoopraTracker> instances = new ConcurrentHashMap();
    /**
     *
     */
    private final String domain;
    private int idleTimeout;

    /**
     *
     */
    private boolean httpAuthEnable = false;
    private String httpAuthUser = null, httpAuthPassword = null;

    /**
     * If an instance of WoopraTracker already exists, this method returns it.
     * It creates a new instance otherwise.
     *
     * @param domain
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
     *
     * @param domain
     */
    public WoopraTracker(String domain) {
        this.domain = domain;
        this.idleTimeout = WoopraTracker.defaultTimeout;
    }

    /**
     * Tracks a custom event through the back-end.
     *
     * @param visitor
     * @param event	(WoopraEvent) the event to track
     */
    public void track(WoopraVisitor visitor, WoopraEvent event) {
        if (visitor != null && event != null) {
            this.woopraHttpRequest(event, visitor);
        }
    }

    /**
     * Pushes the identified user to Woopra through the back-end. Please note
     * that the identified user is automatically pushed with any tracking event.
     * Therefore, this method is useful if you need to identify a user without
     * tracking any event.
     *
     * @param visitor
     */
    public void push(WoopraVisitor visitor) {
        if (visitor != null) {
            this.woopraHttpRequest(null, visitor);
        }
    }

    /**
     * Set the value of the idle timeout (time in milliseconds after which the
     * user is considered offline)
     *
     * @param idleTimeout
     */
    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    /**
     *
     * @param user
     * @param password
     */
    public void enableBasicAuth(String user, String password) {
        this.httpAuthEnable = true;
        this.httpAuthUser = user;
        this.httpAuthPassword = password;
    }

    /**
     *
     * @param event
     * @param visitor
     */
    private void woopraHttpRequest(WoopraEvent event, WoopraVisitor visitor) {
        try {
            String baseUrl = "http://www.woopra.com/track/";
            String configParams = "?host=".concat(URLEncoder.encode((String) this.domain, "UTF-8"));
            configParams = configParams.concat("&cookie=").concat(URLEncoder.encode(visitor.getCookieValue(), "UTF-8"));
            if (!visitor.getIpAddress().equals("")) {
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
            if (event == null) {
                url = baseUrl.concat("identify/").concat(configParams).concat(userParams).concat("&ce_app=").concat(WoopraTracker.SDK_ID);
            } else {
                String eventParams = "";
                eventParams = eventParams.concat("&event=").concat(URLEncoder.encode(event.name, "UTF-8"));
                @SuppressWarnings("unchecked")
                Iterator<String> eventKeys = event.properties.keys();
                while (eventKeys.hasNext()) {
                    String key = eventKeys.next();
                    String value = event.properties.get(key).toString();
                    eventParams = eventParams.concat("&ce_").concat(URLEncoder.encode(key, "UTF-8")).concat("=").concat(URLEncoder.encode(value, "UTF-8"));
                }
                url = baseUrl.concat("ce/").concat(configParams).concat(userParams).concat(eventParams).concat("&ce_app=").concat(WoopraTracker.SDK_ID);
            }

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            if (this.httpAuthEnable) {
                conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((this.httpAuthUser + ":" + this.httpAuthPassword).getBytes()));
            }

            conn.setConnectTimeout(4000);
            conn.setReadTimeout(6000);

            if (visitor.getUserAgent().equals("") == false) {
                conn.addRequestProperty("User-Agent", visitor.getUserAgent());
            }

            InputStream in = conn.getInputStream();
            in.read();
            in.close();

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
