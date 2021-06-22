package com.augmentingtechs.vitalsigns;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.augmentingtechs.vitalsigns.healthwatcher.R;

public class StartVitalSigns extends AppCompatActivity {
    private String user;
    private int p;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_vital_signs_new);

        prefs = getSharedPreferences("vital-prefs", Context.MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("Usr");
            p = extras.getInt("Page");
        }

        Button VS = this.findViewById(R.id.StartVS);

        VS.setOnClickListener(v -> {

            editor = prefs.edit();
            editor.putBoolean("showTutorial", false);
            editor.apply();

            //switch is to decide which activity must be opened
            switch (p) {

                case 1: {
                    Intent i = new Intent(v.getContext(), HeartRateProcess.class);
                    i.putExtra("Usr", user);
                    startActivity(i);
                    finish();
                }
                break;

                case 2: {
                    Intent i = new Intent(v.getContext(), BloodPressureProcess.class);
                    i.putExtra("Usr", user);
                    startActivity(i);
                    finish();
                }
                break;

                case 3: {
                    Intent i = new Intent(v.getContext(), RespirationProcess.class);
                    i.putExtra("Usr", user);
                    startActivity(i);
                    finish();
                }
                break;

                case 4: {
                    Intent i = new Intent(v.getContext(), O2Process.class);
                    i.putExtra("Usr", user);
                    startActivity(i);
                    finish();
                }
                break;

                case 5: {
                    Intent i = new Intent(v.getContext(), VitalSignsProcess.class);
                    i.putExtra("Usr", user);
                    startActivity(i);
                    finish();
                }
                break;
            }

        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(StartVitalSigns.this, Primary.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }


}
