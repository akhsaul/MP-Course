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
        bind.btnCalc.setOnClickListener(v -> {
            var destination = new Intent(this, CalculatorActivity.class);
            startActivity(destination);
        });
        setContentView(bind.getRoot());
    }
}