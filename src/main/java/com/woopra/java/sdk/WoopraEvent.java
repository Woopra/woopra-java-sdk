package com.woopra.java.sdk;

import org.json.JSONObject;

/**
 * A custom event class for Woopra
 *
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
     * The unix ms time at which the event occurred
     * If left blank, the event timestamp will be the time at which
     * the tracking server processes the request
     */
    public long timestamp = -1;

    /**
     * Public Constructor
     *
     * @param eventName
     */
    public WoopraEvent(String eventName) {
        this.name = eventName;
    }

    /**
     * Public Constructor
     *
     * @param eventName
     * @param properties (Object[][]) 2D Array containing Arrays of size 2
     * where:<br>
     * key (String) - property name<br>
     * value (String, int, boolean) - property value<br>
     */
    public WoopraEvent(String eventName, Object[][] properties) {
        this.name = eventName;
        for (Object[] keyValue : properties) {
            this.properties.put((String) keyValue[0], keyValue[1]);
        }
    }

    /**
     * Set one event property
     *
     * @param key
     * @param value
     */
    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public WoopraEvent withProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    /**
     *
     * @param timestamp
     * @return
     */
    public WoopraEvent withTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * Return the event properties as a JSONObject
     *
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.name.concat(": ").concat(this.properties.toString());
    }
}
