package com.trucker.tracker;

import android.content.Intent;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * Capacitor plugin that exposes GPS service control to JavaScript.
 *
 * JS usage (after the bridge shim is loaded):
 *   window.TruckerBridge.startTracking();
 *   window.TruckerBridge.stopTracking();
 *
 * The shim in www/index.html delegates to these plugin methods via
 * Capacitor.Plugins.TruckerBridge.
 */
@CapacitorPlugin(name = "TruckerBridge")
public class TruckerBridgePlugin extends Plugin {

    @PluginMethod
    public void startTracking(PluginCall call) {
        Intent intent = new Intent(getContext(), LocationForegroundService.class);
        getContext().startForegroundService(intent);
        call.resolve();
    }

    @PluginMethod
    public void stopTracking(PluginCall call) {
        Intent intent = new Intent(getContext(), LocationForegroundService.class);
        getContext().stopService(intent);
        call.resolve();
    }
}
