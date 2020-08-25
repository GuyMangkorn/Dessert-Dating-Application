package com.jabirdeveloper.tinderswipe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import java.lang.Exception

class ExampleClass : AppCompatDialogFragment() {
    var listener: ExampleClassListener? = null
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder =  AlertDialog.Builder(activity)
        var lay = getActivity()!!.getLayoutInflater()
        val view:View = lay.inflate(R.layout.question_dialog,null)
        builder.setView(view)
                .setTitle("คำถาม")
                .setNegativeButton("cancel",object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                    }

                })
                .setPositiveButton("ok",object :DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                    }

                })

        return builder.create()
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

      }
}