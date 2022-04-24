package ikhsan.maulana.tugas;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ikhsan.maulana.tugas.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityMainBinding.inflate(getLayoutInflater());
        bind.btnCalc.setOnClickListener(v -> Util.move(this, CalculatorActivity.class));
        bind.btnAddData.setOnClickListener(v -> Util.move(this, AddDataActivity.class));
        bind.btnViewData.setOnClickListener(v -> Util.maintenance(this));
        setContentView(bind.getRoot());
    }
}