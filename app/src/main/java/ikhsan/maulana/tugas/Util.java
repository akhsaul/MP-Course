package ikhsan.maulana.tugas;

import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public final class Util {
    private static Geocoder coder = null;

    public static synchronized Geocoder getCoder(@NonNull Context context) {
        if (coder == null) {
            coder = new Geocoder(context);
        }
        return coder;
    }

    public static void move(@NonNull Context ctx, Class<?> cls, @Nullable Bundle bundle) {
        var dest = new Intent(ctx, cls);
        if (bundle != null) {
            dest.putExtras(bundle);
        }
        ctx.startActivity(dest);
    }

    public static boolean checkPermission(@NonNull Context ctx, @NonNull String... permissions) {
        var granted = false;
        for (String permission : permissions) {
            granted = ctx.checkPermission(permission, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED;
            Log.i(ctx.getClass().getSimpleName(), "Permission " + permission + " is granted = " + granted);
            if (!granted) {
                break;
            }
        }
        return granted;
    }

    public static void move(@NonNull Context ctx, String action) {
        ctx.startActivity(new Intent(action));
    }

    public static void move(@NonNull Context ctx, Class<?> cls) {
        move(ctx, cls, null);
    }

    public static void maintenance(@NonNull Context ctx) {
        show(ctx, "Fungsi belum tersedia");
    }

    public static void show(@NonNull Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    private static int count(@NonNull EditText... texts) {
        int total = 0;
        for (EditText text : texts) {
            total += text.getText().length();
        }

        return total;
    }

    public static void updateData(Context ctx, @NonNull DBHelper helper, @NonNull EditText... texts) {
        if (validate(texts)) {
            var builder = new StringBuilder((DBHelper.UPDATE.length() - 8) + count(texts));
            builder.append(DBHelper.UPDATE);
            replace(builder, texts);

            var sql = builder.toString();
            Log.d(UpdateDataActivity.TAG, sql);

            try (var db = helper.getWritableDatabase()) {
                db.execSQL(sql);
                Util.show(ctx, "Data Berhasil di Ubah");
            }
        } else {
            Util.show(ctx, "Input tidak boleh kosong");
        }
    }

    public static void deleteData(Context ctx, @NonNull DBHelper helper, String no) {
        try (var db = helper.getWritableDatabase()) {
            db.execSQL(DBHelper.DELETE +
                    (char) 39 + no + (char) 39);
            Util.show(ctx, "Data Berhasil di Hapus");
        }
    }

    private static boolean validate(@NonNull EditText... texts) {
        var result = false;
        for (EditText text : texts) {
            var str = text.getText().toString();
            if (!str.isEmpty() && !str.equals(" ")) {
                result = true;
                break;
            }
        }
        return result;
    }

    private static void replace(@NonNull StringBuilder builder, @NonNull EditText... texts) {
        for (int i = 0; i < texts.length; i++) {
            var str = texts[i].getText().toString();
            var index = builder.indexOf("#" + i);
            builder.insert(index + 2, str).deleteCharAt(index).deleteCharAt(index);
        }
    }

    public static void insertData(Context ctx, @NonNull DBHelper helper, @NonNull EditText... texts) {
        if (validate(texts)) {
            var builder = new StringBuilder((DBHelper.INSERT.length() - 6) + count(texts));
            builder.append(DBHelper.INSERT);
            replace(builder, texts);

            var sql = builder.toString();
            Log.d(AddDataActivity.TAG, sql);

            try (var db = helper.getWritableDatabase()) {
                db.execSQL(sql);
                Util.show(ctx, "Data Berhasil di Tambahkan");
            }
        } else {
            Util.show(ctx, "Input tidak boleh kosong");
        }
    }

    public static void turnOnGps(@NonNull Context ctx) {
        new AlertDialog.Builder(ctx)
                .setMessage("Enable GPS").setCancelable(false)
                .setPositiveButton("Yes", (d, w) -> Util.move(ctx,
                        ACTION_LOCATION_SOURCE_SETTINGS
                ))
                .setNegativeButton("No", (dialog, which) -> {
                    Util.show(ctx, "Unable to find location.");
                    dialog.cancel();
                })
                .create().show();
    }

    public static void getLocation(@NonNull AppService service, @NonNull TextView txt) {
        service.setListener((m, e) -> {
            if (!(e instanceof DecoderException)) {
                Util.show(service.getActivity(), "Unable to find location.");
            }
        }).setLocationListener(location -> {
            Log.d(service.getTag(), "location from Service = " + location);
            var build = new StringBuilder()
                    .append("Your Location:")
                    .append('\n')
                    .append("Latitude: ")
                    .append(location.getLatitude())
                    .append('\n')
                    .append("Longitude: ")
                    .append(location.getLongitude());
            txt.setText(build.toString());
        }).requestLocation().start();
    }
}
