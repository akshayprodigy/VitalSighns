package com.augmentingtechs.vitalsigns;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.augmentingtechs.vitalsigns.healthwatcher.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RespirationResult extends AppCompatActivity {

    private String user, Date;
    private Constants constants;
    int RR;
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    Date today = Calendar.getInstance().getTime();
    JSONObject RRData;
    JSONArray RRArray;
    private static PowerManager.WakeLock wakeLock = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiration_result_new);

        Date = df.format(today);
        TextView RRR = this.findViewById(R.id.RRR);
        Button SRR = this.findViewById(R.id.SendRR);

        constants = new Constants();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            RR = bundle.getInt("bpm");
            user = bundle.getString("Usr");
            RRR.setText(String.valueOf(RR));

            RRData = new JSONObject();
            String data = readData(this);
            try {
                if (!data.equals("")) {
                    RRArray = new JSONArray(data);
                } else {
                    RRArray = new JSONArray();
                }

                RRData.put("time", Date);
                RRData.put("type", "Blood Pressure");
                RRData.put("result", RR);

                RRArray.put(RRData);
                writeData(RRArray.toString(), this);
            } catch (Exception error) {
                error.printStackTrace();
            }
        }

        SRR.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Health Watcher");
            i.putExtra(Intent.EXTRA_TEXT, user + "'s Respiration Rate " + "\n" + " at " + Date + " is :  " + RR);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(RespirationResult.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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

//        Intent i = new Intent(RespirationResult.this, Primary.class);
//        i.putExtra("Usr", user);
//        startActivity(i);
//        finish();
        super.onBackPressed();

    }

    private void writeData(String data, Context context) {
        try {
            OutputStreamWriter writer =
                    new OutputStreamWriter(context.openFileOutput(constants.getContentNAME(), Context.MODE_PRIVATE));
            writer.write(data);
            writer.close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private String readData(Context context) {
        String returnDATA = "";

        try {
            InputStream stream = context.openFileInput(constants.getContentNAME());
            if (stream != null) {
                InputStreamReader reader =
                        new InputStreamReader(stream);
                BufferedReader bufferedReader =
                        new BufferedReader(reader);
                String receiveString;
                StringBuilder builder =
                        new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    builder.append(receiveString);
                }

                stream.close();
                returnDATA = builder.toString();
            }
        } catch (Exception error) {
            error.printStackTrace();
        }

        return returnDATA;
    }
}
