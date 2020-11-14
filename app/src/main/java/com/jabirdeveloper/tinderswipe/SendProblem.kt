package com.jabirdeveloper.tinderswipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class SendProblem : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var dB: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_problem)
        editText = findViewById(R.id.textSend)
        button = findViewById(R.id.button_send)
        dB = FirebaseDatabase.getInstance().reference.child("CloseAccount").child("other")
        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                Log.d("count",s.length.toString())
                button.isEnabled = s.length >= 10
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })
        button.setOnClickListener {
            val sendMap2 =  hashMapOf<String, Any>()
            sendMap2[editText.text.toString()]  = ServerValue.TIMESTAMP
            dB.updateChildren(sendMap2)
        }
    }
}