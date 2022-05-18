package ikhsan.maulana.tugas;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.Sensor.TYPE_ALL;
import static android.hardware.Sensor.TYPE_PROXIMITY;
import static android.location.Criteria.ACCURACY_FINE;
import static android.location.Criteria.ACCURACY_HIGH;
import static android.location.LocationManager.GPS_PROVIDER;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AppService implements LocationListener {
    private final static String TAG = AppService.class.getSimpleName();
    private final static Handler handler = new Handler();
    private final static CheckedSet<String> providers = new CheckedSet<>();
    private final static CheckedSet<Sensor> sensors = new CheckedSet<>();
    private static boolean onProgress = false;
    private final Activity act;
    @Nullable
    private LocationManager locationManager = null;
    private AppServiceListener listener = (message, exception) -> {
        throw new RuntimeException(message, exception);
    };
    @Nullable
    private SensorManager sensorManager = null;
    @Nullable
    private LocationListener locationListener = null;
    @Nullable
    private SensorListener sensorListener = null;

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
        Log.d(TAG, "LocationManager is " + locationManager);
        if (locationManager == null) {
            Log.i(TAG, "Request Location Permission");
            ActivityCompat.requestPermissions(act, new String[]{ACCESS_FINE_LOCATION}, 1);
            if (Util.checkPermission(act, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET)) {
                locationManager = (LocationManager) act.getSystemService(LOCATION_SERVICE);
            }
        }
    }

    private synchronized void sensorPermission() {
        if (sensorManager == null) {
            sensorManager = (SensorManager) act.getSystemService(SENSOR_SERVICE);
        }
    }

    @NonNull
    private <T> T notNull(@Nullable T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public AppService setListener(@NonNull AppServiceListener listener) {
        this.listener = listener;
        return this;
    }

    public AppService setLocationListener(@NonNull LocationListener listener) {
        locationListener = listener;
        return this;
    }

    public AppService setSensorListener(@NonNull SensorListener listener) {
        sensorListener = listener;
        return this;
    }

    private AppService requestSensor(int type) {
        sensorPermission();
        if (sensorManager != null) {
            try {
                sensors.addAll(type == TYPE_ALL
                        ? sensorManager.getSensorList(TYPE_ALL)
                        : Collections.singletonList(sensorManager.getDefaultSensor(type))
                );
            } catch (NullPointerException npe) {
                listener.onError("Sensor " + type + " is NULL." +
                                " Check permission or does not supported",
                        npe
                );
            }
        } else {
            Log.w(TAG, "Sensor Manager is NULL. Can't request sensor!");
        }
        return this;
    }

    public AppService requestProximity() {
        return requestSensor(TYPE_PROXIMITY);
    }

    public AppService requestAllSensor() {
        return requestSensor(TYPE_ALL);
    }

    private void requestLocation(@NonNull LocationManager manager) {
        var list = manager.getProviders(true);
        if (!list.isEmpty() && !list.contains(null)) {
            var provider = GPS_PROVIDER;
            if (list.contains(provider)) {
                providers.add(provider);

                var criteria = new Criteria();
                criteria.setAccuracy(ACCURACY_FINE);
                criteria.setSpeedAccuracy(ACCURACY_HIGH);
                provider = manager.getBestProvider(criteria, true);
                providers.add(provider, true);
            } else {
                providers.addAll(list);
            }
        } else {
            listener.onError("No Provider Enabled.", new IllegalStateException());
        }
    }

    public AppService requestLocation() {
        locationPermission();
        if (locationManager != null) {
            requestLocation(locationManager);
        } else {
            Log.w(TAG, "Location Manager is NULL. Can't request location!");
        }
        return this;
    }

    @SuppressWarnings("MissingPermission")
    public void start() {
        if (!providers.isEmpty() && locationManager != null) {
            if (!onProgress) {
                var queued = handler.post(() -> {
                    for (var provider : providers) {
                        onProgress = true;
                        Log.i(TAG, "request location started using " + provider);
                        locationManager.requestLocationUpdates(provider,
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

        if (!sensors.isEmpty() && sensorManager != null) {
            var queued = handler.post(() -> {
                for (Sensor sensor : sensors) {
                    sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
                }
            });

            if (queued) {
                Log.i(TAG, "request sensor in queued");
            } else {
                Log.i(TAG, "request sensor NOT in queued");
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // call real listener
        notNull(locationListener).onLocationChanged(location);
        // get coder
        var coder = locationListener.getCoder(act);
        List<Address> result = new ArrayList<>();
        try {
            result = coder.getFromLocation(
                    location.getLatitude(), location.getLongitude(),
                    5
            );
        } catch (IOException io) {
            var msg = "Error when trying to decode location = " + location;
            Log.w(TAG, msg, new DecoderException(io.getMessage(), io));
            listener.onError(msg, new DecoderException(io.getMessage(), io));
        } finally {
            locationListener.onLocationDecoded(result.isEmpty() ? null : result.get(0));
            locationListener.onLocationDecoded(result);
        }
        onProgress = false;
        Log.i(TAG, "request location finished");
    }
}
