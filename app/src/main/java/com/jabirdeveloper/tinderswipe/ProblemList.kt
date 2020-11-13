package com.jabirdeveloper.tinderswipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_problem_list.*

class ProblemList : AppCompatActivity(),View.OnClickListener {
    private lateinit var editText: EditText
    private lateinit var button: Button
    private var st = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem_list)
        editText = findViewById(R.id.textSend)
        button = findViewById(R.id.button_send)
        c1.setOnClickListener(this)
        c2.setOnClickListener(this)
        c3.setOnClickListener(this)
        c4.setOnClickListener(this)
        c5.setOnClickListener(this)

        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                Log.d("count",s.length.toString())
                st = s.isNotEmpty()
                check()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })
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
            st -> {button.isEnabled = true}
            else -> button.isEnabled = c5.isChecked
        }
    }

    override fun onClick(v: View) {
        check()
    }
}