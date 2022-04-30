package ikhsan.maulana.tugas;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import ikhsan.maulana.tugas.databinding.ActivityAddDataBinding;

public final class AddDataActivity extends AppCompatActivity {
    public final static String TAG = AddDataActivity.class.getSimpleName();
    private DBHelper helper;
    private AppService service;
    private EditText address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityAddDataBinding.inflate(getLayoutInflater());
        helper = new DBHelper(this);
        service = new AppService(this);
        address = bind.inAddress;
        bind.btnSave.setOnClickListener(v -> Util.insertData(this, helper,
                bind.inName, bind.inNoHp, bind.inJK, bind.inAddress));
        bind.btnBack.setOnClickListener(v -> finish());
        bind.btnGetLoc.setOnClickListener(v -> Util.getLocation(service, address));
        setContentView(bind.getRoot());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            Log.i(TAG, "Permission " + Arrays.deepToString(permissions)
                    + " is " + Arrays.toString(grantResults));
            if(Arrays.binarySearch(grantResults, PERMISSION_DENIED) > 0){
                Util.turnOnGps(this);
            } else {
                Util.getLocation(service, address);
            }
        }
    }
}