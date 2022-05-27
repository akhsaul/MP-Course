package ikhsan.maulana.tugas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
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
            if (Util.validate(bind.txtNim, bind.txtName, bind.txtAddress, bind.txtHobby)) {
                var progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Menambahkan Data...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                sendData(
                        progressDialog,
                        Util.toStr(bind.txtNim),
                        Util.toStr(bind.txtName),
                        Util.toStr(bind.txtAddress),
                        Util.toStr(bind.txtHobby)
                );
            } else {
                Util.show(this, "Periksa kembali data yang anda masukkan !");
            }
        });
    }

    private void notifyError(@NonNull AlertDialog.Builder dialog) {
        dialog.setPositiveButton("Kembali", (d, w) -> {
            setResult(RESULT_CANCELED);
            this.finish();
        }).show();
    }

    private void sendData(@NonNull ProgressDialog progressDialog, @NonNull String... values) {
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
            progressDialog.dismiss();
            notifyError(dialog);
        }

        Connector.getInstance().apiAdd(obj).getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                try {
                    status[0] = jsonObject.getBoolean("status");
                    message[0] = Util.notNull(jsonObject.getString("msg"));
                } catch (Exception e) {
                    Log.e(TAG, "Error on JsonObjet", e);
                } finally {
                    dialog.setPositiveButton("Kembali", (d, w) -> {
                        if (status[0]) {
                            setResult(RESULT_OK);
                        } else {
                            setResult(RESULT_CANCELED);
                        }
                        AddActivity.this.finish();
                    }).setMessage(message[0]).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                Log.e(TAG, "Error in AndroidNetworking", anError.getCause());
                progressDialog.dismiss();
                notifyError(dialog);
            }
        });
    }
}