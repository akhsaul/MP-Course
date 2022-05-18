package ikhsan.maulana.tugas;

import static android.hardware.Sensor.TYPE_PROXIMITY;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ikhsan.maulana.tugas.databinding.ActivityProximityBinding;

public final class ProximityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityProximityBinding.inflate(getLayoutInflater());
        var service = new AppService(this);
        service.setListener((message, exception) -> {
            bind.txtProximity.setText("No Proximity Sensor");
        }).setSensorListener(event -> {
            if (event.sensor.getType() == TYPE_PROXIMITY) {
                if (event.values[0] == 0) {
                    bind.txtProximity.setText("DEKAT");
                } else {
                    bind.txtProximity.setText("JAUH");
                }
            }
        }).requestProximity().start();
        setContentView(bind.getRoot());
    }
}