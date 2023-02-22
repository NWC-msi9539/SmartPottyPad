package nwc.hardware.smartpottypad.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import nwc.hardware.smartpottypad.R;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";

    private EditText name;
    private EditText address;
    private EditText totalFloor;

    private Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}