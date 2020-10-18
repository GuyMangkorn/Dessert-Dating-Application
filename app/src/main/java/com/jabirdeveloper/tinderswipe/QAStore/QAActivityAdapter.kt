package com.jabirdeveloper.tinderswipe.QAStore

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.jabirdeveloper.tinderswipe.R

class QAActivityAdapter(private val context:Context, private val result:ArrayList<QAObject>) : PagerAdapter() {
    override fun getCount(): Int {
        return result.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = layoutInflater.inflate(R.layout.item_question, container, false)
        val button = itemView.findViewById<Button>(R.id.registerQABtn)
        button.setOnClickListener{
            Log.d("TAGRegisterQABtn","Click")
        }
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}