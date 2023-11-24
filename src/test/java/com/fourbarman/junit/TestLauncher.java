package com.fourbarman.junit;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.PrintWriter;

/**
 * Manual Test Launcher with manual config.
 */
public class TestLauncher {
    public static void main(String[] args) {
        Launcher launcher = LauncherFactory.create();
        //we can use listeners and specify them in builder
        //or launcher.registerTestExecutionListeners(listener)
        //or launcher.execute(request, listener)
        SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
                .request()
                //.selectors(DiscoverySelectors.selectClass(UserServiceTest.class)) - we can specify Test class
                .selectors(DiscoverySelectors.selectPackage("com.fourbarman.junit.service")) //or package
                //.listeners() //add Listeners
                .build();
        launcher.execute(request, summaryGeneratingListener);
        try (PrintWriter printWriter = new PrintWriter(System.out)) {
            summaryGeneratingListener.getSummary().printTo(printWriter);
        }
    }
}
