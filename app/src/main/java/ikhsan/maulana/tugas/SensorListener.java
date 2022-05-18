package ikhsan.maulana.tugas;

import android.hardware.Sensor;

public interface SensorListener extends android.hardware.SensorEventListener {
    @Override
    default void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
        // can be override by user implementation
    }
}