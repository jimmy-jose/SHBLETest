package xs.jimmy.app.shbletest;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;

public interface HeartDataCallback extends DataReceivedCallback {
    @Override
    void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data);
}
