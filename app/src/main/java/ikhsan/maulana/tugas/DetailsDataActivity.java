package ikhsan.maulana.tugas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ikhsan.maulana.tugas.databinding.ActivityDetailsDataBinding;

public final class DetailsDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityDetailsDataBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
    }
}