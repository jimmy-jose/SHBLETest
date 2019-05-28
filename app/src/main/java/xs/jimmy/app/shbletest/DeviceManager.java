package xs.jimmy.app.shbletest;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.ble.data.Data;
import xs.jimmy.app.shbletest.interfaces.BodySensorCallback;
import xs.jimmy.app.shbletest.interfaces.HeartDataCallback;
import xs.jimmy.app.shbletest.interfaces.HeartMonitorBLEManager;

public class DeviceManager extends BleManager<HeartMonitorBLEManager> {
    private BluetoothGattCharacteristic mHeartRateCharacteristic,mBodySensorCharacteristic;

    private final static UUID HEARTRATE_MEASUREMENT_UUID = UUID
            .fromString("00002A37-0000-1000-8000-00805f9b34fb");

    public final static UUID HEARTRATE_MEASUREMENT_SERVICE_UUID = UUID
            .fromString("0000180D-0000-1000-8000-00805f9b34fb");

    private static final UUID BODY_SENSOR_LOCATION_UUID = UUID
            .fromString("00002A38-0000-1000-8000-00805f9b34fb");

    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {
        @Override
        protected void initialize() {
            setNotificationCallback(mHeartRateCharacteristic).with(heartDataCallback);
            readCharacteristic(mHeartRateCharacteristic).with(heartDataCallback).enqueue();
            enableNotifications(mHeartRateCharacteristic).enqueue();

            setNotificationCallback(mBodySensorCharacteristic).with(bodySensorCallback);
            readCharacteristic(mBodySensorCharacteristic).with(bodySensorCallback).enqueue();
            enableNotifications(mBodySensorCharacteristic).enqueue();
        }

        @Override
        protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(HEARTRATE_MEASUREMENT_SERVICE_UUID);
            if (service != null) {
                mHeartRateCharacteristic = service.getCharacteristic(HEARTRATE_MEASUREMENT_UUID);
                mBodySensorCharacteristic = service.getCharacteristic(BODY_SENSOR_LOCATION_UUID);
            }
            return mHeartRateCharacteristic != null && mBodySensorCharacteristic != null;
        }

        @Override
        protected void onDeviceDisconnected() {

        }
    };

    private BodySensorCallback bodySensorCallback = new BodySensorCallback() {
        @Override
        public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
            mCallbacks.onDataReceived(device,data);
        }
    };

    private HeartDataCallback heartDataCallback = new HeartDataCallback() {
        @Override
        public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
            mCallbacks.onDataReceived(device,data);
        }
    };
    /**
     * The manager constructor.
     * <p>
     * After constructing the manager, the callbacks object must be set with
     * {@link #setGattCallbacks(BleManagerCallbacks)}.
     * <p>
     *
     * @param context the context.
     */
    public DeviceManager(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }



}
