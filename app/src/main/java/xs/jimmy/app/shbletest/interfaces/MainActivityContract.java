package xs.jimmy.app.shbletest.interfaces;

import androidx.annotation.NonNull;

import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;
import xs.jimmy.app.shbletest.models.DiscoveredBluetoothDevice;

public interface MainActivityContract {
    interface View {

        /**
         * Method to get location permission
         */
        void getLocationPermission();

        /**
         * Method to register Broadcast receiver for bluetooth state changes
         */
        void registerBluetoothBroadcastReceiver();

        /**
         * Method to get state of bluetooth
         * @return true if bluetooth is enabled
         *         false otherwise
         */
        boolean getBluetoothState();

        /**
         * Method to enable Bluetooth
         */
        void enableBluetooth();

        /**
         * Method to disable bluetooth
         */
        void disableBluetooth();

        /**
         * Method to make required UI changes when bluetooth is enabled
         */
        void setBluetoothEnabled();

        /**
         * Method to make required UI changes when bluetooth is disabled
         */
        void setBluetoothDisabled();

        /**
         * Method to start scanning for devices
         */
        void startScanningForDevices();

        /**
         * Method to stop scanning for devices
         */
        void stopScanningForDevices();

        /**
         * Method to connect to the discovered device
         * @param device Will contain the details of the device to be connected to.
         *
         */
        void connect(@NonNull DiscoveredBluetoothDevice device);

        /**
         * Method to unregister bluetooth broadcast receiver
         */
        void unRegisterBluetoothBroadcastReceiver();

        /**
         * Method to check if GPS is turned on
         * @return true if GPS is on false otherwise
         */
        boolean isGPSTurnedOn();

        /**
         * Method to update the chart data
         * @param value value is the data received from the device
         */
        void updateChartData(int value);

        /**
         * Method to show a toast to the user
         * @param text Text to show
         * @param duration duration for the toast
         */
        void showToast(String text, int duration);

        /**
         * Method to update the sensor position
         * @param text value to be updated with
         */
        void updateSensorPosition(String text);

        /**
         * method to disconnect from device
         */
        void disconnect();

        /**
         * Method to update UI while bluetooth is turning off
         */
        void setBluetoothTurningOff();

        /**
         * Method to update UI while bluetooth is turning on
         */
        void setBluetoothTurningOn();

        /**
         * Method to update recyclerview with scanned data
         */
        void updateRecyclerView(List<ScanResult> results);

        /**
         * Method to check if location permission is given
         * @return true if permission is granted false otherwise
         */
        boolean isLocationPermissionGiven();

        /**
         * Method to show disconnect button
         */
        void showDisconnect();
    }

    interface Presenter {
        /**
         * Method to set view to the presenter
         * @param var1 view object
         */
        void setView(@NonNull View var1);

        /**
         * Method to initialise the presenter
         */
        void start();

        /**
         * Method to stop the presenter
         */
        void stop();

        /**
         * Method to inform bluetooth swich is clicked
         * @param isEnabled whether bluetooth is enabled or not
         */
        void bluetoothSwitchClicked(boolean isEnabled);

        /**
         * Method to inform Scan button is clicked
         * @param isScanning whether scan is in progress or not
         */
        void scanClicked(boolean isScanning);

        /**
         * Method to inform a device from the scan list is selectec to connect
         * @param device Bluetooth device selected
         * @param isScanning whether scanning is in progress or not
         */
        void onItemClicked(DiscoveredBluetoothDevice device, boolean isScanning);

        /**
         * Methdod to inform location permission is granted
         */
        void locationPermissionGranted();

        /**
         * Method to inform heartrate data is received from the device
         * @param value recived value
         */
        void onHeartRateDataReceived(int value);

        /**
         * Method to inform connection successfully made
         */
        void connectedToDevice();

        /**
         * Method to inform sensor position data is received from the device
         * @param value received value
         */
        void onSensorPositionDataReceived(Integer value);

        /**
         * Method to inform disconnect is clicked
         */
        void disconnectClicked();

        /**
         * Method to notify that the bluetooth is disabled
         */
        void notifyBluetoothDisabled();

        /**
         * Method to notify that the bluetooth is turning off
         */
        void notiyBluetoothTurningOff();

        /**
         * Method to notify that the bluetooth is enabled
         */
        void notifyBluetoothEnabled();

        /**
         * Method to notify that the bluetooth is turning on
         */
        void notifyBluetoothTurningOn();

        /**
         * Method to notify the presenter of the scan result
         */
        void onBatchScanResult(List<ScanResult> results);

        /**
         * Method to notify that the rationale is denied
         */
        void rationaleDenied();
    }
}
