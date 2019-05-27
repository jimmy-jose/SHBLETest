package xs.jimmy.app.shbletest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import xs.jimmy.app.shbletest.Model.DiscoveredBluetoothDevice;
import xs.jimmy.app.shbletest.unnecessary.BluetoothUtils;

public class MainActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks,
        DevicesAdapter.OnItemClickListener,
        HeartMonitorBLEManager,
        MainActivityContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int HEART_RATE_MEASUREMENT_VALUE_FORMAT = BluetoothGattCharacteristic.FORMAT_UINT8;

    private DevicesAdapter mAdapter;

    private Button bluetoothSwitch;
    private Button scanButton;
    private LottieAnimationView scaningAnnimation;
    private BarChart chart;


    private boolean bluetoothEnabled;
    private BluetoothAdapter mBluetoothAdapter;
    private DeviceManager deviceManager;
    private BluetoothDevice mDevice;

    private ArrayList<DiscoveredBluetoothDevice> mList = new ArrayList<>();

    private MainActivityContract.Presenter mPresenter;


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action!=null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        // Bluetooth has been turned off;
                        bluetoothSwitch.setEnabled(true);
                        setBluetoothDisabled();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        // Bluetooth is turning off;
                        bluetoothSwitch.setEnabled(false);
                        bluetoothSwitch.setText(getApplicationContext().getString(R.string.turning_off));
                        break;
                    case BluetoothAdapter.STATE_ON:
                        // Bluetooth has been on
                        bluetoothSwitch.setEnabled(true);
                        setBluetoothenabled();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        // Bluetooth is turning on
                        bluetoothSwitch.setEnabled(false);
                        bluetoothSwitch.setText(getApplicationContext().getString(R.string.turning_on));
                        break;
                }
            }
        }
    };

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            Log.d(TAG, "onScanResult: "+result);
        }

        @Override
        public void onBatchScanResults(@NonNull List<ScanResult> results) {
            if (results.isEmpty())
                return;
            mList.clear();
            for(ScanResult result: results){
                mList.add(new DiscoveredBluetoothDevice(result));
            }
            mAdapter.setmDevices(mList);
            Log.d(TAG, "onBatchScanResults: "+results.toString());
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d(TAG, "onScanFailed: "+errorCode);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new MainActivityPresenter();
        mPresenter.setView(this);

        RecyclerView devicesRCView = findViewById(R.id.devices_rcv);
        bluetoothSwitch = findViewById(R.id.bluetooth_switch);
        scanButton = findViewById(R.id.scan);
        scaningAnnimation = findViewById(R.id.animation_view);
        chart = findViewById(R.id.chart);

        devicesRCView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        devicesRCView.setLayoutManager(layoutManager);
        mAdapter = new DevicesAdapter(this,mList);
        devicesRCView.setAdapter(mAdapter);

        deviceManager = new DeviceManager(getApplication());
        deviceManager.setGattCallbacks(this);

        scanButton.setEnabled(false);

        bluetoothSwitch.setOnClickListener(v -> mPresenter.bluetoothSwitchClicked(bluetoothEnabled));

        scanButton.setOnClickListener(v ->
                mPresenter.scanClicked(scanButton.getText().equals(getApplicationContext().getString(R.string.stop_scanning))));

    }

    @Override
    public void registerBluetoothBroadcastReciever(){
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    public void disableBluetooth() {
        if(scanButton.getText().equals(getApplicationContext().getString(R.string.stop_scanning)))
            stopScanningForDevices();
        mList.clear();
        mAdapter.setmDevices(mList);
        mBluetoothAdapter.disable();
    }

    @Override
    public boolean getBluetoothState() {
        mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter(this);
        return mBluetoothAdapter.isEnabled();
    }

    @Override
    public void setBluetoothenabled(){
        bluetoothEnabled = true;
        bluetoothSwitch.setText(getApplicationContext().getString(R.string.on));
        bluetoothSwitch.setCompoundDrawablesWithIntrinsicBounds(
                AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_bluetooth_blue_24dp),
                null,null,null);
        scanButton.setEnabled(true);
        scanButton.setText(getApplicationContext().getString(R.string.scan));
    }

    @Override
    public void setBluetoothDisabled(){
        bluetoothEnabled = false;
        bluetoothSwitch.setText(getApplicationContext().getString(R.string.off));
        bluetoothSwitch.setCompoundDrawablesWithIntrinsicBounds(
                AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_bluetooth_black_24dp),
                null,null,null);
        scanButton.setEnabled(false);
    }

    @Override
    public void startScanningForDevices() {
        scanButton.setText(getApplicationContext().getString(R.string.stop_scanning));
        addDummyData();
        scaningAnnimation.setVisibility(View.VISIBLE);
        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(500)
                .setUseHardwareBatchingIfSupported(false)
                .build();
//        List<ScanFilter> filters = new ArrayList<>();
//        filters.add(new ScanFilter.Builder().setServiceUuid(scanUUID).build());
        scanner.startScan(null, settings, mScanCallback);

    }

    @Override
    public void stopScanningForDevices(){
        scanButton.setText(getApplicationContext().getString(R.string.scan));
        scaningAnnimation.setVisibility(View.INVISIBLE);
        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(mScanCallback);
    }

    @Override
    protected void onStop() {
        mPresenter.stop();
        super.onStop();
    }

    @Override
    public void unRegisterBluetoothBroadcastReceiver(){
        unregisterReceiver(mReceiver);
    }

    @Override
    public void enableBluetooth() {
        bluetoothSwitch.setEnabled(false);
        bluetoothSwitch.setText(getApplicationContext().getString(R.string.turning_on));
        mBluetoothAdapter.enable();
    }

    //region Location Permission
    @Override
    public void getLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            mPresenter.locationPermissionGranted();
            Log.d(TAG, "getLocationPermission: location permission available");
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.location_rationale),
                    Constants.REQUEST_LOCATION_ENABLE_CODE, perms);
        }
    }

    @Override
    public boolean isGPSTurnedOn() {
        int locationMode = 0;
        //Equal or higher than API 19/KitKat
        try {
            locationMode = Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY){
                return true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            getLocationPermission();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        getLocationPermission();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        getLocationPermission();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.d(TAG, "onRationaleAccepted: ");
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Log.d(TAG, "onRationaleDenied: ");
        getLocationPermission();
    }

    @Override
    public void onItemClick(@NonNull DiscoveredBluetoothDevice device) {
        mPresenter.onItemClicked(device,scanButton.getText().equals(getApplicationContext().getString(R.string.stop_scanning)));
    }

    /**
     * Connect to peripheral.
     */
    @Override
    public void connect(@NonNull final DiscoveredBluetoothDevice device) {
        // Prevent from calling again when called again (screen orientation changed)
//        if (mDevice == null) {
            mDevice = device.getDevice();
            final LogSession logSession
                    = Logger.newSession(getApplication(), null, device.getAddress(), device.getName());
            deviceManager.setLogger(logSession);
            reconnect();
//        }
    }

    /**
     * Reconnects to previously connected device.
     * If this device was not supported, its services were cleared on disconnection, so
     * reconnection may help.
     */
    private void reconnect() {
        if (mDevice != null) {
            deviceManager.connect(mDevice)
                    .retry(3, 100)
                    .useAutoConnect(false)
                    .enqueue();
        }
    }

    private void addDummyData(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(10);
        arrayList.add(20);
        arrayList.add(30);
        arrayList.add(40);
        arrayList.add(50);
        arrayList.add(60);

        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < arrayList.size(); i++) {
            int a = arrayList.get(i);
            entries.add(new BarEntry(i, a));
        }

        BarDataSet barDataSet = new BarDataSet(entries,"dummy data");

        BarData barData = new BarData(barDataSet);
        chart.setData(barData);
        chart.invalidate(); // refresh
    }

    @Override
    public void showToast(String var1, int var3) {
        Toast.makeText(getApplicationContext(),var1,var3).show();
    }

    @Override
    public void onDeviceConnecting(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onDeviceConnecting: ");
    }

    @Override
    public void onDeviceConnected(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onDeviceConnected: ");
    }

    @Override
    public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onDeviceDisconnecting: ");
    }

    @Override
    public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onDeviceDisconnected: ");
    }

    @Override
    public void onLinkLossOccurred(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onLinkLossOccurred: ");
    }

    @Override
    public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
        Log.d(TAG, "onServicesDiscovered: ");
    }

    @Override
    public void onDeviceReady(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onDeviceReady: ");
    }

    @Override
    public void onBondingRequired(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onBondingRequired: ");
    }

    @Override
    public void onBonded(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onBonded: ");
    }

    @Override
    public void onBondingFailed(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onBondingFailed: ");
    }

    @Override
    public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {
        Log.d(TAG, "onError: ");
    }

    @Override
    public void onDeviceNotSupported(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onDeviceNotSupported: ");
    }

    @Override
    public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
        Log.d(TAG, "onDataReceived: "+ data.getIntValue(Data.FORMAT_UINT8,1));
    }
    //endregion
}
