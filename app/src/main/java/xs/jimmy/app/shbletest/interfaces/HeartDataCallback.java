package xs.jimmy.app.shbletest.interfaces;

import android.bluetooth.BluetoothDevice;

import no.nordicsemi.android.ble.data.Data;

public interface HeartDataCallback {
    void updateHeartRateData(BluetoothDevice device, Data data);
}
