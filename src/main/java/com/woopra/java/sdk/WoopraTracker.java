package com.woopra.java.sdk;

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
import org.json.JSONException;

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

    /**
     *
     */
    private int idleTimeout;
    private boolean httpsEnable = false, httpAuthEnable = false;
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
        this.idleTimeout = defaultTimeout;
    }

    WoopraTracker() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Tracks a custom event through the back-end.
     *
     * @param visitor
     * @param event	(WoopraEvent) the event to track
     */
    public void track(WoopraVisitor visitor, WoopraEvent event) {
        if (visitor != null && event != null) {
            this.httpRequest(event, visitor);
        }
    }

    /**
     *
     * @param visitor
     */
    public void identify(WoopraVisitor visitor) {
        if (visitor != null) {
            this.httpRequest(null, visitor);
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
    @Deprecated
    public void push(WoopraVisitor visitor) {
        if (visitor != null) {
            this.httpRequest(null, visitor);
        }
    }

    /**
     *
     * @param httpsEnable
     */
    public void setSecureTracking(boolean httpsEnable) {
        this.httpsEnable = httpsEnable;
    }

    /**
     *
     * @param httpsEnable
     * @return
     */
    public WoopraTracker withSecureTracking(boolean httpsEnable) {
        this.httpsEnable = httpsEnable;
        return this;
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
     * Set the value of the idle timeout (time in milliseconds after which the
     * user is considered offline)
     *
     * @param idleTimeout
     * @return
     */
    public WoopraTracker withIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
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
     * Enables http basic auth
     *
     * @param user
     * @param password
     * @return
     */
    public WoopraTracker withBasicAuth(String user, String password) {
        this.httpAuthEnable = true;
        this.httpAuthUser = user;
        this.httpAuthPassword = password;
        return this;
    }

    /**
     *
     * @param event
     * @param visitor
     */
    private void httpRequest(WoopraEvent event, WoopraVisitor visitor) {
        try {
            StringBuilder url = new StringBuilder();
            url.append(((this.httpsEnable) ? "https" : "http") + "://www.woopra.com/track/");

            //Identify or Track
            if (event == null) {
                url.append("identify/");
            } else {
                url.append("ce/");
            }

            //Request Options
            url.append("?host=").append(URLEncoder.encode(this.domain, "UTF-8"));
            url.append("&app=").append(WoopraTracker.SDK_ID);
            url.append("&cookie=").append(URLEncoder.encode(visitor.getCookieValue(), "UTF-8"));
            url.append("&timeout=").append(this.idleTimeout);
            if (!visitor.getIpAddress().equals("")) {
                url.append("&ip=").append(URLEncoder.encode(visitor.getIpAddress(), "UTF-8"));
            }
            if (event != null && event.timestamp > 0) {
                url.append("&timestamp=").append(event.timestamp);
            }

            //visitor Props
            Iterator<String> userKeys = visitor.properties.keySet().iterator();
            while (userKeys.hasNext()) {
                String key = userKeys.next();
                String value = visitor.properties.get(key).toString();

                url.append("&cv_").append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
            }

            //event Props
            if (event != null) {
                url.append("&event=").append(URLEncoder.encode(event.name, "UTF-8"));
                @SuppressWarnings("unchecked")

                Iterator<String> eventKeys = event.properties.keys();
                while (eventKeys.hasNext()) {
                    String key = eventKeys.next();
                    String value = event.properties.get(key).toString();
                    url.append("&ce_").append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
                }
            }

            //Make Request
//            System.err.println("Sending Request: ".concat(url.toString()));
            HttpURLConnection conn = (HttpURLConnection) new URL(url.toString()).openConnection();
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
