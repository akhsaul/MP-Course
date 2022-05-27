package ikhsan.maulana.tugas;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import ikhsan.maulana.tugas.core.Connector;
import ikhsan.maulana.tugas.databinding.ActivityReadBinding;

public class ReadActivity extends AppCompatActivity {
    private static final String TAG = ReadActivity.class.getSimpleName();
    private ActivityReadBinding bind;
    private ProgressDialog progressDialog;
    private ArrayList<ArrayList<String>> arrayData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityReadBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        progressDialog = new ProgressDialog(this);
        bind.rvMain.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        ));
        bind.rvMain.setHasFixedSize(true);
        bind.swipeRefresh.setOnRefreshListener(() -> {
            refresh();
            bind.swipeRefresh.setRefreshing(false);
        });
        refresh();
    }

    void refresh() {
        progressDialog.setMessage("Mengambil Data.....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        getData();
    }

    private void getData() {
        arrayData = new ArrayList<>();
        Connector.getInstance().apiRead().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Connector.getInstance().run(() -> {
                    try {
                        var status = jsonObject.getBoolean("status");
                        if (status) {
                            var array = jsonObject.getJSONArray("data");
                            var tmpArrayData = new ArrayList<ArrayList<String>>();
                            long[] arrayNim = new long[array.length()];
                            for (int i = 0; i < array.length(); i++) {
                                var obj = array.getJSONObject(i);
                                var arrayObj = new ArrayList<String>();
                                arrayObj.add(obj.getString("nim"));
                                arrayObj.add(obj.getString("nama"));
                                arrayObj.add(obj.getString("alamat"));
                                arrayObj.add(obj.getString("hobi"));
                                tmpArrayData.add(arrayObj);
                                arrayNim[i] = obj.getLong("nim");
                            }

                            Arrays.sort(arrayNim);

                            for (long nim : arrayNim) {
                                for (ArrayList<String> data : tmpArrayData) {
                                    if (nim == Long.parseLong(data.get(0))) {
                                        arrayData.add(data);
                                    }
                                }
                            }
                            tmpArrayData.clear();
                        } else {
                            Connector.getInstance().runOnMain(() -> Util.show(ReadActivity.this, "Gagal mengambil Data"));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error. " + e.getMessage(), e.getCause());
                        Connector.getInstance().runOnMain(() -> Util.show(ReadActivity.this, "Gagal mengambil Data"));
                    } finally {
                        var adapter = new RVAdapter(ReadActivity.this, arrayData);
                        Connector.getInstance().runOnMain(() -> bind.rvMain.setAdapter(adapter));
                        Connector.getInstance().runOnMain(() -> progressDialog.dismiss(), arrayData.size() * 6L);
                    }
                });
            }

            @Override
            public void onError(ANError anError) {
                Log.e(TAG, "Error. " + anError.getMessage(), anError.getCause());
                progressDialog.dismiss();
                Util.show(ReadActivity.this, "Gagal mengambil Data");
                bind.rvMain.setAdapter(new RVAdapter(ReadActivity.this, arrayData));
            }
        });
    }
}