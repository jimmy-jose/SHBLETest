package xs.jimmy.app.shbletest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import xs.jimmy.app.shbletest.adapters.DevicesAdapter;
import xs.jimmy.app.shbletest.interfaces.HeartMonitorBLEManager;
import xs.jimmy.app.shbletest.interfaces.MainActivityContract;
import xs.jimmy.app.shbletest.models.DiscoveredBluetoothDevice;
import xs.jimmy.app.shbletest.utils.DeviceManager;

public class MainActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks,
        DevicesAdapter.OnItemClickListener,
        HeartMonitorBLEManager,
        MainActivityContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_LOCATION_ENABLE_CODE = 101;

    private DevicesAdapter mAdapter;
    private Button bluetoothSwitch;
    private Button mDeviceConnect;
    private Button mDisconnect;
    private Button scanButton;
    private TextView position;
    private LottieAnimationView scanningAnimation;
    private BarChart chart;

    private boolean bluetoothEnabled;
    private BluetoothAdapter mBluetoothAdapter;
    private DeviceManager deviceManager;
    private BluetoothDevice mDevice;

    private ArrayList<DiscoveredBluetoothDevice> mList = new ArrayList<>();

    private MainActivityContract.Presenter mPresenter;

    private List<BarEntry> entries = new ArrayList<>();

    private int i = 0;

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
                        mPresenter.notifyBluetoothDisabled();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        // Bluetooth is turning off;
                        mPresenter.notiyBluetoothTurningOff();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        // Bluetooth has been on
                        mPresenter.notifyBluetoothEnabled();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        // Bluetooth is turning on
                        mPresenter.notifyBluetoothTurningOn();
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
            mPresenter.onBatchScanResult(results);
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
        scanningAnimation = findViewById(R.id.animation_view);
        chart = findViewById(R.id.chart);
        position = findViewById(R.id.position_value);
        mDisconnect = findViewById(R.id.disconnect);

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

        mDisconnect.setOnClickListener(v -> mPresenter.disconnectClicked());

    }

    @Override
    protected void onStop() {
        mPresenter.stop();
        super.onStop();
    }

    @Override
    public void registerBluetoothBroadcastReceiver(){
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
        if(mBluetoothAdapter == null) {
            BluetoothManager mBluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        return mBluetoothAdapter.isEnabled();
    }

    @Override
    public void setBluetoothEnabled(){
        bluetoothSwitch.setEnabled(true);
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
        bluetoothSwitch.setEnabled(true);
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
        scanningAnimation.setVisibility(View.VISIBLE);
        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(1000)
                .setUseHardwareBatchingIfSupported(false)
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(DeviceManager.HEARTRATE_MEASUREMENT_SERVICE_UUID)).build());
        scanner.startScan(filters, settings, mScanCallback);
    }

    @Override
    public void stopScanningForDevices(){
        scanButton.setText(getApplicationContext().getString(R.string.scan));
        scanningAnimation.setVisibility(View.INVISIBLE);
        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(mScanCallback);
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

    @Override
    public void onItemClick(Button mConnect, @NonNull DiscoveredBluetoothDevice device) {
        mDeviceConnect = mConnect;
        mPresenter.onItemClicked(device,scanButton.getText().equals(getApplicationContext().getString(R.string.stop_scanning)));
    }

    /**
     * Connect to peripheral.
     */
    @Override
    public void connect(@NonNull final DiscoveredBluetoothDevice device) {
        if(mDisconnect.getVisibility()!=View.VISIBLE){
            mDevice = device.getDevice();
            if (mDevice != null) {
                deviceManager.connect(mDevice)
                        .retry(3, 100)
                        .useAutoConnect(false)
                        .enqueue();
            }
        }else{
            showToast("Please disconnect before connecting to another device.",Toast.LENGTH_LONG);
        }
    }

    @Override
    public void disconnect(){
        if(mDevice != null) {
            deviceManager.disconnect().enqueue();
            mDeviceConnect.setEnabled(true);
            mDeviceConnect.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            mDeviceConnect.setText(getApplicationContext().getText(R.string.connect));
            updateSensorPosition("");
            i = 0;
            entries.clear();
            chart.clear();
            if(mDisconnect.getVisibility()==View.VISIBLE)
                mDisconnect.setVisibility(View.GONE);
        }
    }

    @Override
    public void setBluetoothTurningOff() {
        bluetoothSwitch.setEnabled(false);
        bluetoothSwitch.setEnabled(false);
        bluetoothSwitch.setText(getApplicationContext().getString(R.string.turning_off));
    }

    @Override
    public void setBluetoothTurningOn() {
        bluetoothSwitch.setEnabled(false);
        bluetoothSwitch.setText(getApplicationContext().getString(R.string.turning_on));
    }

    @Override
    public void updateRecyclerView(List<ScanResult> results) {
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
    public boolean isLocationPermissionGiven() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        return EasyPermissions.hasPermissions(this, perms);
    }

    @Override
    public void showDisconnect() {
        mDisconnect.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateChartData(int value){
        entries.add(new BarEntry(i++, value));
        BarDataSet barDataSet = new BarDataSet(entries,"Heart rate data");
        BarData barData = new BarData(barDataSet);
        chart.setData(barData);
        chart.invalidate(); // refresh
    }

    @Override
    public void showToast(String var1, int var3) {
        Toast.makeText(getApplicationContext(),var1,var3).show();
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
                    REQUEST_LOCATION_ENABLE_CODE, perms);
        }
    }

    @Override
    public boolean isGPSTurnedOn() {
        int locationMode;
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
        mPresenter.rationaleDenied();
    }
    //endregion

    @Override
    public void updateSensorPosition(String text) {
        position.setText(text);
    }

    @Override
    public void onDeviceConnecting(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onDeviceConnecting: ");
        mDeviceConnect.setText(getApplicationContext().getString(R.string.connecting));

    }

    @Override
    public void onDeviceConnected(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onDeviceConnected: ");
        mDeviceConnect.setText(getApplicationContext().getString(R.string.connected));
        mDeviceConnect.setEnabled(false);
        mDeviceConnect.setCompoundDrawablesWithIntrinsicBounds(
                AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_check_circle_blue_24dp)
                , null, null, null);

        mPresenter.connectedToDevice();
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
        mPresenter.onConnectionError();
    }

    @Override
    public void onDeviceNotSupported(@NonNull BluetoothDevice device) {
        Log.d(TAG, "onDeviceNotSupported: ");
    }

    @Override
    public void updateBodySensorPosition(BluetoothDevice device, Data data) {
        Integer value = data.getIntValue(Data.FORMAT_UINT8,0);
        if(value != null)
            mPresenter.onSensorPositionDataReceived(value);
    }

    @Override
    public void updateHeartRateData(BluetoothDevice device, Data data) {
        Log.d(TAG, "onHeartRateDataReceived: "+ data.getIntValue(Data.FORMAT_UINT8,1)+"v "+data.getIntValue(Data.FORMAT_UINT8,0));

        Integer value = data.getIntValue(Data.FORMAT_UINT8,1);
        if(value != null)
            mPresenter.onHeartRateDataReceived(value);
    }
}
