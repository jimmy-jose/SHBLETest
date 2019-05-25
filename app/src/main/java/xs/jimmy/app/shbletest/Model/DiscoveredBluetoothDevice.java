package xs.jimmy.app.shbletest.Model;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class DiscoveredBluetoothDevice {
    private final BluetoothDevice device;
    private ScanResult lastScanResult;
    private String name;
    private int rssi;
    private int highestRssi = -128;

    public DiscoveredBluetoothDevice(final ScanResult scanResult) {
        device = scanResult.getDevice();
        update(scanResult);
    }

    public BluetoothDevice getDevice() {
            return device;
        }

    public String getAddress() {
            return device.getAddress();
        }

    public String getName() {
            return name;
        }

    public int getRssi() {
            return rssi;
        }

    public ScanResult getScanResult() {
            return lastScanResult;
        }
    /**
    * Returns the highest recorded RSSI value during the scan.
    *
    * @return Highest RSSI value.
    */
    public int getHighestRssi() {
        return highestRssi;
    }

    /**
     * Updates the device values based on the scan result.
     *
     * @param scanResult the new received scan result.
     */
    public void update(final ScanResult scanResult) {
        lastScanResult = scanResult;
        name = scanResult.getScanRecord() != null ?
                scanResult.getScanRecord().getDeviceName() : null;
        int previousRssi = rssi;
        rssi = scanResult.getRssi();
        if (highestRssi < rssi)
            highestRssi = rssi;
    }

    public boolean matches(final ScanResult scanResult) {
        return device.getAddress().equals(scanResult.getDevice().getAddress());
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof DiscoveredBluetoothDevice) {
            final DiscoveredBluetoothDevice that = (DiscoveredBluetoothDevice) o;
            return device.getAddress().equals(that.device.getAddress());
        }
        return super.equals(o);
    }

}

