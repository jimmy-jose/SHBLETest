package xs.jimmy.app.shbletest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import xs.jimmy.app.shbletest.interfaces.MainActivityContract;

import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when;

public class MainActivityPresenterTest {
    @Mock
    MainActivityContract.View mView;

    MainActivityContract.Presenter mPresenter;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        mPresenter = new MainActivityPresenter();
        mPresenter.setView(mView);
        when(mView.getBluetoothState()).thenReturn(true);
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
        verify(mView).registerBluetoothBroadcastReciever();
        if(verify(mView).getBluetoothState())
            verify(mView).setBluetoothDisabled();
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
        verify(mView).startScanningForDevices();    }

    @Test
    public void onItemClicked() {

    }

    @Test
    public void locationPermissionGranted() {
    }

    @Test
    public void onDataReceived() {
    }

    @Test
    public void detachView() {
    }
}