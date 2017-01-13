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

    private String testProject = "ralphsamuel.io";

    public static void main(String[] args) {
        Test t = new Test();
        t.testConfigOptions();
        t.testEventProps();
        t.testIdentify();

    }

    private void testConfigOptions() {
        WoopraTracker tracker = WoopraTracker.getInstance(this.testProject)
                .withIdleTimeout(500);

        WoopraVisitor visitor = new WoopraVisitor("email", this.generateUseremail());

        WoopraEvent event = new WoopraEvent("java_sdk_test")
                .withTimestamp(1484334934086L); //friday jan 13 2017 about 11:30 am PST

        System.out.println("Tracking Event: ".concat(event.toString()));
        tracker.track(visitor, event);
    }

    private void testEventProps() {
        WoopraTracker tracker = WoopraTracker.getInstance(this.testProject);

        WoopraEvent event = new WoopraEvent("java_sdk_test_with_props");
        WoopraVisitor visitor = new WoopraVisitor("email", this.generateUseremail());

        event.withProperty("test_event_prop_1", "test_event_prop_value_1");

        System.out.println("Tracking Event: ".concat(event.toString()));
        tracker.track(visitor, event);
    }

    private void testIdentify() {
        WoopraTracker tracker = WoopraTracker.getInstance(this.testProject);

        WoopraVisitor visitor = new WoopraVisitor("email", this.generateUseremail())
                .withProperty("test_vis_prop_1", "test_vis_prop_val_".concat(String.valueOf((int) (100 * Math.random()))));

        System.out.println("Sending Identify(): ".concat(visitor.toString()));
        tracker.identify(visitor);
    }

    private String generateUseremail() {
        return "ralph".concat(String.valueOf((int) (10 * Math.random()))).concat(".java.sdk@woopratest.com");
    }
}
