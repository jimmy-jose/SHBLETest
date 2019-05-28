package xs.jimmy.app.shbletest;

import android.widget.Toast;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;
import xs.jimmy.app.shbletest.interfaces.MainActivityContract;
import xs.jimmy.app.shbletest.models.DiscoveredBluetoothDevice;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainActivityPresenterTest {
    @Mock
    MainActivityContract.View mView;

    private MainActivityContract.Presenter mPresenter;

    @Mock
    DiscoveredBluetoothDevice device;

    List<ScanResult> data = new ArrayList<>();

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        mPresenter = new MainActivityPresenter();
        mPresenter.setView(mView);
        when(mView.getBluetoothState()).thenReturn(true);
        when(mView.isGPSTurnedOn()).thenReturn(false);
        when(mView.isLocationPermissionGiven()).thenReturn(false);
    }

    @Test
    public void bluetoothSwitchClickedEnabled() {
        mPresenter.bluetoothSwitchClicked(true);
        verify(mView).disableBluetooth();
    }

    @Test
    public void bluetoothSwitchClickedDisabled() {
        mPresenter.bluetoothSwitchClicked(false);
        verify(mView).enableBluetooth();
    }

    @Test
    public void start() {
        mPresenter.start();
        verify(mView).registerBluetoothBroadcastReceiver();
        verify(mView).getBluetoothState();
        verify(mView).setBluetoothEnabled();
        verify(mView).getLocationPermission();
    }

    @Test
    public void stop() {
        mPresenter.stop();
        verify(mView).unRegisterBluetoothBroadcastReceiver();
    }


    @Test
    public void scanClickedTrue() {
        mPresenter.scanClicked(true);
        verify(mView).stopScanningForDevices();
    }

    @Test
    public void scanClickedFalse() {
        mPresenter.scanClicked(false);
        verify(mView).showToast("The app won't work well with out location permission!", Toast.LENGTH_LONG);
        verify(mView).startScanningForDevices();
    }


    @Test
    public void onItemClicked() {
        mPresenter.onItemClicked(device,true);
        verify(mView).stopScanningForDevices();
        verify(mView).connect(device);
    }

    @Test
    public void locationPermissionGranted() {
        mPresenter.locationPermissionGranted();
        verify(mView).isGPSTurnedOn();
        verify(mView).showToast("Gps is disabled. Please enable GPS.", Toast.LENGTH_LONG);
    }

    @Test
    public void onHeartRateDataReceived() {
        mPresenter.onHeartRateDataReceived(10);
        verify(mView).updateChartData(10);
    }

    @Test
    public void connectedToDevice() {
        mPresenter.connectedToDevice();
        verify(mView).showToast("Connected to device",Toast.LENGTH_LONG);
    }

    @Test
    public void onSensorPosition0Received() {
        mPresenter.onSensorPositionDataReceived(0);
        verify(mView).updateSensorPosition("Other");
    }

    @Test
    public void onSensorPosition1Received() {
        mPresenter.onSensorPositionDataReceived(1);
        verify(mView).updateSensorPosition("Chest");
    }

    @Test
    public void onSensorPosition2Received() {
        mPresenter.onSensorPositionDataReceived(2);
        verify(mView).updateSensorPosition("Wrist");
    }

    @Test
    public void onSensorPosition3Received() {
        mPresenter.onSensorPositionDataReceived(3);
        verify(mView).updateSensorPosition("Finger");
    }

    @Test
    public void onSensorPosition4Received() {
        mPresenter.onSensorPositionDataReceived(4);
        verify(mView).updateSensorPosition("Hand");
    }

    @Test
    public void onSensorPosition5Received() {
        mPresenter.onSensorPositionDataReceived(5);
        verify(mView).updateSensorPosition("Ear Lobe");
    }

    @Test
    public void onSensorPosition6Received() {
        mPresenter.onSensorPositionDataReceived(6);
        verify(mView).updateSensorPosition("Foot");
    }

    @Test
    public void disconnectClicked() {
        mPresenter.disconnectClicked();
        verify(mView).disconnect();
    }

    @Test
    public void notifyBluetoothDisabled() {
        mPresenter.notifyBluetoothDisabled();
        verify(mView).setBluetoothDisabled();
    }

    @Test
    public void notiyBluetoothTurningOff() {
        mPresenter.notiyBluetoothTurningOff();
        verify(mView).setBluetoothTurningOff();
    }

    @Test
    public void notifyBluetoothEnabled() {
        mPresenter.notifyBluetoothEnabled();
        verify(mView).setBluetoothEnabled();
    }

    @Test
    public void notifyBluetoothTurningOn() {
        mPresenter.notifyBluetoothTurningOn();
        verify(mView).setBluetoothTurningOn();
    }

    @Test
    public void onBatchScanResult() {
        mPresenter.onBatchScanResult(data);
        verify(mView).updateRecyclerView(data);
    }

    @Test
    public void rationaleDenied() {
        mPresenter.rationaleDenied();
        verify(mView).showToast("The app won't work well with out location permission!",Toast.LENGTH_LONG);
    }
}