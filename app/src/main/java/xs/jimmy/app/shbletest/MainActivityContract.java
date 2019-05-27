package xs.jimmy.app.shbletest;

import android.content.Context;

import androidx.annotation.NonNull;

import xs.jimmy.app.shbletest.Model.DiscoveredBluetoothDevice;

public interface MainActivityContract {
    interface View {

        void getLocationPermission();

        void registerBluetoothBroadcastReciever();

        boolean getBluetoothState();

        void enableBluetooth();

        void disableBluetooth();

        void setBluetoothenabled();

        void setBluetoothDisabled();

        void startScanningForDevices();

        void stopScanningForDevices();

        void connect(@NonNull DiscoveredBluetoothDevice device);

        void unRegisterBluetoothBroadcastReceiver();

        boolean isGPSTurnedOn();

        void showToast(String text, int duration);
    }

    interface Presenter {
        void setView(@NonNull View var1);

        void detachView();

        void start();

        void stop();

        void bluetoothSwitchClicked(boolean isEnabled);

        void scanClicked(boolean flag);

        void onItemClicked(DiscoveredBluetoothDevice device, boolean isScanning);

        void locationPermissionGranted();
    }
}
