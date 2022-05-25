package ikhsan.maulana.tugas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import ikhsan.maulana.tugas.core.Connector;
import ikhsan.maulana.tugas.databinding.ActivityAddBinding;

public class AddActivity extends AppCompatActivity {
    public static final String TAG = AddActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        bind.submit.setOnClickListener(v -> {
            var progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Menambahkan Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Handler().post(() -> {
                if (Util.validate(bind.txtNim, bind.txtName, bind.txtAddress, bind.txtHobby)) {
                    sendData(
                            progressDialog,
                            Util.toStr(bind.txtNim),
                            Util.toStr(bind.txtName),
                            Util.toStr(bind.txtAddress),
                            Util.toStr(bind.txtHobby)
                    );
                } else {
                    progressDialog.dismiss();
                    Util.show(this, "Periksa kembali data yang anda masukkan !");
                }
            });
        });
    }

    private void sendData(@NonNull ProgressDialog progressDialog, @NonNull String... values) {
        if (values.length == 0 || values.length > 4) {
            Log.e(TAG, "values has length = " + values.length);
        } else {
            try {
                Connector.getInstance()
                        .apiAdd(new JSONObject()
                                .put("nim", values[0])
                                .put("nama", values[1])
                                .put("alamat", values[2])
                                .put("hobi", values[3])
                        )
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                progressDialog.dismiss();
                                try {
                                    var status = jsonObject.getBoolean("status");
                                    var message = jsonObject.getString("msg");
                                    Util.show(AddActivity.this, message);
                                    new AlertDialog.Builder(AddActivity.this)
                                            .setCancelable(false)
                                            .setMessage(message)
                                            .setPositiveButton("Kembali", (dialog, which) -> {
                                                var i = getIntent();
                                                if (status) {
                                                    setResult(RESULT_OK, i);
                                                } else {
                                                    setResult(RESULT_CANCELED, i);
                                                }
                                                AddActivity.this.finish();
                                            }).show();
                                } catch (JSONException e) {
                                    Log.e(TAG, "Error on JsonObjet", e);
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "Error in AndroidNetworking", anError.getCause());
                            }
                        });
            } catch (Throwable t) {
                Log.e(TAG, "Error happened.", t);
            }
        }
    }
}