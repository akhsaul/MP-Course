package ikhsan.maulana.tugas;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import ikhsan.maulana.tugas.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
    }
}