package com.jabirdeveloper.tinderswipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.google.android.material.card.MaterialCardView

class CloseAccount : AppCompatActivity(),View.OnClickListener {
    private lateinit var toolbar: Toolbar
    private lateinit var cardRe:MaterialCardView
    private lateinit var cardLove:MaterialCardView
    private lateinit var cardBad:MaterialCardView
    private lateinit var cardProblem:MaterialCardView
    private lateinit var cardOther:MaterialCardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_close_account)

        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Close Account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        cardBad = findViewById(R.id.close_bad)
        cardLove = findViewById(R.id.close_love)
        cardRe = findViewById(R.id.close_re)
        cardOther = findViewById(R.id.close_other)
        cardProblem = findViewById(R.id.close_problem)
        cardRe.setOnClickListener(this)
        cardLove.setOnClickListener(this)
        cardBad.setOnClickListener(this)
        cardProblem.setOnClickListener(this)
        cardOther.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if(v == cardRe){

        }
        if(v == cardLove){

        }
        if(v == cardBad){

        }
        if(v == cardProblem){
            val intent = Intent(applicationContext, ProblemList::class.java)
            startActivity(intent)
        }
        if(v == cardOther){
            val intent = Intent(applicationContext, SendProblem::class.java)
            startActivity(intent)
        }
    }
}