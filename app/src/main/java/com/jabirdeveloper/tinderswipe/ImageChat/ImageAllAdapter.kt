package com.jabirdeveloper.tinderswipe.ImageChat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jabirdeveloper.tinderswipe.R

class ImageAllAdapter(private val image_all: MutableList<ScreenObject?>?, private val context: Context) : RecyclerView.Adapter<ImageAllAdapter.Holder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_all_image, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.set(position)
    }

    override fun getItemCount(): Int {
        var size = image_all!!.size
        size = if (image_all.size % 3 == 0) {
            image_all.size / 3
        } else {
            image_all.size / 3 + 1
        }
        return size
    }

    inner class Holder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        private val m1: ImageView? = ItemView.findViewById(R.id.all_image_1)
        private val m2: ImageView? = ItemView.findViewById(R.id.all_image_2)
        private val m3: ImageView? = ItemView.findViewById(R.id.all_image_3)
        private  var MatchId: TextView = ItemView.findViewById(R.id.chatId_image)
        private  var Chatid: TextView = ItemView.findViewById(R.id.matchId_image)
        fun set(position: Int) {
            Chatid.text = (image_all!![position]!!.ChatID)
            MatchId.text = (image_all[position]!!.MatchId)
            val check = image_all.size - 1
            val position_real = (position + 1) * 3 - 1
            val position_real2 = position_real - 3
            if (position_real2 + 1 <= check) {
                if (position_real2 + 1 == check) {
                    Glide.with(context).load(image_all[position_real2 + 1]?.Url).into(m1!!)
                    Glide.with(context).load(image_all[position_real2 + 1]?.Url).into(m2!!)
                    Glide.with(context).load(image_all[position_real2 + 1]?.Url).into(m3!!)
                    m2.visibility = View.INVISIBLE
                    m3.visibility = View.INVISIBLE
                } else {
                    Glide.with(context).load(image_all[position_real2 + 1]?.Url).into(m1!!)
                }
            }
            if (position_real2 + 2 <= check) {
                if (position_real2 + 2 == check) {
                    Glide.with(context).load(image_all[position_real2 + 2]?.Url).into(m2!!)
                    Glide.with(context).load(image_all[position_real2 + 2]?.Url).into(m3!!)
                    m3.visibility = View.INVISIBLE
                } else {
                    Glide.with(context).load(image_all[position_real2 + 2]?.Url).into(m2!!)
                }
            }
            if (position_real2 + 3 <= check) {
                Glide.with(context).load(image_all[position_real2 + 3]?.Url).into(m3!!)
            }
            m1?.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, ItemImageActivity::class.java)
                intent.putExtra("matchIdReal", MatchId.text.toString())
                intent.putExtra("matchId", Chatid.text.toString())
                intent.putExtra("ChkImage2", (position_real2 + 2).toString())
                intent.putExtra("ChkImage", image_all.size.toString())
                (context as Activity?)!!.finish()
                context.startActivity(intent)
            })
            m2?.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, ItemImageActivity::class.java)
                intent.putExtra("matchIdReal", MatchId.text.toString())
                intent.putExtra("matchId", Chatid.text.toString())
                intent.putExtra("ChkImage2", (position_real2 + 3).toString())
                intent.putExtra("ChkImage", image_all.size.toString())
                (context as Activity?)!!.finish()
                context.startActivity(intent)
            })
            m3?.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, ItemImageActivity::class.java)
                intent.putExtra("matchIdReal", MatchId.text.toString())
                intent.putExtra("matchId", Chatid.text.toString())
                intent.putExtra("ChkImage2", (position_real2 + 4).toString())
                intent.putExtra("ChkImage", image_all.size.toString())
                (context as Activity?)!!.finish()
                context.startActivity(intent)
            })
        }

    }

}