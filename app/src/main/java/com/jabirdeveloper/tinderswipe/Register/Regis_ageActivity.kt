package com.jabirdeveloper.tinderswipe.Register

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.jabirdeveloper.tinderswipe.R
import com.jabirdeveloper.tinderswipe.Register.Regis_ageActivity
import com.tapadoo.alerter.Alerter
import java.util.*

class Regis_ageActivity : AppCompatActivity() {
    private var Age: String? = null
    private var age = 0
    private var y = 0
    private var m = 0
    private var d = 0
    private lateinit var button:Button
    private lateinit var toolbar:Toolbar
    private lateinit var calendar:Calendar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocal()
        setContentView(R.layout.activity_regis_age)
        button = findViewById(R.id.button11)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.registered)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        calendar = Calendar.getInstance()
        datePicker.maxDate = calendar.timeInMillis
        calendar.timeInMillis = System.currentTimeMillis()
        y = calendar.get(Calendar.YEAR)
        m = calendar.get(Calendar.MONTH)
        d = calendar.get(Calendar.DAY_OF_MONTH)
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { _, year, month, dayOfMonth ->
            y = year
            m = month
            d = dayOfMonth
        }
        button.setOnClickListener(View.OnClickListener {
            age = getAge(y, m, d)
            if (age >= 18) {
                Alerter.hide()
                val intent = Intent(this@Regis_ageActivity, Regis_target_Acivity::class.java)
                intent.putExtra("Sex", getIntent().getStringExtra("Sex"))
                intent.putExtra("Type", getIntent().getStringExtra("Type"))
                intent.putExtra("X", getIntent().getStringExtra("X").toDouble())
                intent.putExtra("Y", getIntent().getStringExtra("Y").toDouble())
                intent.putExtra("Name", getIntent().getStringExtra("Name"))
                intent.putExtra("Age", age)
                intent.putExtra("email", getIntent().getStringExtra("email"))
                intent.putExtra("password", getIntent().getStringExtra("password"))
                startActivity(intent)
                return@OnClickListener
            } else {
                Alerter.create(this@Regis_ageActivity)
                        .setTitle(getString(R.string.Noti))
                        .setText(getString(R.string.up18))
                        .setBackgroundColorRes(R.color.c2)
                        .show()
            }
        })
    }

    private fun setLocal(lang: String?) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.setLocale(locale)
        baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", lang)
        editor.apply()
        Log.d("My", lang)
    }

    private fun loadLocal() {
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val langure = preferences.getString("My_Lang", "")
        Log.d("My2", langure)
        setLocal(langure)
    }

    private fun getAge(year: Int, month: Int, dayOfMonth: Int): Int {
        /*return Period.between(
                LocalDate.of(year, month, dayOfMonth),
                LocalDate.now()
        ).getYears();*/
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = dayOfMonth
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}