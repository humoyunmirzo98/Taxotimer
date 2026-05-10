package com.trucker.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Restarts LocationForegroundService after device reboot.
 * Requires RECEIVE_BOOT_COMPLETED permission in the manifest.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, LocationForegroundService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
