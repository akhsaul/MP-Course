package ikhsan.maulana.tugas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ikhsan.maulana.tugas.databinding.ActivityUpdateDataBinding;

public final class UpdateDataActivity extends AppCompatActivity {
    public final static String TAG = UpdateDataActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var bind = ActivityUpdateDataBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
    }
}