package com.trucker.tracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.BridgeActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BridgeActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Register the JS bridge plugin before the Capacitor bridge initialises.
        registerPlugin(TruckerBridgePlugin.class);
        super.onCreate(savedInstanceState);

        // Keep screen on while the app is in the foreground.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        requestAllPermissions();
        requestBatteryOptimizationExemption();

        // Auto-start GPS foreground service when the app opens.
        startForegroundService(new Intent(this, LocationForegroundService.class));
    }

    /** Builds the full list of runtime permissions needed and requests any that are not yet granted. */
    private void requestAllPermissions() {
        List<String> needed = new ArrayList<>();

        // Location — always required
        add(needed, Manifest.permission.ACCESS_FINE_LOCATION);
        add(needed, Manifest.permission.ACCESS_COARSE_LOCATION);

        // Background location must be requested separately after foreground location is granted.
        // We include it here; Android will silently ignore it if foreground is not yet granted.
        add(needed, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        // Camera
        add(needed, Manifest.permission.CAMERA);

        // Storage — split by API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {   // API 33+
            add(needed, Manifest.permission.READ_MEDIA_IMAGES);
            add(needed, Manifest.permission.READ_MEDIA_VIDEO);
            add(needed, Manifest.permission.READ_MEDIA_AUDIO);
            add(needed, Manifest.permission.POST_NOTIFICATIONS);
        } else {
            add(needed, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {       // below API 29
                add(needed, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

        if (!needed.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    needed.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    /** Adds a permission to the list only if it has not already been granted. */
    private void add(List<String> list, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            list.add(permission);
        }
    }

    /**
     * Asks the system to exempt this app from battery optimisation so the
     * GPS foreground service is not killed when the screen is off.
     * The user sees a system dialog; if they decline, the service may be
     * throttled on some OEM ROMs.
     */
    private void requestBatteryOptimizationExemption() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }
}
