package ikhsan.maulana.tugas;

import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import java.util.Arrays;

import ikhsan.maulana.tugas.databinding.ActivityUpdateDataBinding;

public final class UpdateDataActivity extends AppCompatActivity {
    public final static String TAG = UpdateDataActivity.class.getSimpleName();
    private AppService service;
    private EditText address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityUpdateDataBinding.inflate(getLayoutInflater());
        var helper = new DBHelper(this);
        try(var db = helper.getReadableDatabase();
            var cursor = db.rawQuery(DBHelper.SELECT +
                            " where no = " +
                            (char) 39 + getIntent().getExtras().getString("no") + (char) 39,
                    null)) {
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                bind.inNumber.setText(cursor.getString(0));
                bind.inName.setText(cursor.getString(1));
                bind.inNoHp.setText(cursor.getString(2));
                bind.inJK.setText(cursor.getString(3));
                bind.inAddress.setText(cursor.getString(4));
            }
        }
        service = new AppService(this, this);
        address = bind.inAddress;
        bind.btnUpdate.setOnClickListener(v -> Util.updateData(this, helper, bind.inNumber,
                bind.inName, bind.inNoHp, bind.inJK, bind.inAddress));
        bind.btnBack.setOnClickListener(v -> finish());
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
                            .append(location.getLatitude())
                            .append(" , ")
                            .append(location.getLongitude());

                    address.setText(build.toString());
                }
            });
        } catch (IllegalStateException e) {
            Log.i(TAG, "Message is " + e.getMessage());
            Util.show(this, "Unable to find location.");
        }
    }
}