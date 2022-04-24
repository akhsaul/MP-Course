package ikhsan.maulana.tugas;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ikhsan.maulana.tugas.databinding.ActivityCalculatorBinding;

public class CalculatorActivity extends AppCompatActivity {
    private ActivityCalculatorBinding bind;
    private static final char[] empty = new char[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityCalculatorBinding.inflate(getLayoutInflater());
        bind.btnAdd.setOnClickListener(this::add);
        bind.btnMin.setOnClickListener(this::min);
        bind.btnMulti.setOnClickListener(this::multi);
        bind.btnDivide.setOnClickListener(this::div);
        bind.btnReset.setOnClickListener(this::reset);
        bind.btnBack.setOnClickListener(v -> finish());
        setContentView(bind.getRoot());
    }

    private void add(View v) {
        Integer bil1 = getVal(bind.inBil1.getText());
        Integer bil2 = getVal(bind.inBil2.getText());
        if (bil1 != null && bil2 != null) {
            setVal(bil1 + bil2);
        }
    }

    @Nullable
    private Integer getVal(@NonNull Editable txt) {
        String val = txt.toString();
        if (!val.isEmpty() && !val.equals(" ")) {
            try {
                return Integer.parseInt(val);
            } catch (Throwable t) {
                return null;
            }
        } else {
            return null;
        }
    }

    private void setVal(int result) {
        bind.txtHasil.setText(String.valueOf(result));
    }

    private void min(View v) {
        Integer bil1 = getVal(bind.inBil1.getText());
        Integer bil2 = getVal(bind.inBil2.getText());
        if (bil1 != null && bil2 != null) {
            setVal(bil1 - bil2);
        }
    }

    private void multi(View v) {
        Integer bil1 = getVal(bind.inBil1.getText());
        Integer bil2 = getVal(bind.inBil2.getText());
        if (bil1 != null && bil2 != null) {
            setVal(bil1 * bil2);
        }
    }

    private void div(View v) {
        Integer bil1 = getVal(bind.inBil1.getText());
        Integer bil2 = getVal(bind.inBil2.getText());
        if (bil1 != null && bil2 != null) {
            setVal(bil1 / bil2);
        }
    }

    private void reset(View v) {
        bind.inBil1.setText(empty, 0, empty.length);
        bind.inBil2.setText(empty, 0, empty.length);
        bind.txtHasil.setText(R.string.hsl);
    }
}