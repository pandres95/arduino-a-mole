package me.pandres95.arduinoamole.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

import me.pandres95.arduinoamole.R;
import me.pandres95.arduinoamole.activities.DeviceListActivity;

public abstract class BluetoothHandler extends AppCompatActivity
        implements BluetoothCOM.EventsListener {
    private static final int REQUEST_BT_ENABLE = 15;
    private static final int REQUEST_BT_PAIR = 16;
    private UUID BTMODULEUUID;

    private BluetoothCOM bluetoothCOM;
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private String address;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BTMODULEUUID = UUID.fromString(getString(R.string.bt_uuid));
    }

    @Override protected void onResume() {
        super.onResume();

        if(!hasBluetoothAdapter()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
            return;
        }

        if(address == null) {
            Intent getDevicesIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(getDevicesIntent, REQUEST_BT_PAIR);
            return;
        }

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) { onBluetoothException(e); }

        try {
            bluetoothCOM = new BluetoothCOM(this, btSocket);
            btSocket.connect();
            bluetoothCOM.initialize();
        } catch (IOException connectException) {
            onBluetoothException(connectException);

            try {
                btSocket.close();
                bluetoothCOM.close();
            } catch (IOException closeException) {
                onBluetoothException(closeException);
            }
        }

    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_BT_ENABLE: {
                onResume();
                return;
            }
            case REQUEST_BT_PAIR: {
                if(data.hasExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS))
                    address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                return;
            }
        }
    }

    @Override protected void onPause() {
        super.onPause();
        if(btSocket != null) try {
            btSocket.close();
            bluetoothCOM.close();
        } catch (IOException ex) { finish(); }
    }

    public BluetoothCOM getBluetoothCOM() {
        return bluetoothCOM;
    }

    boolean hasBluetoothAdapter() {
        return (btAdapter = BluetoothAdapter.getDefaultAdapter()) != null && btAdapter.isEnabled();
    }

    BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
}