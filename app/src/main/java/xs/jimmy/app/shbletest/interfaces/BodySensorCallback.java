package xs.jimmy.app.shbletest.interfaces;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;

public interface BodySensorCallback extends DataReceivedCallback {
    @Override
    void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data);
}
