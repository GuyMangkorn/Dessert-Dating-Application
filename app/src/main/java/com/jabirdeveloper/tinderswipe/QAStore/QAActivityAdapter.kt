package com.jabirdeveloper.tinderswipe.QAStore

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.jabirdeveloper.tinderswipe.R

class QAActivityAdapter(private val context:Context, private val result:ArrayList<QAObject>,private val viewPager: ViewPager2) : RecyclerView.Adapter<QAActivityAdapter.Holder>() {

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val question:TextView = itemView.findViewById(R.id.questionQARegister)
         val confirmButton:Button = itemView.findViewById(R.id.registerQABtn)
         val dismissButton:Button = itemView.findViewById(R.id.registerQABtnCancel)

    }

    override fun getItemCount(): Int {
        return result.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.question.text = "${position+1}. ${result[position].questions}"
        holder.confirmButton.setOnClickListener {
            viewPager.setCurrentItem(++viewPager.currentItem, false)
        }
        when (position) {
            0 -> {
                holder.confirmButton.text = context.getString(R.string.next_QA)
                holder.dismissButton.text = context.getString(R.string.dismiss_label)
                holder.dismissButton.setOnClickListener {
                }
            }
            itemCount - 1 -> {
                holder.confirmButton.text = context.getString(R.string.ok_QA)
                holder.dismissButton.text = context.getString(R.string.previous_QA)
                holder.dismissButton.setOnClickListener {
                    viewPager.setCurrentItem(--viewPager.currentItem, false)
                }
                holder.confirmButton.setOnClickListener {

                }
            }
            else -> {
                holder.confirmButton.text = context.getString(R.string.next_QA)
                holder.dismissButton.text = context.getString(R.string.previous_QA)
                holder.dismissButton.setOnClickListener {
                    viewPager.setCurrentItem(--viewPager.currentItem, false)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = (context as Activity).layoutInflater
        return Holder(inflater.inflate(R.layout.item_question, parent, false))
    }

}