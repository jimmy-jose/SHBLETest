package xs.jimmy.app.shbletest;

import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import xs.jimmy.app.shbletest.Model.DiscoveredBluetoothDevice;

public class MainActivityPresenter implements MainActivityContract.Presenter {

    private WeakReference<MainActivityContract.View> mView;

    @Override
    public void setView(@NonNull MainActivityContract.View view) {
        this.mView = new WeakReference<>(view);
    }


    @Override
    public void start() {
        MainActivityContract.View view = mView.get();
        if(view != null) {
            view.registerBluetoothBroadcastReciever();
            if (view.getBluetoothState()) {
                view.setBluetoothenabled();
            } else {
                view.setBluetoothDisabled();
            }
            view.getLocationPermission();
        }
    }

    @Override
    public void stop() {
        mView.get().unRegisterBluetoothBroadcastReceiver();
    }


    @Override
    public void bluetoothSwitchClicked(boolean isEnabled) {
        if(isEnabled){
            mView.get().disableBluetooth();
        }else{
            mView.get().enableBluetooth();
        }
    }

    @Override
    public void scanClicked(boolean flag) {
        if(flag){
            mView.get().stopScanningForDevices();
        }else {
            mView.get().startScanningForDevices();
        }
    }

    @Override
    public void onItemClicked(DiscoveredBluetoothDevice device, boolean isScanning) {
        MainActivityContract.View view = mView.get();
        if(!view.getBluetoothState()){
            view.showToast("Bluetooth is disabled!", Toast.LENGTH_LONG);
        }else {
            if(isScanning)
                view.stopScanningForDevices();
            view.connect(device);
        }
    }

    @Override
    public void locationPermissionGranted() {
        if(!mView.get().isGPSTurnedOn()){
            mView.get().showToast("Gps is disabled. Please enable GPS.",Toast.LENGTH_LONG);
        }
    }



    @Override
    public void detachView() {

    }
}
