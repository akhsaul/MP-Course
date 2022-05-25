package ikhsan.maulana.tugas.core;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;

import org.json.JSONObject;

import ikhsan.maulana.tugas.AddActivity;
import ikhsan.maulana.tugas.UpdateDataActivity;

public class Connector {
    private static Connector singleton = null;
    private static final String DEV = "http://127.0.0.1:8000";
    private static final String PUB = "https://mp-course.herokuapp.com";
    private static final String PATH = "/api/mahasiswa/";
    private static final String AGENT = "ikhsan.maulana.tugas";
    private static String BASE = "";

    Connector(){
        buildURL();
    }

    public static Connector getInstance() {
        if (singleton == null) {
            singleton = new Connector();
        }
        return singleton;
    }

    public void initialize(@NonNull Context ctx) {
        new Handler().post(() -> AndroidNetworking.initialize(ctx));
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

    public ANRequest apiAdd(JSONObject json) {
        return AndroidNetworking.post(BASE + "add")
                .setUserAgent(AGENT)
                .setTag(AddActivity.class)
                .addJSONObjectBody(json)
                .build();
    }
}
