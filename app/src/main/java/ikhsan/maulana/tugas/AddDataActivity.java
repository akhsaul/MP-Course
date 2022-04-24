package ikhsan.maulana.tugas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ikhsan.maulana.tugas.databinding.ActivityAddDataBinding;

public class AddDataActivity extends AppCompatActivity {
    private final DBHelper helper;

    public AddDataActivity(){
        helper = new DBHelper(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityAddDataBinding.inflate(getLayoutInflater());
        bind.btnSave.setOnClickListener(v -> Util.insertDB(this, helper,
                bind.inName, bind.inNoHp, bind.inJK, bind.inAddress));
        bind.btnBack.setOnClickListener(v -> finish());
        setContentView(bind.getRoot());
    }
}