package ikhsan.maulana.tugas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ikhsan.maulana.tugas.databinding.ActivityAddDataBinding;

public class AddDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityAddDataBinding.inflate(getLayoutInflater());
        bind.btnSave.setOnClickListener(v -> Util.maintenance(this));
        bind.btnBack.setOnClickListener(v -> finish());
        setContentView(bind.getRoot());
    }
}