package xs.jimmy.app.shbletest;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import xs.jimmy.app.shbletest.Model.DiscoveredBluetoothDevice;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {
    private final Context mContext;
    private List<DiscoveredBluetoothDevice> mDevices = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(@NonNull final DiscoveredBluetoothDevice device);
    }

    DevicesAdapter(@NonNull final MainActivity activity,
                   @NonNull List<DiscoveredBluetoothDevice> mDevices) {
        mContext = activity;
        mOnItemClickListener = activity;
        setHasStableIds(true);
        this.mDevices.addAll(mDevices);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View layoutView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DiscoveredBluetoothDevice device = mDevices.get(position);
        final String deviceName = device.getName();

        if (!TextUtils.isEmpty(deviceName))
            holder.deviceName.setText(deviceName);
        else
            holder.deviceName.setText(R.string.unknown_device);
        holder.deviceAddress.setText(device.getAddress());
    }

    @Override
    public long getItemId(final int position) {
        return mDevices.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return mDevices != null ? mDevices.size() : 0;

    }

    void setmDevices(List<DiscoveredBluetoothDevice> mDevices) {
        this.mDevices.clear();
        this.mDevices.addAll(mDevices);
        notifyDataSetChanged();
    }

    final class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        Button mConnect;
        private ViewHolder(@NonNull final View view) {
            super(view);
            deviceName = view.findViewById(R.id.device_name);
            deviceAddress = view.findViewById(R.id.device_address);
            mConnect = view.findViewById(R.id.connectButton);
            mConnect.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mDevices.get(getAdapterPosition()));
                }
            });
        }
    }
}
