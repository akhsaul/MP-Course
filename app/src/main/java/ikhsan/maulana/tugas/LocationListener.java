package ikhsan.maulana.tugas;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface LocationListener extends android.location.LocationListener {

    default Geocoder getCoder(@NonNull Context context) {
        return Util.getCoder(context);
    }

    /**
     * @param address NULL or Address
     * */
    default void onLocationDecoded(@Nullable Address address) {
        // ignore
        // can be override by user implementation
    }
    /**
     * @param addressList empty or list of Address
     * */
    default void onLocationDecoded(@NonNull List<Address> addressList) {
        // ignore
        // can be override by user implementation
    }
}