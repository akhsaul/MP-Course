package ikhsan.maulana.tugas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ikhsan.maulana.tugas.databinding.ActivityViewDataBinding;

public final class ViewDataActivity extends AppCompatActivity {
    public final static String TAG = ViewDataActivity.class.getSimpleName();
    private final DBHelper helper;
    private String[] ids;
    private final static CharSequence[] dialogItem = {
            "Lihat Biodata",
            "Update Biodata",
            "Hapus Biodata"
    };

    public ViewDataActivity() {
        helper = new DBHelper(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityViewDataBinding.inflate(getLayoutInflater());
        bind.btnAdd.setOnClickListener(v -> Util.move(this, AddDataActivity.class));
        bind.btnBack.setOnClickListener(v -> finish());
        setContentView(bind.getRoot());
        refreshList(bind.listItem);
    }

    private void refreshList(ListView list) {
        String[] names;
        try (var db = helper.getReadableDatabase();
             var cursor = db.rawQuery(DBHelper.SELECT, null)) {
            names = new String[cursor.getCount()];
            ids = new String[cursor.getCount()];
            for (int i = 0; i < names.length; i++) {
                cursor.moveToPosition(i);
                ids[i] = cursor.getString(0);
                names[i] = cursor.getString(1);
            }
        }
        list.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names)
        );
        list.setSelected(true);
        list.setOnItemClickListener((parent, view, position, id) -> new AlertDialog.Builder(this).setTitle("Pilihan")
                .setItems(dialogItem, (dialog, which) -> {
                    switch (which) {
                        case 0:
                        case 1:
                            Util.maintenance(this);
                            break;
                        case 2:
                            var data = new Bundle();
                            var selection = ids[position];
                            Log.d(TAG, "no selection " + selection);
                            data.putString("no", selection);

                            Util.deleteData(this, helper, selection);
                            refreshList(list);
                            break;
                    }
                }).create().show());
        ((ArrayAdapter<?>) list.getAdapter()).notifyDataSetChanged();
    }
}