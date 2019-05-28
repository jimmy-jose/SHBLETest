package xs.jimmy.app.shbletest.interfaces;

import android.bluetooth.BluetoothDevice;

import no.nordicsemi.android.ble.data.Data;

public interface BodySensorUpdateCallBack {
    void updateBodySensorPosition(BluetoothDevice device, Data data);
}
