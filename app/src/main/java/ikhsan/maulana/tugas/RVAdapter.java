package ikhsan.maulana.tugas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.util.ArrayList;

import ikhsan.maulana.tugas.core.Connector;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.Holder> {
    private final Context context;
    private final ArrayList<ArrayList<String>> arrayData;

    public RVAdapter(@NonNull Context context, @NonNull ArrayList<ArrayList<String>> data) {
        this.context = context;
        this.arrayData = data;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.template_rv, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        var data = arrayData.get(position);
        holder.nim.setText(data.get(0));
        holder.nama.setText(data.get(1));
        holder.alamat.setText(data.get(2));
        holder.hobi.setText(data.get(3));
        holder.cvMain.setOnClickListener(v -> {
            var i = new Intent(context, UpdateActivity.class)
                    .putExtra("nim",data.get(0))
                    .putExtra("nama", data.get(1))
                    .putExtra("alamat", data.get(2))
                    .putExtra("hobi", data.get(3));
            ((ReadActivity) context).startActivityForResult(i, 1);
        });

        holder.cvMain.setOnLongClickListener(v -> {
            final String[] message = {"Data gagal di hapus!"};
            String nim = data.get(0);
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setMessage("Ingin menghapus Data dengan NIM " + nim + " ?")
                    .setCancelable(false);

            builder.setPositiveButton("Ya", (dialog, w) -> {
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Menghapus...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                Connector.getInstance().apiDelete(nim).getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            boolean status = response.getBoolean("status");
                            message[0] = response.getString("msg");
                            if (status && context instanceof ReadActivity) {
                                Util.show(context, message[0]);
                                ((ReadActivity) context).refresh();
                            } else {
                                message[0] = "Context is not instance of " + ReadActivity.class;
                                dialog.cancel();
                                Util.show(context, message[0]);
                            }
                        } catch (Exception e) {
                            Log.e("DELETE", "Error in onResponse.", e.getCause());
                            dialog.cancel();
                            Util.show(context, message[0]);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("DELETE", "Error in onError.", anError.getCause());
                        progressDialog.dismiss();
                        try {
                            JSONObject json = new JSONObject(anError.getErrorBody());
                            message[0] = json.getString("msg");
                        } catch (Exception e) {
                            Log.w("DELETE", "Ignored, Error when deserialize JSON", e.getCause());
                        }
                        dialog.cancel();
                        Util.show(context, message[0]);
                    }
                });
            });
            builder.setNegativeButton("Tidak", (dialog, w) -> {
                dialog.cancel();
                Util.show(context, "Canceled");
            });
            builder.show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return arrayData.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        public TextView nim, nama, alamat, hobi;
        public CardView cvMain;

        public Holder(@NonNull View v) {
            super(v);
            cvMain = v.findViewById(R.id.cvMain);
            nim = v.findViewById(R.id.tv_nim);
            nama = v.findViewById(R.id.tv_name);
            alamat = v.findViewById(R.id.tv_address);
            hobi = v.findViewById(R.id.tv_hobby);
        }
    }
}
