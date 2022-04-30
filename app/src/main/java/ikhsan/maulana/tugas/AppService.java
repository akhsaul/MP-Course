package ikhsan.maulana.tugas;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.content.Context.LOCATION_SERVICE;
import static android.location.Criteria.ACCURACY_FINE;
import static android.location.Criteria.ACCURACY_HIGH;

import android.app.Activity;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class AppService implements LocationListener {
    private final static String TAG = AppService.class.getSimpleName();
    private final static Handler handler = new Handler();
    private final static Set<String> providers = new LinkedHashSet<>();
    private static boolean onProgress = false;
    private final Activity act;
    @Nullable
    private LocationManager locationManager = null;
    @Nullable
    private LocationListener locationListener = null;

    public AppService(@NonNull Activity activity) {
        act = activity;
    }

    @NonNull
    public Activity getActivity() {
        return act;
    }

    @NonNull
    public String getTag() {
        return act.getClass().getSimpleName();
    }

    private synchronized void locationPermission() {
        Log.i(TAG, "LocationManager is " + locationManager);
        if (locationManager == null) {
            Log.i(TAG, "Request Location Permission");
            ActivityCompat.requestPermissions(act, new String[]{ACCESS_FINE_LOCATION}, 1);
            if (Util.checkPermission(act, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET)) {
                locationManager = (LocationManager) act.getSystemService(LOCATION_SERVICE);
            }
        }
    }

    private void requestLocationUpdates(@NonNull LocationManager manager) {
        var list = manager.getProviders(true);
        if (!list.isEmpty()) {
            var provider = GPS_PROVIDER;
            if (list.contains(provider)) {
                providers.add(provider);

                var criteria = new Criteria();
                criteria.setAccuracy(ACCURACY_FINE);
                criteria.setSpeedAccuracy(ACCURACY_HIGH);
                provider = manager.getBestProvider(criteria, true);
                if (provider != null) {
                    providers.add(provider);
                }
            } else {
                providers.addAll(list);
            }
        } else {
            throw new IllegalStateException("No Provider Enabled.");
        }
    }

    @NonNull
    private <T> T notNull(@Nullable T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }


    public AppService setLocationListener(@NonNull LocationListener listener) {
        locationListener = listener;
        return this;
    }

    public AppService requestLocation() {
        locationPermission();
        if (locationManager != null) {
            requestLocationUpdates(locationManager);
        } else {
            Log.w(TAG, "Location Manager is NULL. Can't request location!");
        }
        return this;
    }

    @SuppressWarnings("MissingPermission")
    public void start() {

        if (!providers.isEmpty()) {
            if (!onProgress) {
                var queued = handler.post(() -> {
                    for (String provider : providers) {
                        onProgress = true;
                        Log.i(TAG, "request location started using " + provider);
                        notNull(locationManager).requestLocationUpdates(provider,
                                1000 * 60 * 2, 10,
                                this
                        );
                    }
                });

                if (queued) {
                    Log.i(TAG, "request location in queued");
                } else {
                    Log.i(TAG, "request location NOT in queued");
                }
            } else {
                Log.i(TAG, "request location onProgress. NEW REQUEST WILL BE IGNORED!");
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // call real listener
        notNull(locationListener).onLocationChanged(location);
        // get coder
        var coder = locationListener.getCoder(act);
        List<Address> result = new LinkedList<>();
        try {
            result = coder.getFromLocation(
                    location.getLatitude(), location.getLongitude(),
                    5
            );
        } catch (Throwable t) {
            Log.w(TAG, "Error when trying to decode location.", t);
        }

        locationListener.onLocationDecoded(result.isEmpty() ? null : result.get(0));
        locationListener.onLocationDecoded(result);
        onProgress = false;
        Log.i(TAG, "request location finished");
    }
}
