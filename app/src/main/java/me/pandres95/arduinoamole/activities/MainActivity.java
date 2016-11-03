package me.pandres95.arduinoamole.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.pandres95.arduinoamole.R;
import me.pandres95.arduinoamole.bluetooth.BluetoothHandler;

public class MainActivity extends BluetoothHandler {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.txtOutput) TextView textOutput;
    @BindViews({
            R.id.btnR0C0, R.id.btnR0C1, R.id.btnR0C2,
            R.id.btnR1C0, R.id.btnR1C1, R.id.btnR1C2,
            R.id.btnR2C0, R.id.btnR2C1, R.id.btnR2C2
    }) Button[] moleButtons;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @OnClick({
            R.id.btnR0C0, R.id.btnR0C1, R.id.btnR0C2,
            R.id.btnR1C0, R.id.btnR1C1, R.id.btnR1C2,
            R.id.btnR2C0, R.id.btnR2C1, R.id.btnR2C2
    }) public void moleClick(Button button) {
        String message = button.getTag().toString();

        textOutput.setText(message);
        getBluetoothCOM().send(message + "\n");
    }

    @Override public void onIncomingMessage(int what, String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override public void onBluetoothException(Exception exception) {
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
    }
}
