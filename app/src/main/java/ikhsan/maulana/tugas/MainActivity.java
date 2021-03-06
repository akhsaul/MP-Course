package ikhsan.maulana.tugas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ikhsan.maulana.tugas.core.Connector;
import ikhsan.maulana.tugas.databinding.ActivityMainBinding;

public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityMainBinding.inflate(getLayoutInflater());
        bind.btnCalc.setOnClickListener(v -> Util.move(this, CalculatorActivity.class));
        bind.btnAddData.setOnClickListener(v -> Util.move(this, AddDataActivity.class));
        bind.btnViewData.setOnClickListener(v -> Util.move(this, ViewDataActivity.class));
        bind.btnLocation.setOnClickListener(v -> Util.move(this, CheckLocationActivity.class));
        bind.btnProximity.setOnClickListener(v -> Util.move(this, ProximityActivity.class));
        bind.btnAdd.setOnClickListener(v -> Util.move(this, AddActivity.class));
        bind.btnRead.setOnClickListener(v -> Util.move(this, ReadActivity.class));
        setContentView(bind.getRoot());
        Connector.getInstance().initialize(this);
    }
}