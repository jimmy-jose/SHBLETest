package xs.jimmy.app.shbletest;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.log.LogSession;

public class DeviceManager extends BleManager<HeartMonitorBLEManager> {
    private LogSession mLogSession;
    private BluetoothGattCharacteristic mHeartRateCharacteristic;

    private final static UUID HMC = UUID
            .fromString("00002A37-0000-1000-8000-00805f9b34fb");

    public final static UUID HMS = UUID
            .fromString("0000180D-0000-1000-8000-00805f9b34fb");

    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {
        @Override
        protected void initialize() {
            setNotificationCallback(mHeartRateCharacteristic).with(heartDataCallback);
            readCharacteristic(mHeartRateCharacteristic).with(heartDataCallback).enqueue();
            enableNotifications(mHeartRateCharacteristic).enqueue();
        }

        @Override
        protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(HMS);
            if (service != null) {
                mHeartRateCharacteristic = service.getCharacteristic(HMC);

            }

//            boolean writeRequest = false;
//            if (mLedCharacteristic != null) {
//                final int rxProperties = mLedCharacteristic.getProperties();
//                writeRequest = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
//            }

            return mHeartRateCharacteristic != null;
        }

        @Override
        protected void onDeviceDisconnected() {

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


    /**
     * Sets the log session to be used for low level logging.
     * @param session the session, or null, if nRF Logger is not installed.
     */
    public void setLogger(@Nullable final LogSession session) {
        this.mLogSession = session;
    }

}
