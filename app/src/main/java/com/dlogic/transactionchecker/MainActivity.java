package com.dlogic.transactionchecker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dlogic.uFCoder;
import com.dlogic.uFCoderHelper;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("uFCoder"); //Load uFCoder library
    }

    uFCoderHelper UFC;
    uFCoder uFCoder;

    public boolean LOOP = false;
    private final int STATUS_IS_OK = 0;
    ProgressDialog dialog;
    private Handler handler;

    public static String CERTIFICATE_STR = "";
    public static String PUBLIC_KEY_STR = "";
    public static String DATE_FROM = "";
    public static String DATE_TO = "";
    public static String LIMIT = "";
    public static int ORDER = 0;

    EditText limitET;
    TableRow tableRow;

    String serverUrl = "";

    @Override
    protected void onPause() {
        UFC.callOnPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        UFC.callOnResume(this);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UFC = uFCoderHelper.getInstance(this);
        uFCoder = new uFCoder(getApplicationContext());

        tableRow = findViewById(R.id.tableRowID);

        final View dialogView = View.inflate(MainActivity.this, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        final DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
        final TimePicker timePicker = dialogView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(Boolean.TRUE);

        ImageView dateFromET = findViewById(R.id.dateFromID);
        ImageView dateToET = findViewById(R.id.dateToID);

        dateFromET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DATE_FROM = datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth() + " " + timePicker.getHour() + ":" + timePicker.getMinute() + ":00";

                        alertDialog.dismiss();
                    }});
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });

        dateToET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DATE_TO = datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth() + " " + timePicker.getHour() + ":" + timePicker.getMinute() + ":00";

                        alertDialog.dismiss();
                    }});
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });

        final Spinner orderSpinner = (Spinner) findViewById(R.id.orderSpinnerID);
        ArrayAdapter<CharSequence> spnLightAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.order_mode,
                R.layout.dl_spinner_textview);
        spnLightAdapter.setDropDownViewResource(R.layout.dl_spinner_textview);
        orderSpinner.setAdapter(spnLightAdapter);
        orderSpinner.setSelection(0);
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ORDER = pos;
            }

            public void onNothingSelected(AdapterView<?> parent) { }
        });

        limitET = findViewById(R.id.limitID);

        DATE_TO = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        DATE_FROM = DATE_TO.substring(0, DATE_TO.indexOf(" ")) + " 00:00:00";

        Button btnSend = findViewById(R.id.btnSendID);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClearTable(R.id.transactionTableID);
                tableRow.setVisibility(View.GONE);

                NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());

                if(nfcAdapter != null)
                {
                    if (!nfcAdapter.isEnabled())
                    {
                        Toast.makeText(getApplicationContext(), "Please enable NFC", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "This phone does not have NFC", Toast.LENGTH_SHORT).show();
                }

                int status = uFCoder.ReaderOpenEx(5, "", 0, "");

                if(status != 0)
                {
                    Toast.makeText(getApplicationContext(), uFCoder.UFR_Status2String(status), Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
                serverUrl = prefs.getString("HostString", "");

                Log.e("URL", serverUrl);

                if(serverUrl.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Host is not defined", Toast.LENGTH_SHORT).show();
                    return;
                }

                LOOP = true;

                dialog = ProgressDialog.show(MainActivity.this, "",
                        "Tap DL Signer card on the phone...", true);
            }
        });

        handler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                if(msg.what == STATUS_IS_OK)
                {
                    LOOP = false;

                    new Thread() {
                        public void run() {
                            int status = GetCertificate();

                            if(status == 0)
                            {
                                if(!CERTIFICATE_STR.equals("") && !PUBLIC_KEY_STR.equals(""))
                                {
                                    getData();
                                }
                                else
                                {
                                    dialog.dismiss();
                                    ShowStatus("Cannot get certificate, card was lost, please try again");
                                }
                            }
                            else
                            {
                                dialog.dismiss();
                                ShowStatus("Cannot get certificate, status is " + uFCoder.UFR_Status2String(status));
                            }
                        }
                    }.start();
                }
            }
        };


        new Thread() {
            public void run() {

                while(true)
                {
                    if(LOOP)
                    {
                        byte[] cmd = {0x00, 0x00};
                        byte[] resp = new byte[100];
                        int[] respLen = new int[1];
                        int thread_status = uFCoder.APDUPlainTransceive(cmd, 2, resp, respLen);

                        switch (thread_status) {
                            case 0:
                                handler.obtainMessage(STATUS_IS_OK, -1, -1)
                                        .sendToTarget();
                                break;
                        }

                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }.start();

        ImageView settingsIcon = findViewById(R.id.iconSettingsId);
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        TextView settingsText = findViewById(R.id.txtSettingsId);
        settingsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        System.setProperty("http.keepAlive", "true");
    }

    public int GetCertificate()
    {
        int[] status = new int[1];
        byte[] cert = uFCoder.GetCertificateFromCard(status, (byte)1, (byte)0);

        if(status[0] == 0)
        {
            if(cert != null)
            {
                CERTIFICATE_STR = bytesToHex(cert);
            }
            else
            {
                ShowStatus("Cannot get certificate, probably card was lost, please try again");
                CERTIFICATE_STR = "";
                PUBLIC_KEY_STR = "";
            }
        }
        else
        {
            CERTIFICATE_STR = "";
            PUBLIC_KEY_STR = "";
            return status[0];
        }

        byte[] pub_key = uFCoder.GetECPublicKeyFromCard(status);

        if(status[0] == 0)
        {
            if(pub_key != null)
            {
                PUBLIC_KEY_STR = bytesToHex(pub_key);
            }
            else
            {
                ShowStatus("Cannot get public key, probably card was lost, please try again");
                CERTIFICATE_STR = "";
                PUBLIC_KEY_STR = "";
            }
        }
        else
        {
            CERTIFICATE_STR = "";
            PUBLIC_KEY_STR = "";
        }

        return status[0];
    }

    public static String bytesToHex(byte[] byteArray) {
        StringBuilder sBuilder = new StringBuilder(byteArray.length * 2);
        for(byte b: byteArray)
            sBuilder.append(String.format("%02x", b & 0xff));
        return sBuilder.toString();
    }

    public void ShowStatus(final String status)
    {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getData()
    {
       SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
       serverUrl = prefs.getString("HostString", "");

       StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();

                String[] statusResp = response.split(";");

                if(statusResp.length == 2)
                {
                    if(statusResp[0].trim().equals("0"))
                    {
                        tableRow.setVisibility(View.VISIBLE);

                        String[] transactions = statusResp[1].split(Pattern.quote("||"));

                        for(int i = 0; i < transactions.length; i++)
                        {
                            transactions[i] = transactions[i].trim();

                            String amountStr = transactions[i].substring(0, transactions[i].indexOf('|'));
                            String dateStr = transactions[i].substring(transactions[i].indexOf('|') + 1);

                            AddRow(amountStr, dateStr);
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), statusResp[1], Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(serverUrl.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Host is not defined", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
                }

                try {
                    Log.e("SendingError", error.getMessage());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
                String apiKeyStr = prefs.getString("ApiKeyString", "");

                //9B4761E0BFA9A2F8E536B26F6FC7E409A9A4942F23A3FD45C33AE52B8B6CE366

                params.put("api_key", apiKeyStr);
                params.put("certificate", CERTIFICATE_STR);
                params.put("public_key", PUBLIC_KEY_STR);
                params.put("date_from", DATE_FROM);
                params.put("date_to", DATE_TO);

                if(ORDER == 0)
                {
                    params.put("order", "ascending");
                }
                else
                {
                    params.put("order", "descending");
                }

                params.put("limit", limitET.getText().toString());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    public void onStop () {
        LOOP = false;
        super.onStop();
    }

    public void AddRow(String amount, String date)
    {
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);

            TableLayout table = findViewById(R.id.transactionTableID);

            TableRow row = new TableRow(MainActivity.this);

            TextView tv = new TextView(MainActivity.this);
            tv.setLayoutParams(params);
            tv.setBackgroundResource(R.drawable.table_divider);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(20);
            tv.setText(amount);

            row.addView(tv);

            TextView tv1 = new TextView(MainActivity.this);
            tv1.setLayoutParams(params);
            tv1.setBackgroundResource(R.drawable.table_divider);
            tv1.setTextColor(Color.BLACK);
            tv1.setTextSize(20);
            tv1.setText(date);

            row.addView(tv1);

            table.addView(row);
    }

        public void ClearTable(int tableID)
        {
            TableLayout table = findViewById(tableID);
            table.removeAllViews();
        }
}
