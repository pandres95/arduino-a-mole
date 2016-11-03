package me.pandres95.arduinoamole.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import me.pandres95.arduinoamole.R;

public class DeviceListActivity extends AppCompatActivity {
    // EXTRA string to send on to mainactivity
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothAdapter btAdapter;
    private ArrayAdapter<String> pairedDevicesArrayAdapter;

    @BindView(R.id.list_paired_devices) ListView pairedListView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        ButterKnife.bind(this);

        pairedDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
    }

    @Override public void onResume() {
        super.onResume();

        if(!hasBluetoothAdapter()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
            return;
        }

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            pairedDevicesArrayAdapter.clear();
            for (BluetoothDevice device : pairedDevices) pairedDevicesArrayAdapter.add(
                    String.format(Locale.getDefault(),
                            "%s\n%s", device.getName(), device.getAddress()
                    )
            );
        } else {
            String noDevices = getString(R.string.label_no_paired_devices);
            pairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @OnItemClick(R.id.list_paired_devices)
    public void onDeviceSelected(TextView txtDevice) {
        String device = txtDevice.getText().toString();
        String address = device.substring(device.length() - 17);

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        setResult(RESULT_OK, resultIntent);
        finish();
    };

    boolean hasBluetoothAdapter() {
        return (btAdapter = BluetoothAdapter.getDefaultAdapter()) != null && btAdapter.isEnabled();
    }

}
