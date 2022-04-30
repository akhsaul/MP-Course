package ikhsan.maulana.tugas;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import ikhsan.maulana.tugas.databinding.ActivityCheckLocationBinding;

public final class CheckLocationActivity extends AppCompatActivity {
    public final static String TAG = CheckLocationActivity.class.getSimpleName();
    private AppService service;
    private TextView showLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityCheckLocationBinding.inflate(getLayoutInflater());
        service = new AppService(this);
        showLoc = bind.txtLoc;
        bind.btnGetLoc.setOnClickListener(v -> Util.getLocation(service,  showLoc));
        setContentView(bind.getRoot());
    }

    @SuppressWarnings("All")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            var grants = Arrays.stream(grantResults);
            Log.i(TAG, "Permission " + Arrays.deepToString(permissions)
                    + " is " + Arrays.toString(grantResults));
            if (grants.anyMatch(x -> x == PackageManager.PERMISSION_DENIED)) {
                Util.turnOnGps(this);
            } else {
                Util.getLocation(service, showLoc);
            }
        }
    }
}