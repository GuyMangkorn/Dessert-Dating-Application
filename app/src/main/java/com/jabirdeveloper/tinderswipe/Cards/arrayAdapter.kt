package com.jabirdeveloper.tinderswipe.Cards

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jabirdeveloper.tinderswipe.MainActivity
import com.jabirdeveloper.tinderswipe.ProfileUserOppositeActivity2
import com.jabirdeveloper.tinderswipe.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by manel on 9/5/2017.
 */
class arrayAdapter(private var items: ArrayList<cards>, private val context: Context?, private val activity: MainActivity) : RecyclerView.Adapter<arrayAdapter.Holder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.set(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView?
        private val age: TextView?
        private val dis: TextView?
        private val myself: TextView?
        private val image: ImageView?
        private val on_off: ImageView?
        private val currentUid: String?
        private val linearLayout: LinearLayout?

        @SuppressLint("SetTextI18n")
        fun set(position: Int) {


            if (items[position].star) {
                linearLayout!!.visibility = View.VISIBLE
            }
            name!!.text = items[position].name
            if (!items[position].off_status) {
                on_off!!.visibility = View.VISIBLE
                if (items[position].status == "offline") {
                    Glide.with(context!!).load(R.drawable.offline_user).into(on_off)
                } else {
                    Glide.with(context!!).load(R.drawable.online_user).into(on_off)
                }
            } else on_off!!.visibility = View.GONE
            age!!.text = ",  " + items[position].age
            dis!!.text = items[position].city + ",  " + items[position].dis + " " + context!!.getString(R.string.kilometer)
            Glide.with(context).load(items[position].profileImageUrl).into(image!!)
            if (items[position].myself != "") {
                myself!!.visibility = View.VISIBLE
                myself.text = items[position].myself
            } else myself!!.visibility = View.GONE
            image.setOnClickListener(View.OnClickListener {
                val CurrentUserConnectionDb = FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(items.get(position).userId!!)
                        .child("see_profile").child(currentUid!!)
                val calendar = Calendar.getInstance()
                val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
                val time_user = currentTime.format(calendar.time)
                val currentDate = SimpleDateFormat("dd/MM/yyyy")
                val date_user = currentDate.format(calendar.time)
                val newDate = hashMapOf<String, Any>()
                newDate["date"] = date_user
                newDate["time"] = time_user
                CurrentUserConnectionDb.updateChildren(newDate)
                val intent = Intent(context, ProfileUserOppositeActivity2::class.java)
                intent.putExtra("User_opposite", items[position].userId)
                intent.putExtra("form_main", "1")
                activity.startActivityForResult(intent, 115)
            })
        }

        init {
            currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            name = itemView.findViewById(R.id.cname)
            age = itemView.findViewById(R.id.cage)
            dis = itemView.findViewById(R.id.cdis)
            image = itemView.findViewById(R.id.image)
            on_off = itemView.findViewById(R.id.on_off)
            myself = itemView.findViewById(R.id.myselfcard)
            linearLayout = itemView.findViewById(R.id.layout_star)
        }
    }


}