package ikhsan.maulana.tugas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Locale;

import ikhsan.maulana.tugas.databinding.ActivityCheckLocationBinding;

public class CheckLocationActivity extends AppCompatActivity {
    public final static String TAG = CheckLocationActivity.class.getSimpleName();
    private static final int REQUEST_LOCATION = 1;
    private LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION
        );
        var bind = ActivityCheckLocationBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        bind.btnGetLoc.setOnClickListener(v -> {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getLocation(bind.txtLoc);
            } else {
                turnOnGps();
            }
        });
    }

    private void turnOnGps() {
        new AlertDialog.Builder(this)
                .setMessage("Enable GPS")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, which) -> Util.move(this,
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        )
                )
                .setNegativeButton("No",
                        (dialog, which) -> dialog.cancel()
                )
                .create().show();
    }

    private void getLocation(TextView showLoc) {
        if (Util.checkPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            var locationGPS = manager.getLastKnownLocation(
                    LocationManager.GPS_PROVIDER
            );
            Log.d(TAG, "locationGPS = " + locationGPS);
            if (locationGPS != null) {
               setLocation(showLoc, locationGPS);
            } else {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, location -> {
                    Log.d(TAG, "location = " + location);
                    if (location == null){
                        Util.show(this, "Unable to find location.");
                    } else {
                        setLocation(showLoc, location);
                    }
                });
            }
        } else {
            turnOnGps();
        }
    }

    private void setLocation(@NonNull TextView showLoc, @NonNull Location gps){
        showLoc.setText(String.format(
                Locale.getDefault(),
                "Your Location: %nLatitude: %f%nLongitude: %f",
                gps.getLatitude(),
                gps.getLongitude()
        ));
    }
}