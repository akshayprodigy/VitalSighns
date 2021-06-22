package com.augmentingtechs.vitalsigns;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.augmentingtechs.vitalsigns.healthwatcher.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class Primary extends AppCompatActivity implements OnUserEarnedRewardListener {

    private String user;
    private int p;

    private int singleAdCounter = 0;
    private int allAdCounter = 0;
    private int rewardCounter = 0;

    private AdLoader singleLoader;

    private RewardedInterstitialAd rewardedAd;

    private Constants constants;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_new);

        constants = new Constants();

        prefs = getSharedPreferences("vital-prefs", Context.MODE_PRIVATE);
        askPermission();

        MobileAds.initialize(this, initializationStatus -> {
            initAds();
            loadRewardAd();
        });

        RelativeLayout HeartRate = this.findViewById(R.id.HR);
        RelativeLayout BloodPressure = this.findViewById(R.id.BP);
        RelativeLayout Ox2 = this.findViewById(R.id.O2);
        RelativeLayout RRate = this.findViewById(R.id.RR);
        RelativeLayout VitalSigns = this.findViewById(R.id.VS);
        TextView history = this.findViewById(R.id.History);
        //ImageButton Abt = this.findViewById(R.id.About);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("Usr");
            //The key argument here must match that used in the other activity
        }

        /*Abt.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), AboutApp.class);
            startActivity(i);
            //finish();
        });*/


        //Every Test Button sends the username + the test number, to go to the wanted test after the instructions activity
        HeartRate.setOnClickListener(v -> {
            rewardCounter = prefs.getInt("rewardCounter", 5);
            singleAdCounter++;
            rewardCounter--;
            setRewardPrefs(rewardCounter);
            if (rewardCounter == 0) {
                rewardedAd.show(Primary.this, Primary.this);
            } else if (singleAdCounter % constants.getSingleAdInterval() == 0) {
                singleLoader.loadAd(new AdRequest.Builder().build());
            } else {
                boolean showTutorial = prefs.getBoolean("showTutorial", true);
                if(showTutorial){
                    p = 1;
                    Intent i = new Intent(v.getContext(), StartVitalSigns.class);
                    i.putExtra("Usr", user);
                    i.putExtra("Page", p);
                    startActivity(i);
                }else {
                    Intent i = new Intent(v.getContext(), HeartRateProcess.class);
                    i.putExtra("Usr", user);
                    startActivity(i);
                }

//                p = 1;
//                Intent i = new Intent(v.getContext(), StartVitalSigns.class);
//                i.putExtra("Usr", user);
//                i.putExtra("Page", p);
//                startActivity(i);
                //finish();
            }
        });

        BloodPressure.setOnClickListener(v -> {
            rewardCounter = prefs.getInt("rewardCounter", 5);
            singleAdCounter++;
            rewardCounter--;
            setRewardPrefs(rewardCounter);
            if (rewardCounter == 0) {
                rewardedAd.show(Primary.this, Primary.this);
            } else if (singleAdCounter % constants.getSingleAdInterval() == 0) {
                singleLoader.loadAd(new AdRequest.Builder().build());
            } else {
                boolean showTutorial = prefs.getBoolean("showTutorial", true);
                if(showTutorial){
                    p = 2;
                    Intent i = new Intent(v.getContext(), StartVitalSigns.class);
                    i.putExtra("Usr", user);
                    i.putExtra("Page", p);
                    startActivity(i);
                }else {
                    Intent i = new Intent(v.getContext(), BloodPressureProcess.class);
                    i.putExtra("Usr", user);
                    startActivity(i);
                }
//                p = 2;
//                Intent i = new Intent(v.getContext(), StartVitalSigns.class);
//                i.putExtra("Usr", user);
//                i.putExtra("Page", p);
//                startActivity(i);
                //finish();
            }
        });

        RRate.setOnClickListener(v -> {
            rewardCounter = prefs.getInt("rewardCounter", 5);
            singleAdCounter++;
            rewardCounter--;
            setRewardPrefs(rewardCounter);
            if (rewardCounter == 0) {
                rewardedAd.show(Primary.this, Primary.this);
            } else if (singleAdCounter % constants.getSingleAdInterval() == 0) {
                singleLoader.loadAd(new AdRequest.Builder().build());
            } else {
                boolean showTutorial = prefs.getBoolean("showTutorial", true);
                if(showTutorial){
                    p = 3;
                    Intent i = new Intent(v.getContext(), StartVitalSigns.class);
                    i.putExtra("Usr", user);
                    i.putExtra("Page", p);
                    startActivity(i);
                }else {
                    Intent i = new Intent(v.getContext(), RespirationProcess.class);
                    i.putExtra("Usr", user);
                    startActivity(i);
                }
//                p = 3;
//                Intent i = new Intent(v.getContext(), StartVitalSigns.class);
//                i.putExtra("Usr", user);
//                i.putExtra("Page", p);
//                startActivity(i);
                //finish();
            }
        });

        Ox2.setOnClickListener(v -> {
            rewardCounter = prefs.getInt("rewardCounter", 5);
            singleAdCounter++;
            rewardCounter--;
            setRewardPrefs(rewardCounter);
            if (rewardCounter == 0) {
                rewardedAd.show(Primary.this, Primary.this);
            } else if (singleAdCounter % constants.getSingleAdInterval() == 0) {
                singleLoader.loadAd(new AdRequest.Builder().build());
            } else {
                boolean showTutorial = prefs.getBoolean("showTutorial", true);
                if(showTutorial){
                    p = 4;
                    Intent i = new Intent(v.getContext(), StartVitalSigns.class);
                    i.putExtra("Usr", user);
                    i.putExtra("Page", p);
                    startActivity(i);
                }else {
                    Intent i = new Intent(v.getContext(), O2Process.class);
                    i.putExtra("Usr", user);
                    startActivity(i);
                }
//                p = 4;
//                Intent i = new Intent(v.getContext(), StartVitalSigns.class);
//                i.putExtra("Usr", user);
//                i.putExtra("Page", p);
//                startActivity(i);
                //finish();
            }
        });

        VitalSigns.setOnClickListener(v -> {
            rewardCounter = prefs.getInt("rewardCounter", 5);
            allAdCounter++;
            rewardCounter--;
            setRewardPrefs(rewardCounter);
            if (rewardCounter == 0) {
                rewardedAd.show(Primary.this, Primary.this);
            } else if (allAdCounter % constants.getAllAdInterval() == 0) {
                singleLoader.loadAd(new AdRequest.Builder().build());
            } else {

                boolean showTutorial = prefs.getBoolean("showTutorial", true);
                if(showTutorial){
                    p = 5;
                    Intent i = new Intent(v.getContext(), StartVitalSigns.class);
                    i.putExtra("Usr", user);
                    i.putExtra("Page", p);
                    startActivity(i);
                }else {
                    Intent i = new Intent(v.getContext(), VitalSignsProcess.class);
                    i.putExtra("Usr", user);
                    startActivity(i);
                }
//                p = 5;
//
//                Intent i = new Intent(v.getContext(), StartVitalSigns.class);
//                i.putExtra("Usr", user);
//                i.putExtra("Page", p);
//                startActivity(i);
                //finish();
            }
        });

        history.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), History.class);
            i.putExtra("Usr", user);
            startActivity(i);
        });
    }

    private void askPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            // Doing Nothing
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        Toast.makeText(Primary.this, "Permission Needed", Toast.LENGTH_LONG).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void setRewardPrefs(int counter) {
        editor = prefs.edit();
        editor.putInt("rewardCounter", counter);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {

                    Primary.super.onBackPressed();
                    Primary.this.finish();
                    System.exit(0);
                }).create().show();
    }

    private void initAds() {
        singleLoader = new AdLoader.Builder(this, "ca-app-pub-8024886885221085/8205449345")
                .forNativeAd(NativeAd -> {
                    // Show the ad.
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
    }

    private void loadRewardAd() {
        RewardedInterstitialAd.load(Primary.this, "ca-app-pub-8024886885221085/8862202697",
                new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        rewardedAd = ad;
                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            /** Called when the ad failed to show full screen content. */
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Doing Nothing
                            }

                            /** Called when ad showed the full screen content. */
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Doing Nothing
                            }

                            /** Called when full screen content is dismissed. */
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Doing Nothing
                            }
                        });
                    }
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        loadRewardAd();
                    }
                });
    }

    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        setRewardPrefs(5);
        loadRewardAd();
    }

}

