package ikhsan.maulana.tugas;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
        service = new AppService(this);
        try (var db = helper.getReadableDatabase();
             var cursor = db.rawQuery(DBHelper.SELECT +
                             " where no = " +
                             (char) 39 + getIntent().getExtras().getString("no") + (char) 39,
                     null)) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                bind.inNumber.setText(cursor.getString(0));
                bind.inName.setText(cursor.getString(1));
                bind.inNoHp.setText(cursor.getString(2));
                bind.inJK.setText(cursor.getString(3));
                bind.inAddress.setText(cursor.getString(4));
            }
        }
        address = bind.inAddress;
        bind.btnUpdate.setOnClickListener(v -> Util.updateData(this, helper, bind.inNumber,
                bind.inName, bind.inNoHp, bind.inJK, bind.inAddress));
        bind.btnBack.setOnClickListener(v -> finish());
        bind.btnGetLoc.setOnClickListener(v -> Util.getLocation(service, address));
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
                Util.turnOnGps(this);
            } else {
                Util.getLocation(service, address);
            }
        }
    }
}