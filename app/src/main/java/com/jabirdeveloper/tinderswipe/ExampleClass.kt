package com.jabirdeveloper.tinderswipe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class ExampleClass : AppCompatDialogFragment() {
    var listener: ExampleClassListener? = null
    var radio1 : RadioButton? = null
    var radio2 : RadioButton? = null
    var image1 : ImageView? = null
    var radioGroup1 : RadioGroup? = null
    var question:String = ""
    var choice:String = ""
    private lateinit var functions: FirebaseFunctions

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //getdata()
        val builder = AlertDialog.Builder(activity)
        var lay = getActivity()!!.getLayoutInflater()
        val view: View = lay.inflate(R.layout.question_dialog, null)
        val wm: DisplayMetrics? = resources.displayMetrics
        var height: Int = wm!!.heightPixels
        functions = Firebase.functions
        var width: Int = wm!!.widthPixels
        //image1!!.layoutParams.height = ((height*.3).toInt())
        //image1!!.layoutParams.width = ((width*.8).toInt())
        //image1!!.requestLayout()


        Log.d("Check_IsCheck","$width , $height")
        builder.setView(view)
                .setTitle("คำถาม")
                .setNegativeButton("cancel",object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                    }

                })
                .setPositiveButton("ok",object :DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        val check:Int
                        if (radio1!!.isChecked) {
                            Log.d("Check_IsCheck", "p1 isChecked")
                            check = 1
                        }else if(radio2!!.isChecked){
                            Log.d("Check_IsCheck", "p2 isChecked")
                            check = 2
                        }else{
                            Log.d("Check_IsCheck", "p3 isChecked")
                            check = 3
                        }
                        //listener!!.applyTexts(check)
                    }

                })
        radio1 = view.findViewById(R.id.radioButton_QA1)
        radio2 = view.findViewById(R.id.radioButton_QA2)
        radioGroup1 = view.findViewById(R.id.radioGroup_QA)
        return builder.create()
    }
    private fun getdata(): Task<String> {
        val data = hashMapOf(
                "questions" to question,
                "choice" to choice
        )
        return functions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then result will throw an Exception which will be
                    // propagated down.
                    val result = task.result?.data as String
                    result
                }
    }
    override fun onAttach(context: Context) {
        try {
            listener = context as ExampleClassListener;
        }catch (e : Exception){
            Log.d("error",e.toString())
        }
        super.onAttach(context)
    }
      interface ExampleClassListener {
        fun applyTexts(choice :Int)
      }
}