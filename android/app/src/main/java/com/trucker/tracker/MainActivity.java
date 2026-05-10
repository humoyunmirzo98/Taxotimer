package com.trucker.tracker;

import android.content.Intent;
import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Register the JS bridge plugin before the Capacitor bridge initialises.
        registerPlugin(TruckerBridgePlugin.class);
        super.onCreate(savedInstanceState);

        // Auto-start GPS foreground service when the app opens.
        startForegroundService(new Intent(this, LocationForegroundService.class));
    }
}
