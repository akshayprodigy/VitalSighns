package com.augmentingtechs.vitalsigns;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
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

public class BloodPressureResult extends AppCompatActivity {

    private String user, Date;
    private Constants constants;
    int SP, DP;
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    Date today = Calendar.getInstance().getTime();
    JSONObject BPData;
    JSONArray BPArray;
    private static PowerManager.WakeLock wakeLock = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure_result);

        Date = df.format(today);
        TextView TBP = this.findViewById(R.id.BPT);
        ImageButton SBP = this.findViewById(R.id.SendBP);

        constants = new Constants();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            SP = bundle.getInt("SP");
            DP = bundle.getInt("DP");
            user = bundle.getString("Usr");
            TBP.setText(SP + " / " + DP);

            BPData = new JSONObject();
            BPArray = new JSONArray();
            try {
                BPData.put("time", Date);
                BPData.put("type", "Blood Pressure");
                BPData.put("result", SP + "/" + DP);

                BPArray.put(BPData);
                writeData(BPArray.toString(), this);
            } catch (Exception error) {
                error.printStackTrace();
            }
        }

        SBP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Health Watcher");
                i.putExtra(Intent.EXTRA_TEXT, user + "'s Blood Pressure " + "\n" + " at " + Date + " is :    " + SP + " / " + DP);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(BloodPressureResult.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
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
//        Intent i = new Intent(BloodPressureResult.this, Primary.class);
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
