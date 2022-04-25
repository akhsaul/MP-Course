package ikhsan.maulana.tugas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ikhsan.maulana.tugas.databinding.ActivityDetailsDataBinding;

public final class DetailsDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityDetailsDataBinding.inflate(getLayoutInflater());
        var helper = new DBHelper(this);
        try(var db = helper.getReadableDatabase();
            var cursor = db.rawQuery(DBHelper.SELECT +
                            " where no = " +
                            (char) 39 + getIntent().getExtras().getString("no") + (char) 39,
                    null)) {
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                bind.inNumber.setText(cursor.getString(0));
                bind.inName.setText(cursor.getString(1));
                bind.inNoHp.setText(cursor.getString(2));
                bind.inJK.setText(cursor.getString(3));
                bind.inAddress.setText(cursor.getString(4));
            }
        }
        bind.btnBack.setOnClickListener(v -> finish());
        setContentView(bind.getRoot());
    }
}