package com.augmentingtechs.vitalsigns;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.augmentingtechs.vitalsigns.healthwatcher.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class O2Result extends AppCompatActivity {

    private String user, Date;
    private Constants constants;
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    Date today = Calendar.getInstance().getTime();
    JSONObject O2Data;
    JSONArray O2Array;
    int O2;
    private static PowerManager.WakeLock wakeLock = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o2_result);

        Date = df.format(today);
        TextView RO2 = this.findViewById(R.id.O2R);
        ImageButton SO2 = this.findViewById(R.id.SendO2);

        constants = new Constants();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            O2 = bundle.getInt("O2R");
            user = bundle.getString("Usr");
            RO2.setText(String.valueOf(O2));

            O2Data = new JSONObject();
            O2Array = new JSONArray();
            try {
                O2Data.put("time", Date);
                O2Data.put("type", "Blood Pressure");
                O2Data.put("result", O2);

                O2Array.put(O2Data);
                writeData(O2Array.toString(), this);
            } catch (Exception error) {
                error.printStackTrace();
            }
        }

        SO2.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Health Watcher");
            i.putExtra(Intent.EXTRA_TEXT, user + "'s Oxygen Saturation Level " + "\n" + " at " + Date + " is :   " + O2);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(O2Result.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        });
        // WakeLock Initialization : Forces the phone to stay On
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Vital sign: DoNotDimScreen");
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    @Override
    public void onBackPressed() {

//        Intent i = new Intent(O2Result.this, Primary.class);
//        i.putExtra("Usr", user);
//        startActivity(i);
//        finish();
        super.onBackPressed();

    }

    private void writeData(String data, Context context) {
        try {
            String dataFILE =
                    constants.getContentDIR() + File.separator + constants.getContentNAME() + ".txt";
            OutputStreamWriter writer =
                    new OutputStreamWriter(context.openFileOutput(dataFILE, Context.MODE_PRIVATE));
            writer.write(data);
            writer.close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
