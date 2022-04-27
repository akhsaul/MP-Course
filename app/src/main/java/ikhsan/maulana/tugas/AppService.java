package ikhsan.maulana.tugas;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public final class AppService implements LocationListener {
    private final static String TAG = AppService.class.getSimpleName();
    private final static Handler handler = new Handler();
    private boolean onProgress = false;
    private LocationManager manager = null;
    private LocationListener listener;
    private final Context ctx;
    private final Activity act;

    public AppService(@NonNull Context context, Activity activity) {
        ctx = context;
        act = activity;
    }

    private void locationPermission() {
        Log.i(TAG, "LocationManager is " + manager);
        if (manager == null) {
            Log.i(TAG, "Request Location Permission");
            ActivityCompat.requestPermissions(act, new String[]{ACCESS_FINE_LOCATION}, 1);
            if (Util.checkPermission(ctx, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET)) {
                manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        listener.onLocationChanged(location);
        onProgress = false;
        Log.i(TAG, "request location finished");
    }

    @SuppressWarnings("MissingPermission")
    public void getLocation(@NonNull LocationListener listener) {
        locationPermission();
        this.listener = listener;
        if (manager != null) {
            if (!this.onProgress) {
                var queued = false;
                if (manager.isProviderEnabled(GPS_PROVIDER)) {
                    queued = handler.post(() -> {
                        this.onProgress = true;
                        Log.i(TAG, "request location started using GPS");
                        manager.requestLocationUpdates(GPS_PROVIDER,
                                1000 * 60 * 2, 10, this);
                    });
                } else if (manager.isProviderEnabled(NETWORK_PROVIDER)) {
                    queued = handler.post(() -> {
                        this.onProgress = true;
                        Log.i(TAG, "request location started using NETWORK");
                        manager.requestLocationUpdates(NETWORK_PROVIDER,
                                1000 * 60 * 2, 10, this);
                    });
                } else {
                    throw new IllegalStateException("No Provider Enabled.");
                }
                if (queued) {
                    Log.i(TAG, "request location in queued");
                } else {
                    Log.i(TAG, "request location NOT in queued");
                }
            } else {
                Log.i(TAG, "request location onProgress. NEW REQUEST WILL BE IGNORED!");
            }
        } else {
            Log.w(TAG, "Location Manager is NULL. Can't Request Location!");
        }
    }
}
