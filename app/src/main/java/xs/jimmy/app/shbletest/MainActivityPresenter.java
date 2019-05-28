package xs.jimmy.app.shbletest;

import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;
import xs.jimmy.app.shbletest.interfaces.MainActivityContract;
import xs.jimmy.app.shbletest.models.DiscoveredBluetoothDevice;

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
            view.registerBluetoothBroadcastReceiver();
            if (view.getBluetoothState()) {
                view.setBluetoothEnabled();
            } else {
                view.setBluetoothDisabled();
            }
            view.getLocationPermission();
        }
    }

    @Override
    public void stop() {
        mView.get().unRegisterBluetoothBroadcastReceiver();
        mView.get().disconnect();
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
    public void scanClicked(boolean isScanning) {
        MainActivityContract.View view = mView.get();
        if(view != null) {
            if(!isScanning) {
                if (view.isLocationPermissionGiven()) {
                    if (!view.isGPSTurnedOn())
                        view.showToast("Gps is disabled. Please enable GPS.", Toast.LENGTH_LONG);
                } else {
                    view.showToast("The app won't work well with out location permission!", Toast.LENGTH_LONG);
                }
                view.startScanningForDevices();
            }else
                view.stopScanningForDevices();
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
    public void onHeartRateDataReceived(int value) {
        if(mView.get()!=null){
            mView.get().updateChartData(value);
        }
    }

    @Override
    public void connectedToDevice() {
        if(mView.get() != null){
            mView.get().showDisconnect();
            mView.get().showToast("Connected to device",Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onSensorPositionDataReceived(Integer value) {
        if(mView.get() != null){
            String text = "Other";
            switch (value){
                case 0:
                    text = "Other";
                    break;
                case 1:
                    text = "Chest";
                    break;
                case 2:
                    text = "Wrist";
                    break;
                case 3:
                    text = "Finger";
                    break;
                case 4:
                    text = "Hand";
                    break;
                case 5:
                    text = "Ear Lobe";
                    break;
                case 6:
                    text = "Foot";
                    break;
            }
            mView.get().updateSensorPosition(text);

        }
    }

    @Override
    public void disconnectClicked() {
        if(mView.get() != null){
            mView.get().disconnect();
        }
    }

    @Override
    public void notifyBluetoothDisabled() {
        if(mView.get() != null)
            mView.get().setBluetoothDisabled();
    }

    @Override
    public void notiyBluetoothTurningOff() {
        if(mView.get() != null)
            mView.get().setBluetoothTurningOff();
    }

    @Override
    public void notifyBluetoothEnabled() {
        if(mView.get() != null)
            mView.get().setBluetoothEnabled();
    }

    @Override
    public void notifyBluetoothTurningOn() {
        if(mView.get() != null)
            mView.get().setBluetoothTurningOn();
    }

    @Override
    public void onBatchScanResult(List<ScanResult> results) {
        if(mView.get() != null)
            mView.get().updateRecyclerView(results);
    }

    @Override
    public void rationaleDenied() {
        if(mView.get() != null){
            mView.get().showToast("The app won't work well with out location permission!",Toast.LENGTH_LONG);
        }
    }
}
