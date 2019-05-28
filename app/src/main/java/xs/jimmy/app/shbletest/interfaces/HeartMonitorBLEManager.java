package xs.jimmy.app.shbletest.interfaces;

import no.nordicsemi.android.ble.BleManagerCallbacks;

public interface HeartMonitorBLEManager extends BleManagerCallbacks,
        HeartDataCallback, BodySensorUpdateCallBack {
}
