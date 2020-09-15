package com.jabirdeveloper.tinderswipe.Listcard

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.ProfileUserOppositeActivity2
import com.jabirdeveloper.tinderswipe.R
import com.tapadoo.alerter.Alerter
import java.text.SimpleDateFormat
import java.util.*

class ListcardViewHolders(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    var mMatchId: TextView = itemView.findViewById(R.id.id)
    var mMatchName: TextView = itemView.findViewById(R.id.Matches_name)
    var mDistance: TextView = itemView.findViewById(R.id.distance_text)
    var mStatus: TextView = itemView.findViewById(R.id.status_time)
    var tag: TextView = itemView.findViewById(R.id.tagkm)
    var percent:TextView = itemView.findViewById(R.id.Latest_chat)
    var myself: TextView = itemView.findViewById(R.id.myself)
    var mMatchImage: ImageView = itemView.findViewById(R.id.Match_Image)
    var on_off_list: ImageView = itemView.findViewById(R.id.on_off_matches)
    private var mLinear: LinearLayout = itemView.findViewById(R.id.dd_22)
    var view_item: LinearLayout = itemView.findViewById(R.id.view_item)
    var view_online: LinearLayout = itemView.findViewById(R.id.view_online)
    private val mDataReport: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
    private var seeDB: DatabaseReference? = null
    private val userID: String = FirebaseAuth.getInstance().uid!!
    private var return_d = 0
    private var i = 0
    var progressBar: ProgressBar? = itemView.findViewById(R.id.progress_image)
    var mdialog: Dialog? = null
    private val selected: String? = null

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun showdialog() {
        val dd = PopupMenu(context, tag)
        dd.menuInflater.inflate(R.menu.popup_listmenu, dd.menu)
        dd.gravity = Gravity.START
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dd.setForceShowIcon(true)
        }
        dd.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_unmatch2) {
                val choice = context.resources.getStringArray(R.array.report_item)
                val checked_item = BooleanArray(choice.size)
                val builder = AlertDialog.Builder(context)
                builder.setTitle(R.string.dialog_reportUser)
                builder.setMultiChoiceItems(R.array.report_item, checked_item) { _, which, isChecked -> checked_item[which] = isChecked }
                builder.setPositiveButton(R.string.dialog_report) { _, _ ->
                    return_d = 0
                    i = 0
                    while (i < choice.size) {
                        val checked = checked_item[i]
                        if (checked) {
                            update(i.toString())
                        }
                        i++
                    }
                    //UpdateDate()
                }
                builder.setNegativeButton(R.string.cancle) { dialog, which -> dialog.dismiss() }
                val mDialog = builder.create()
                mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.myrect2))
                mDialog.show()
            }
            mLinear.background = ContextCompat.getDrawable(context, R.drawable.background_click)
            true
        }
        dd.setOnDismissListener { mLinear.background = ContextCompat.getDrawable(context, R.drawable.background_click) }
        dd.show()
    }

    private fun update(Child: String) {
        mDataReport.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val MatchId = mMatchId.text.toString()
                var date_before: Boolean = true
                if (dataSnapshot.child(userID).child("PutReportId").hasChild(MatchId)) {
                    date_before = false
                } else {
                    mDataReport.child(userID)
                            .child("PutReportId")
                            .child(mMatchId.text.toString())
                            .setValue("true")
                }
                if (date_before) {
                    if (return_d == 0) {
                        return_d = -1
                        Alerter.create(context as Activity?)
                                .setTitle(context.getString(R.string.report_suc))
                                .setText(context.getString(R.string.report_suc2))
                                .setBackgroundColorInt(Color.parseColor("#7AFFCF"))
                                .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_check)!!)
                                .show()
                    }
                    if (!dataSnapshot.child(MatchId).hasChild("Report")) {
                        val jj: MutableMap<String?, Any?> = HashMap()
                        jj[Child] = "1"
                        mDataReport.child(MatchId).child("Report").updateChildren(jj)
                    } else if (dataSnapshot.child(MatchId).hasChild("Report")) {
                        if (dataSnapshot.child(MatchId).child("Report").hasChild(Child)) {
                            val count_rep = dataSnapshot.child(MatchId).child("Report").child(Child).value.toString().toInt() + 1
                            val input_count = count_rep.toString()
                            val jj: MutableMap<String?, Any?> = HashMap()
                            jj[Child] = input_count
                            mDataReport.child(MatchId).child("Report").updateChildren(jj)
                        } else {
                            val jj: MutableMap<String?, Any?> = HashMap()
                            jj[Child] = "1"
                            mDataReport.child(MatchId).child("Report").updateChildren(jj)
                        }
                    }
                } else {
                    if (return_d == 0) {
                        return_d = -1
                        Alerter.create(context as Activity?)
                                .setTitle(context.getString(R.string.report_failed))
                                .setText(context.getString(R.string.report_fail))
                                .setBackgroundColorInt(Color.parseColor("#FF5050"))
                                .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_do_not_disturb_black_24dp)!!)
                                .show()
                        val builder = AlertDialog.Builder(context)
                        val view = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null)
                        val title = view.findViewById(R.id.title_alert) as TextView
                        val li = view.findViewById(R.id.linear_alert) as LinearLayout
                        val icon = view.findViewById(R.id.icon_alert) as ImageView
                        val message = view.findViewById(R.id.message_alert) as TextView
                        val dis = view.findViewById(R.id.dis_alert) as TextView
                        val yes = view.findViewById(R.id.yes_alert) as TextView
                        yes.setText(R.string.report_close)
                        li.gravity = Gravity.CENTER
                        dis.visibility = View.GONE
                        title.setText(R.string.report_alert)
                        message.setText(R.string.report_reset)
                        icon.background = ContextCompat.getDrawable(context, R.drawable.ic_warning_black_24dp)
                        builder.setView(view)
                        val mDialog = builder.show()
                        yes.setOnClickListener { mDialog.dismiss() }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    init {
        mLinear.setOnLongClickListener(OnLongClickListener {
            mLinear.background = ContextCompat.getDrawable(context, R.drawable.background_click_tran)
            showdialog()
            true
        })
        mLinear.setOnClickListener(View.OnClickListener {
            seeDB = FirebaseDatabase.getInstance().reference.child("Users").child(mMatchId.text.toString()).child("see_profile").child(userID)
            val calendar = Calendar.getInstance()
            val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
            val time_user = currentTime.format(calendar.time)
            val currentDate = SimpleDateFormat("dd/MM/yyyy")
            val date_user = currentDate.format(calendar.time)
            val newDate: MutableMap<String?, Any?> = HashMap()
            newDate["date"] = date_user
            newDate["time"] = time_user
            seeDB!!.updateChildren(newDate)
            val intent = Intent(context, ProfileUserOppositeActivity2::class.java)
            intent.putExtra("User_opposite", mMatchId.text.toString())
            intent.putExtra("form_list", mMatchId.text.toString())
            context.startActivity(intent)
        })
    }
}