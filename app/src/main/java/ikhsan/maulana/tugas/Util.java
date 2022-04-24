package ikhsan.maulana.tugas;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class Util {
    public static void move(@NonNull Context ctx, Class<?> cls) {
        ctx.startActivity(new Intent(ctx, cls));
    }

    public static void maintenance(@NonNull Context ctx) {
        Toast.makeText(ctx, "Under Maintenance", Toast.LENGTH_SHORT).show();
    }
}
