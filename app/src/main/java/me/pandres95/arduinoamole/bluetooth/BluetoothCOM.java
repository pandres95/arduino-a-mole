package me.pandres95.arduinoamole.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;


public class BluetoothCOM extends Thread {
    private static final String TAG = "BluetoothCOM";

    public interface EventsListener {
        void onIncomingMessage(int what, String message);
        void onBluetoothException(Exception exception);
    }

    private EventsListener listener;
    private Handler socketHandler;

    private boolean running = true;
    private int handlerState;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public BluetoothCOM(EventsListener listener, BluetoothSocket socket) {
        this(listener, socket, 0);
    }

    public BluetoothCOM(EventsListener listener, BluetoothSocket socket, int state) {
        this.listener = listener;
        this.socket = socket;
        this.handlerState = state;
    }

    public void initialize() throws IOException {
        socketHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, String.format(Locale.getDefault(),
                        "%d: %s", msg.what, msg.obj.toString()
                ));
                if (msg.what == handlerState)
                    listener.onIncomingMessage(msg.what, (String) msg.obj);
            }
        };
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public void send(String message) {
        byte[] msgBuffer = message.getBytes();
        try {
            outputStream.write(msgBuffer);
        } catch (IOException ex) { listener.onBluetoothException(ex); }
    }

    @Override public void run() {
        int bytes;
        byte[] buffer = new byte[256];

        while (running) {
            try {
                bytes = inputStream.read(buffer);
                socketHandler
                        .obtainMessage(handlerState, bytes, -1, new String(buffer, 0, bytes))
                        .sendToTarget();
            } catch (Exception ex) {
                listener.onBluetoothException(ex);
                break;
            }
        }
    }

    public void close() {
        this.running = false;
    }
}
