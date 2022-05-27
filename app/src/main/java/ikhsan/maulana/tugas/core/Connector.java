package ikhsan.maulana.tugas.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ikhsan.maulana.tugas.AddActivity;
import ikhsan.maulana.tugas.ReadActivity;

public class Connector {
    private static Connector singleton = null;
    private static final String DEV = "http://127.0.0.1:8000";
    private static final String PUB = "https://mp-course.herokuapp.com";
    private static final String PATH = "/api/mahasiswa/";
    private static final String AGENT = "ikhsan.maulana.tugas";
    private static String BASE = "";
    private final ExecutorService executor;
    private final Handler mainThreadHandler;

    Connector() {
        buildURL();
        mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
        executor = Executors.newFixedThreadPool(1, r -> new Thread() {

            @Override
            public void run() {
                setName("Background");
                setPriority(Thread.MAX_PRIORITY);
                r.run();
            }
        });
    }

    @NonNull
    public static Connector getInstance() {
        if (singleton == null) {
            singleton = new Connector();
        }
        return singleton;
    }

    public void initialize(@NonNull Context ctx) {
        run(() -> {
            AndroidNetworking.initialize(ctx);
            AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BASIC);
        });
    }

    private void buildURL() {
        var env = System.getenv("myEnv");
        env = env == null ? System.getProperty("myEnv") : env;
        if (env == null || !env.equals("DEV")) {
            BASE = PUB;
        } else {
            BASE = DEV;
        }
        BASE = BASE + PATH;
    }

    @NonNull
    public ANRequest apiAdd(@Nullable JSONObject json) {
        return AndroidNetworking.post(BASE + "add")
                .setUserAgent(AGENT)
                .setPriority(Priority.HIGH)
                .setTag(AddActivity.class)
                .addJSONObjectBody(json)
                .build();
    }

    @NonNull
    public ANRequest apiRead() {
        return AndroidNetworking.get(BASE)
                .setUserAgent(AGENT)
                .setTag(ReadActivity.class)
                .setPriority(Priority.HIGH)
                .build();
    }

    public void run(@NonNull Runnable r) {
        executor.execute(r);
    }

    public void runOnMain(@NonNull Runnable r) {
        mainThreadHandler.post(r);
    }
    public void runOnMain(@NonNull Runnable r, long delay) {
        mainThreadHandler.postDelayed(r, delay);
    }
}
