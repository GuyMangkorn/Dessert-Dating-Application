package com.jabirdeveloper.tinderswipe.Listcard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.jabirdeveloper.tinderswipe.R

class ListcardAdapter(private val matchesList: ArrayList<ListcardObject?>, private val context: Context) : RecyclerView.Adapter<ListcardViewHolders>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListcardViewHolders {

        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_matches, null, false)
        val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutView.layoutParams = lp
        return ListcardViewHolders(layoutView, context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListcardViewHolders, position: Int) {

        Log.d("dddddddddddddddddddddd", matchesList.size.toString())
        Glide.with(context).load(matchesList[position]!!.profileImageUrl).listener(object : RequestListener<Drawable?> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                holder.view_online.visibility = View.VISIBLE
                holder.progressBar!!.visibility = View.GONE
                return false
            }
        }).apply(RequestOptions().override(100, 100)).into(holder.mMatchImage)
        if (!matchesList[position]!!.off_status) {
            holder.on_off_list.visibility = View.VISIBLE
            if (matchesList[position]!!.status_opposite == "offline") {
                Glide.with(context).load(R.drawable.offline_user).into(holder.on_off_list)
                if (matchesList[position]!!.typeTime != "") {
                    val time = matchesList[position]!!.time
                    when (matchesList[position]!!.typeTime) {
                        "d" -> {
                            holder.mStatus.text = time + " " + context.getString(R.string.days_ago)
                        }
                        "h" -> {
                            holder.mStatus.text = time + " " + context.getString(R.string.hours_ago)
                        }
                        "m" -> {
                            holder.mStatus.text = time + " " + context.getString(R.string.minutes_ago)
                        }
                        "0" -> {
                            holder.mStatus.text = "เมื่อสักครู่"
                        }
                    }
                }
//                if (matchesList[position]!!.time != "null") {
//                    val chk = matchesList[position]!!.time as String
//                    if (chk.substring(0, 1) == "d") {
//                        holder.mStatus.text = chk.substring(1) + " " + context.getString(R.string.days_ago)
//                    } else {
//                        val time = matchesList[position]!!.time!!.toInt()
//                        if (time >= 60) {
//                            val hr_int = time / 60
//                            val hr = hr_int.toString()
//                            holder.mStatus.text = hr + " " + context.getString(R.string.hours_ago)
//                        } else {
//                            val mn = time.toString()
//                            holder.mStatus.text = mn + " " + context.getString(R.string.minutes_ago)
//                        }
//                    }
//                } else if (matchesList[position]!!.time== "null") {
//                    holder.mStatus.text = context.getString(R.string.dont_now_online)
//                } else if (matchesList[position]!!.time == "1") {
//                    Glide.with(context).load(R.drawable.online_user).into(holder.on_off_list)
//                }
            } else {
                Glide.with(context).load(R.drawable.online_user).into(holder.on_off_list)
                holder.mStatus.text = context.getString(R.string.online)
            }
        } else {
            holder.on_off_list.visibility = View.GONE
            holder.mStatus.text = ""
        }
        holder.mDistance.text = matchesList[position]!!.distance + " " + context.getString(R.string.kilometer)
        holder.tag.visibility = View.VISIBLE
        /*  if (matchesList[position]!!.getGender() == "Male") {
              holder.tag.text = context.getString(R.string.male) + " " + matchesList[position]!!.getAge()
              holder.tag.setTextColor(Color.parseColor("#FF9800"))
          } else {
              holder.tag.text = context.getString(R.string.female) + " " + matchesList[position]!!.getAge()
              holder.tag.setTextColor(Color.parseColor("#FF9800"))
          }*/
        holder.tag.text = matchesList[position]!!.Age
        holder.mMatchId.text = matchesList[position]!!.userId
        holder.mMatchId.visibility = View.GONE
        holder.mMatchName.text = matchesList[position]!!.name
        if (matchesList[position]!!.myself != "") {
            holder.myself.visibility = View.VISIBLE
            holder.myself.text = matchesList[position]!!.myself
        } else {
            holder.myself.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return matchesList.size
    }

}