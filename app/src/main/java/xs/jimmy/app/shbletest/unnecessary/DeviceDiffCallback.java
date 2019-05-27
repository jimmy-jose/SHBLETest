package xs.jimmy.app.shbletest.unnecessary;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import xs.jimmy.app.shbletest.Model.DiscoveredBluetoothDevice;

public class DeviceDiffCallback extends DiffUtil.Callback {
    private final List<DiscoveredBluetoothDevice> oldList;
    private final List<DiscoveredBluetoothDevice> newList;

    DeviceDiffCallback(final List<DiscoveredBluetoothDevice> oldList,
                       final List<DiscoveredBluetoothDevice> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(final int oldItemPosition, final int newItemPosition) {
        return oldList.get(oldItemPosition).getAddress().equals(oldList.get(oldItemPosition).getAddress());
    }

    @Override
    public boolean areContentsTheSame(final int oldItemPosition, final int newItemPosition) {
        return oldList.get(oldItemPosition).getDevice().equals(newList.get(newItemPosition).getDevice());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

