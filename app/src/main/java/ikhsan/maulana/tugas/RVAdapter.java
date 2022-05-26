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

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.util.ArrayList;

import ikhsan.maulana.tugas.core.Connector;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.Holder> {
    private static final String TAG = RVAdapter.class.getSimpleName();
    private final Context context;
    private ProgressDialog progressDialog;
    private final ArrayList<ArrayList<String>> arrayData;

    public RVAdapter(@NonNull Context context, ArrayList<ArrayList<String>> data) {
        this.context = context;
        this.arrayData = data;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RVAdapter.Holder(LayoutInflater
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
            Util.maintenance(context);
        });
        holder.cvMain.setOnLongClickListener(v -> {
            Util.maintenance(context);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return arrayData.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public TextView nim, nama, alamat, hobi;
        public CardView cvMain;

        public Holder(@NonNull View v) {
            super(v);
            cvMain = v.findViewById(R.id.cvMain);
            nim = v.findViewById(R.id.tv_nim);
            nama = v.findViewById(R.id.tv_name);
            alamat = v.findViewById(R.id.tv_address);
            hobi = v.findViewById(R.id.tv_hobby);
            progressDialog = new ProgressDialog(context);
        }
    }
}
