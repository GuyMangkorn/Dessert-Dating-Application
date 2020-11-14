package com.jabirdeveloper.tinderswipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_i_dont_like.*
import kotlinx.android.synthetic.main.activity_i_dont_like.c1
import kotlinx.android.synthetic.main.activity_i_dont_like.c2
import kotlinx.android.synthetic.main.activity_i_dont_like.c3
import kotlinx.android.synthetic.main.activity_i_dont_like.c4
import kotlinx.android.synthetic.main.activity_i_dont_like.c5



class IDontLike : AppCompatActivity(),View.OnClickListener {
    private lateinit var button: Button
    private lateinit var dB: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_i_dont_like)
        button = findViewById(R.id.button_send)
        dB = FirebaseDatabase.getInstance().reference.child("CloseAccount").child("bad")
        c1.setOnClickListener(this)
        c2.setOnClickListener(this)
        c3.setOnClickListener(this)
        c4.setOnClickListener(this)
        c5.setOnClickListener(this)
        c6.setOnClickListener(this)
        button.setOnClickListener(this)
    }
    private fun check(){
        if (c1.isChecked) {
            button.isEnabled = true
        }
        else if (c2.isChecked) {
            button.isEnabled = true
        }
        else if (c3.isChecked) {
            button.isEnabled = true
        }
        else if (c4.isChecked) {
            button.isEnabled = true
        }
        else if (c5.isChecked) {
            button.isEnabled = true
        }
        else button.isEnabled = c6.isChecked
    }
    override fun onClick(v: View) {
        check()
        if(v == button){
            dB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val sendMap =  hashMapOf<String, Any>()
                    val map:Map<*,*> = snapshot.value as Map<*, *>
                    Log.d("map",map.toString())
                    if(c1.isChecked){
                        sendMap["notFound"]  = map["notFound"].toString().toInt() + 1
                    }
                    if(c2.isChecked){
                        sendMap["badApp"]  = map["badApp"].toString().toInt() + 1
                    }
                    if(c3.isChecked){
                        sendMap["badExp"]  = map["badExp"].toString().toInt() + 1
                    }
                    if(c4.isChecked){
                        sendMap["otherApp"]  = map["otherApp"].toString().toInt() + 1
                    }
                    if(c5.isChecked){
                        sendMap["notEnough"]  = map["notEnough"].toString().toInt() + 1
                    }
                    if(c6.isChecked){
                        sendMap["notMeet"]  = map["notMeet"].toString().toInt() + 1
                    }
                    dB.updateChildren(sendMap)

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}