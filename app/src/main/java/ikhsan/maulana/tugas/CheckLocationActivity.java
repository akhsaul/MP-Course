package ikhsan.maulana.tugas;

import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import ikhsan.maulana.tugas.databinding.ActivityCheckLocationBinding;

public class CheckLocationActivity extends AppCompatActivity {
    public final static String TAG = CheckLocationActivity.class.getSimpleName();
    private AppService service;
    private TextView showLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityCheckLocationBinding.inflate(getLayoutInflater());
        service = new AppService(this, this);
        showLoc = bind.txtLoc;
        bind.btnGetLoc.setOnClickListener(v -> getLocation());
        setContentView(bind.getRoot());
    }

    @SuppressWarnings("All")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            var grants = Arrays.stream(grantResults);
            Log.i(TAG, "Permission " + Arrays.deepToString(permissions) + " is " + Arrays.toString(grantResults));
            if (grants.anyMatch(x -> x == PackageManager.PERMISSION_DENIED)) {
                turnOnGps();
            } else {
                getLocation();
            }
        }
    }

    private void turnOnGps() {
        new AlertDialog.Builder(this)
                .setMessage("Enable GPS").setCancelable(false)
                .setPositiveButton("Yes", (d, w) -> Util.move(this,
                        ACTION_LOCATION_SOURCE_SETTINGS
                ))
                .setNegativeButton("No", (dialog, which) -> {
                    Util.show(this, "Unable to find location.");
                    dialog.cancel();
                })
                .create().show();
    }

    private void getLocation() {
        try {
            service.getLocation(location -> {
                Log.d(TAG, "location from Service = " + location);
                if (location == null) {
                    Util.show(this, "Unable to find location.");
                } else {

                    var build = new StringBuilder()
                            .append("Your Location:")
                            .append('\n')
                            .append("Latitude: ")
                            .append(location.getLatitude())
                            .append('\n')
                            .append("Longitude: ")
                            .append(location.getLongitude());
                    showLoc.setText(build.toString());
                }
            });
        } catch (IllegalStateException e) {
            Log.i(TAG, "Message is " + e.getMessage());
            Util.show(this, "Unable to find location.");
        }
    }
}