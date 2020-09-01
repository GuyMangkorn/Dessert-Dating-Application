package com.jabirdeveloper.tinderswipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class BandUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_band_user)
        (findViewById<Button>(R.id.button_back)).setOnClickListener {

            startActivity(Intent(applicationContext,ChooseLoginRegistrationActivity::class.java))
            finish()
        }
    }
}