package ikhsan.maulana.tugas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
    private Intent current = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        current = getIntent();
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

    private void notifyError(@NonNull AlertDialog.Builder dialog) {
        dialog.setPositiveButton("Kembali", (d, w) -> {
            setResult(RESULT_CANCELED, current);
            AddActivity.this.finish();
        }).show();
    }

    private void sendData(@NonNull ProgressDialog progressDialog, @NonNull String... values) {
        if (values.length == 0 || values.length > 4) {
            Log.e(TAG, "values has length = " + values.length);
        } else {
            final boolean[] status = {false};
            final String[] message = {"Data gagal disimpan!"};
            var dialog = new AlertDialog.Builder(AddActivity.this)
                    .setCancelable(false)
                    .setMessage(message[0]);

            JSONObject obj = null;
            try {
                obj = new JSONObject()
                        .put("nim", values[0])
                        .put("nama", values[1])
                        .put("alamat", values[2])
                        .put("hobi", values[3]);
            } catch (JSONException t) {
                Log.e(TAG, "Error happened.", t);
                notifyError(dialog);
            }

            if (obj != null) {
                Connector.getInstance()
                        .apiAdd(obj)
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                progressDialog.dismiss();
                                try {
                                    status[0] = jsonObject.getBoolean("status");
                                    message[0] = Util.notNull(jsonObject.getString("msg"));
                                } catch (Exception e) {
                                    Log.e(TAG, "Error on JsonObjet", e);
                                } finally {
                                    boolean stat = status[0];
                                    dialog.setPositiveButton("Kembali", (d, w) -> {
                                        if (stat) {
                                            setResult(RESULT_OK, current);
                                        } else {
                                            setResult(RESULT_CANCELED, current);
                                        }
                                        AddActivity.this.finish();
                                    }).show();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "Error in AndroidNetworking", anError.getCause());
                                notifyError(dialog);
                            }
                        });
            }
        }
    }
}