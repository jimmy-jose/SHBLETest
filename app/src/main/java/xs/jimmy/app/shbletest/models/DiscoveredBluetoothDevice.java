package xs.jimmy.app.shbletest.models;

import android.bluetooth.BluetoothDevice;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class DiscoveredBluetoothDevice {
    private final BluetoothDevice device;
    private String name;


    public DiscoveredBluetoothDevice(final ScanResult scanResult) {
        device = scanResult.getDevice();
        name = scanResult.getScanRecord() != null ?
                scanResult.getScanRecord().getDeviceName() : null;
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

