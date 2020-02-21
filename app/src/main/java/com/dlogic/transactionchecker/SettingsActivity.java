package com.dlogic.transactionchecker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);


        final EditText hostText = findViewById(R.id.editTextHostID);
        hostText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                String hostStr = hostText.getText().toString();
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                editor.putString("HostString", hostStr);
                editor.apply();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        final EditText apiKeyText = findViewById(R.id.editTextApiKeyID);
        apiKeyText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                String apiKeyStr = apiKeyText.getText().toString();
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                editor.putString("ApiKeyString", apiKeyStr);
                editor.apply();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        Button btnClose = findViewById(R.id.btnCloseSettingsID);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        String PREFS_API_KEY = prefs.getString("ApiKeyString", "");
        String PREFS_HOST = prefs.getString("HostString", "");

        hostText.setText(PREFS_HOST);
        apiKeyText.setText(PREFS_API_KEY);
    }

}
