package ikhsan.maulana.tugas;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class Util {
    public static void move(@NonNull Context ctx, Class<?> cls) {
        ctx.startActivity(new Intent(ctx, cls));
    }

    public static void maintenance(@NonNull Context ctx) {
        show(ctx, "Under Maintenance");
    }

    public static void show(@NonNull Context ctx, String message){
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    private static int count(@NonNull EditText... texts) {
        int total = 0;
        for (EditText text : texts) {
            total += text.getText().length() + 3;
        }

        if (total != 0) {
            return --total;
        } else {
            return total;
        }
    }

    public static void insertDB(Context ctx, @NonNull DBHelper helper, @NonNull EditText... texts) {
        var builder = new StringBuilder(DBHelper.INSERT.length() + count(texts) + 1);
        builder.append(DBHelper.INSERT);
        for (int x = 0; x < texts.length; x++) {
            var txt = texts[x].getText();
            var chars = new char[txt.length() + 2];
            chars[0] = (char) 39;
            for (int i = 0; i < txt.length(); i++) {
                chars[i + 1] = txt.charAt(i);
            }
            chars[chars.length - 1] = (char) 39;
            builder.append(chars);
            if ((x + 1) < texts.length) {
                builder.append((char) 44);
            }
        }
        builder.append((char) 41);

        Log.d("Capacity", String.valueOf(builder.capacity()));
        Log.d("Length", String.valueOf(builder.length()));

        try (var db = helper.getWritableDatabase()) {
            var sql = builder.toString();
            Log.d(DBHelper.TAG, sql);
            db.execSQL(sql);
            Util.show(ctx, "Data Berhasil Ditambahkan");
        }
    }
}
