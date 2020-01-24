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

        String HOST_STR = hostText.getText().toString().trim();
        String API_KEY_STR = apiKeyText.getText().toString().trim();

        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        String PREFS_API_KEY = prefs.getString("ApiKeyString", "");
        String PREFS_HOST = prefs.getString("HostString", "");

        if(!PREFS_HOST.equals(""))
        {
            hostText.setText(PREFS_HOST);
        }
        else
        {
            if(!HOST_STR.equals("https://192.168.1.65/dl_chain/transactions.php"))
            {
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                editor.putString("HostString", HOST_STR);
                editor.apply();

                hostText.setText(HOST_STR);
            }
            else if(HOST_STR.equals("https://192.168.1.65/dl_chain/transactions.php"))
            {
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                editor.putString("HostString", "https://192.168.1.65/dl_chain/transactions.php");
                editor.apply();
            }
        }

        if(!PREFS_API_KEY.equals(""))
        {
            apiKeyText.setText(PREFS_API_KEY);
        }
        else
        {
            if(!API_KEY_STR.equals("9b4761e0bfa9a2f8e536b26f6fc7e409a9a4942f23a3fd45c33ae52b8b6ce366"))
            {
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                editor.putString("ApiKeyString", API_KEY_STR);
                editor.apply();

                apiKeyText.setText(API_KEY_STR);
            }
            else if(API_KEY_STR.equals("9b4761e0bfa9a2f8e536b26f6fc7e409a9a4942f23a3fd45c33ae52b8b6ce366"))
            {
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                editor.putString("ApiKeyString", "9b4761e0bfa9a2f8e536b26f6fc7e409a9a4942f23a3fd45c33ae52b8b6ce366");
                editor.apply();
            }
        }
    }

}
