package ikhsan.maulana.tugas;

import androidx.annotation.NonNull;

public interface AppServiceListener {
    void onError(@NonNull String message, @NonNull Exception exception);
}
