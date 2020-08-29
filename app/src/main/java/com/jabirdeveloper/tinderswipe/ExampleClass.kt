package com.jabirdeveloper.tinderswipe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase


class ExampleClass : AppCompatDialogFragment() {
    var listener: ExampleClassListener? = null
    var radio1 : RadioButton? = null
    var radio2 : RadioButton? = null
    var questionText : TextView? = null
    var confirmText : TextView? = null
    var dismissText : TextView? = null
    var radioGroup1 : RadioGroup? = null
    var question:String = ""
    var Choice:ArrayList<String>? = ArrayList()
    private lateinit var functions: FirebaseFunctions

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = Dialog(activity!!)
        var lay = getActivity()!!.getLayoutInflater()
        val view: View = lay.inflate(R.layout.question_dialog, null)
        val wm: DisplayMetrics? = resources.displayMetrics
        var height: Int = wm!!.heightPixels
        var width: Int = wm!!.widthPixels
        questionText = view.findViewById(R.id.message_QA)
        questionText!!.text = question
        radio1 = view.findViewById(R.id.radioButton_QA1)
        radio1!!.text = Choice!![0]
        radio2 = view.findViewById(R.id.radioButton_QA2)
        radio2!!.text = Choice!![1]
        radioGroup1 = view.findViewById(R.id.radioGroup_QA)
        //Log.d("Check_IsCheck", "$width , $height")
        builder.setContentView(view)
        builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        confirmText = view.findViewById(R.id.QA_confirm)
        dismissText = view.findViewById(R.id.QA_dismiss)
        confirmText!!.setOnClickListener {
            if(radioGroup1!!.checkedRadioButtonId == -1){

            }else{
                Log.d("Check_IsCheck" ,radioGroup1!!.checkedRadioButtonId.toString())
            }



            builder.dismiss() }
        dismissText!!.setOnClickListener { builder.dismiss() }
        //confirmText!!.setOnLongClickListener{}

        /*
         .setPositiveButton("ok", object : DialogInterface.OnClickListener {
             override fun onClick(p0: DialogInterface?, p1: Int) {
                 val check: Int
                 if (radio1!!.isChecked) {
                     Log.d("Check_IsCheck", "p1 isChecked")
                     check = 1
                 } else {
                     Log.d("Check_IsCheck", "p2 isChecked")
                     check = 2
                 }
                 //listener!!.applyTexts(check)
             }

         })*/
        builder.show()
        return builder
    }

     fun setData(question: String,Choice:ArrayList<String>){
         this.Choice = Choice;
        this.question = question;
    }
    override fun onAttach(context: Context) {
        try {
            listener = context as ExampleClassListener;
        }catch (e: Exception){
            Log.d("error", e.toString())
        }
        super.onAttach(context)
    }
      interface ExampleClassListener {
        fun applyTexts(choice: Int)
      }
}