package xs.jimmy.app.shbletest.abstracts;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;
import xs.jimmy.app.shbletest.interfaces.HeartDataCallback;


public abstract class HeartRateSensorDataCallback implements DataReceivedCallback, HeartDataCallback {
    @Override
    public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
        updateHeartRateData(device,data);
    }
}
