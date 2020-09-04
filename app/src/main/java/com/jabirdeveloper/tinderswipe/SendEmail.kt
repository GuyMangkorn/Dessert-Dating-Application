package com.jabirdeveloper.tinderswipe


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SendEmail : AppCompatActivity() {
    private lateinit var email:TextView
    private lateinit var subject:TextView
    private lateinit var message:TextView
    private lateinit var button:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_email)
        email = findViewById<EditText>(R.id.email)
        subject = findViewById<EditText>(R.id.topic)
        message = findViewById<EditText>(R.id.textSendEmail)
        button = findViewById<Button>(R.id.button2)

        button.setOnClickListener { senEmail() }
    }
    private fun senEmail() {
        val mEmail: String = email.text.toString()
        val mSubject: String = subject.text.toString()
        val mMessage: String = message.text.toString()
        val javaMailAPI = JavaMailAPI(this, mEmail, mSubject, mMessage)
        javaMailAPI.execute()
    }
}