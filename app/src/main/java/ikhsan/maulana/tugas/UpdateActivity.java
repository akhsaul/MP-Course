package ikhsan.maulana.tugas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import ikhsan.maulana.tugas.core.Connector;
import ikhsan.maulana.tugas.databinding.ActivityUpdateBinding;

public class UpdateActivity extends AppCompatActivity {
    public static final String TAG = UpdateActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        getDataIntent(bind.txtNim, bind.txtName, bind.txtAddress, bind.txtHobby);
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

    @NonNull
    private String getString(@Nullable Bundle bundle, @NonNull String key) {
        var data = bundle != null ? bundle.getString(key) : "";
        return data != null ? data : "";
    }

    private void getDataIntent(@NonNull EditText... texts) {
        var bundle = getIntent().getExtras();
        texts[0].setText(getString(bundle, "nim"));
        texts[1].setText(getString(bundle, "nama"));
        texts[2].setText(getString(bundle, "alamat"));
        texts[3].setText(getString(bundle, "hobi"));
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
        var dialog = new AlertDialog.Builder(this)
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

        Connector.getInstance().apiUpdate(values[0], obj).getAsJSONObject(
                new JSONObjectRequestListener() {
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
                                UpdateActivity.this.finish();
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