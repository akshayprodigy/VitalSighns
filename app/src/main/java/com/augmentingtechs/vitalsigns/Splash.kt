package com.augmentingtechs.vitalsigns

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.augmentingtechs.vitalsigns.healthwatcher.R

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val i = Intent(this, Primary::class.java)
            startActivity(i)

            finish()
        }, 3000)
    }
}