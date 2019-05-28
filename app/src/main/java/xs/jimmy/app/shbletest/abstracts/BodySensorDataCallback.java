package xs.jimmy.app.shbletest.abstracts;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;
import xs.jimmy.app.shbletest.interfaces.BodySensorUpdateCallBack;


public abstract class BodySensorDataCallback implements DataReceivedCallback, BodySensorUpdateCallBack {
    @Override
    public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
        updateBodySensorPosition(device,data);
    }
}
