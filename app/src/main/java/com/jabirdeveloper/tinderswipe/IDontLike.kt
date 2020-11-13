package com.jabirdeveloper.tinderswipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_i_dont_like.*
import kotlinx.android.synthetic.main.activity_i_dont_like.c1
import kotlinx.android.synthetic.main.activity_i_dont_like.c2
import kotlinx.android.synthetic.main.activity_i_dont_like.c3
import kotlinx.android.synthetic.main.activity_i_dont_like.c4
import kotlinx.android.synthetic.main.activity_i_dont_like.c5



class IDontLike : AppCompatActivity(),View.OnClickListener {
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_i_dont_like)
        button = findViewById(R.id.button_send)
        c1.setOnClickListener(this)
        c2.setOnClickListener(this)
        c3.setOnClickListener(this)
        c4.setOnClickListener(this)
        c5.setOnClickListener(this)
        c6.setOnClickListener(this)
    }
    private fun check(){
        when {
            c1.isChecked -> {
                button.isEnabled = true
            }
            c2.isChecked -> {
                button.isEnabled = true
            }
            c3.isChecked -> {
                button.isEnabled = true
            }
            c4.isChecked -> {
                button.isEnabled = true
            }
            c5.isChecked -> {
                button.isEnabled = true
            }
            else -> button.isEnabled = c6.isChecked
        }
    }
    override fun onClick(v: View?) {
        check()
    }
}