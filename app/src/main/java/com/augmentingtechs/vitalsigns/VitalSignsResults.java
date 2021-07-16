package com.augmentingtechs.vitalsigns;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.augmentingtechs.vitalsigns.healthwatcher.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

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

public class VitalSignsResults extends AppCompatActivity {

    private String user, Date;
    private Constants constants;
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    Date today = Calendar.getInstance().getTime();

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    JSONObject BPData, HRData, O2Data, RRData;
    JSONArray MAINArray;
    int VBP1, VBP2, VRR, VHR, VO2;
    private static PowerManager.WakeLock wakeLock = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_signs_results_new);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Date = df.format(today);
        TextView VSRR = this.findViewById(R.id.RRV);
        TextView VSBPS = this.findViewById(R.id.BP2V);
        TextView VSHR = this.findViewById(R.id.HRV);
        TextView VSO2 = this.findViewById(R.id.O2V);
        Button All = this.findViewById(R.id.SendAll);

        constants = new Constants();

        prefs = getSharedPreferences("vital-prefs", Context.MODE_PRIVATE);


        if(Utility.rewardCounter < 0)
            Utility.rewardCounter =0;
        editor = prefs.edit();
        editor.putInt("rewardCounter", Utility.rewardCounter);
        editor.apply();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            VRR = bundle.getInt("breath");
            VHR = bundle.getInt("bpm");
            VBP1 = bundle.getInt("SP");
            VBP2 = bundle.getInt("DP");
            VO2 = bundle.getInt("O2R");
            user = bundle.getString("Usr");
            VSRR.setText(String.valueOf(VRR));
            VSHR.setText(String.valueOf(VHR));
            VSBPS.setText(VBP1 + " / " + VBP2);
            VSO2.setText(String.valueOf(VO2));

            BPData = new JSONObject();
            HRData = new JSONObject();
            O2Data = new JSONObject();
            RRData = new JSONObject();
            String data = readData(this);

            try {
                if (!data.equals("")) {
                    MAINArray = new JSONArray(data);
                } else {
                    MAINArray = new JSONArray();
                }

                BPData.put("time", Date);
                BPData.put("type", "Blood Pressure");
                BPData.put("result", VBP1 + "/" + VBP2);

                HRData.put("time", Date);
                HRData.put("type", "Heart Rate");
                HRData.put("result", VHR);

                O2Data.put("time", Date);
                O2Data.put("type", "Oxygen Saturation");
                O2Data.put("result", VO2);

                RRData.put("time", Date);
                RRData.put("type", "Respiration Rate");
                RRData.put("result", VRR);

                MAINArray.put(BPData);
                MAINArray.put(HRData);
                MAINArray.put(O2Data);
                MAINArray.put(RRData);
                writeData(MAINArray.toString(), this);
            } catch (Exception error) {
                error.printStackTrace();
            }
        }

        All.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Health Watcher");
            i.putExtra(Intent.EXTRA_TEXT, user + "'s new measuerment " + "\n" + " at " + Date + " are :" + "\n" + "Heart Rate = " + VHR + "\n" + "Blood Pressure = " + VBP1 + " / " + VBP2 + "\n" + "Respiration Rate = " + VRR + "\n" + "Oxygen Saturation = " + VO2);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(VitalSignsResults.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        });
// WakeLock Initialization : Forces the phone to stay On
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Vital sign: DoNotDimScreen");
        Utility.allTestCounter--;
        if(Utility.allTestCounter == 0){
            ShowInterstitialAds();
        }
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
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
////        finish();
////        Intent i = new Intent(VitalSignsResults.this, Primary.class);
////        i.putExtra("Usr", user);
////        //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        startActivity(i);
//
//    }

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

    private InterstitialAd mInterstitialAd;
    public void ShowInterstitialAds(){
        Utility.allTestCounter++;
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,Utility.InterstitialAdUnit, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(Utility.TAG, "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                                Utility.allTestCounter = Utility.alltestRefill;
                            }
                        });

                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(VitalSignsResults.this);
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(Utility.TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }
}
