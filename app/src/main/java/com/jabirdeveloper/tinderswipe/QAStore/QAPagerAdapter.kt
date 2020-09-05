package com.jabirdeveloper.tinderswipe.QAStore

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.jabirdeveloper.tinderswipe.R

class QAPagerAdapter(val context: Context, val choice: ArrayList<QAObject>,val dialog: Dialog,val viewpager:ViewPager2) : RecyclerView.Adapter<QAPagerAdapter.Holder?>() {
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioGroupChoice:RadioGroup = itemView.findViewById(R.id.radioGroup_QA)
        val radioGroupChoiceWeight:RadioGroup = itemView.findViewById(R.id.radioGroup_QAWeight)
        val choice1:RadioButton = itemView.findViewById(R.id.radioButton_QA1)
        val choice2:RadioButton = itemView.findViewById(R.id.radioButton_QA2)
        val questions:TextView = itemView.findViewById(R.id.message_QA)
        val valPage:TextView = itemView.findViewById(R.id.page_QA)
        val confirmButton:TextView = itemView.findViewById(R.id.QA_confirm)
        val dismissButton:TextView = itemView.findViewById(R.id.QA_dismiss)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = (context as Activity).layoutInflater
        return Holder(inflater!!.inflate(R.layout.question_dialog,parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.valPage.hint = "${position+1} / $itemCount"
        holder.questions.text = choice[position].questions
        holder.choice1.text = choice[position].choice[0]
        holder.choice2.text = choice[position].choice[1]
        when(position){
            0 -> {  holder.confirmButton.text = context.getString(R.string.next_QA)
                    holder.dismissButton.text = context.getString(R.string.dismiss_label)
                    holder.dismissButton.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            itemCount-1 -> {
                holder.confirmButton.text = context.getString(R.string.ok_QA)
                holder.dismissButton.text = context.getString(R.string.previous_QA)
                holder.dismissButton.setOnClickListener{
                    viewpager.setCurrentItem(--viewpager.currentItem, false)
                }
            }
            else -> {
                holder.confirmButton.text = context.getString(R.string.next_QA)
                holder.dismissButton.text = context.getString(R.string.previous_QA)
                holder.dismissButton.setOnClickListener{
                    viewpager.setCurrentItem(--viewpager.currentItem, false)
                }
            }
        }
        holder.confirmButton.setOnClickListener {
            val chk1 = holder.radioGroupChoice.checkedRadioButtonId
            val chk2 = holder.radioGroupChoiceWeight.checkedRadioButtonId
            if (chk1 == -1 || chk2 == -1) {
                Toast.makeText(context,"กรุณาเลือกคำตอบและตอบให้ครบถ้วน",Toast.LENGTH_SHORT).show()
            } else {
                when (chk1) {
                    R.id.radioButton_QA1 -> Log.d("Check_IsCheck", "*1*")
                    R.id.radioButton_QA2 -> Log.d("Check_IsCheck", "*0*")
                }
                when (chk2) {
                    R.id.radioButton_QAWeight1 -> Log.d("Check_IsCheck", "1 point")
                    R.id.radioButton_QAWeight2 -> Log.d("Check_IsCheck", "10 points")
                    R.id.radioButton_QAWeight3 -> Log.d("Check_IsCheck", "100 points")
                    R.id.radioButton_QAWeight4 -> Log.d("Check_IsCheck", "150 points")
                    R.id.radioButton_QAWeight5 -> Log.d("Check_IsCheck", "250 points")
                }
                viewpager.setCurrentItem(++viewpager.currentItem, false)
                if (position==itemCount-1){
                    dialog.dismiss()
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return choice.size
    }
}