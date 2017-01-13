package com.woopra.java.sdk;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rsamuel
 */
public class Test {
    public static void main(String[] args) {
        Test t = new Test();
        t.runEventTest();
        
    }
    
    private void runEventTest() {
        WoopraTracker tracker =  WoopraTracker.getInstance("ralphsamuel.io");
      
        WoopraEvent event = new WoopraEvent("java_sdk_test");
        WoopraVisitor visitor = new WoopraVisitor("email", "ralph.java.sdk@woopratest.com");
        
        event.withTimestamp(1484334934086L)
                .withProperty("test_prop_1", "test_prop_value");
        
        
        tracker.track(visitor, event);
        System.out.println("Tracking Event: ".concat(event.toString()));
    }
}
