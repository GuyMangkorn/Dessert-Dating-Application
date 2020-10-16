package com.jabirdeveloper.tinderswipe.Matches

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.Chat.ChatActivity
import com.jabirdeveloper.tinderswipe.Functions.ReportUser
import com.jabirdeveloper.tinderswipe.R
import com.tapadoo.alerter.Alerter

@SuppressLint("CutPasteId")
class MatchesViewHolders(itemView: View, private val context: Context?, private val matchesList: MutableList<MatchesObject?>?) : RecyclerView.ViewHolder(itemView) {
    var mMatchId: TextView?
    var mMatchName: TextView?
    var m_distance: TextView?
    var mLate: TextView?
    var mLate_view: TextView?
    var m_read: TextView?
    var last: TextView?
    var mMatchImage: ImageView
    var mStatus: ImageView?
    var mLinear: LinearLayout?
    private val userID: String?
    private var ChatId: String? = null
    private var Show: String? = null
    private val i_1: Intent?
    private var i = 0
    private var return_d: Int? = 0
    private lateinit var mDialog: Dialog
    private var position: Int? = 0
    private val mDataReport: DatabaseReference?
    var progressBar: ProgressBar?
    fun set(position: Int) {
        this.position = position
    }

    fun showdialog() {
        val dd = PopupMenu(context, mLate)
        dd.menuInflater.inflate(R.menu.popup_menu, dd.menu)
        dd.gravity = Gravity.END
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dd.setForceShowIcon(true)
        }
        dd.setOnMenuItemClickListener { item ->
            if (item.toString() == context?.getString(R.string.start_chat)) {
                val intent = Intent(context, ChatActivity::class.java)
                val b = Bundle()
                b.putString("matchId", mMatchId?.text.toString())
                b.putString("time_chk", mLate_view?.text.toString())
                b.putString("nameMatch", mMatchName?.text.toString())
                b.putString("first_chat", last?.text.toString())
                b.putString("unread", m_read?.text.toString())
                //b.putString("gender", matchesList?.get(position!!)!!.getGender())
                matchesList!!.elementAt(position!!)?.count_unread = 0
                intent.putExtras(b)
                context.startActivity(intent)
                m_read?.visibility = View.GONE;
                m_read?.text = "0"
            } else if (item.toString() == context?.getString(R.string.cancel_match)) {
                Show = mMatchId?.text.toString()
                Alerter.create(context as Activity?)
                        .setTitle(context.getString(R.string.cancel_match2))
                        .setText(context.getString(R.string.cancel_match_confirm))
                        .setIconColorFilter(Color.parseColor("#FFFFFF"))
                        .setBackgroundColorInt(Color.parseColor("#FF5050"))
                        .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_warning_black_24dp)!!)
                        .addButton(context.getString(R.string.cancle), R.style.AlertButton, View.OnClickListener { Alerter.hide() })
                        .addButton(context.getString(R.string.ok), R.style.AlertButton, View.OnClickListener {
                            Alerter.hide()
                            deletechild()
                        })
                        .show()
            } else if (item.toString() == context?.getString(R.string.dialog_report)) {
                val mDialog = ReportUser(context as Activity, mMatchId!!.text.toString()).reportDialog()
                mDialog.show()
            } else {
                Toast.makeText(context, "" + item, Toast.LENGTH_SHORT).show()
            }
            mLinear?.background = ContextCompat.getDrawable(context!!, R.drawable.background_click)
            true
        }
        dd.setOnDismissListener { mLinear!!.background = ContextCompat.getDrawable(context!!, R.drawable.background_click) }
        dd.show()
    }

    private fun deletechild() {
        val datadelete = FirebaseDatabase.getInstance().reference.child("Users")
        val datachat = FirebaseDatabase.getInstance().reference
        datachat.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ChatId = dataSnapshot.child("Users").child(userID.toString()).child("connection").child("matches").child(Show.toString()).child("ChatId").value.toString()
                if (dataSnapshot.child("Chat").hasChild(ChatId.toString())) {
                    datachat.child("Chat").child(ChatId.toString()).removeValue()
                }
                datadelete.child(userID.toString()).child("connection").child("matches").child(Show.toString()).removeValue()
                datadelete.child(userID.toString()).child("connection").child("yep").child(Show.toString()).removeValue()
                datadelete.child(Show.toString()).child("connection").child("matches").child(userID.toString()).removeValue()
                datadelete.child(Show.toString()).child("connection").child("yep").child(userID.toString()).removeValue()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun update(Child_2: String) {
        val mDialog = ReportUser(context as Activity, mMatchId!!.text.toString()).reportDialog()
        mDialog.show()

    }

    init {
        i_1 = Intent(context, MatchesActivity::class.java)
        mDataReport = FirebaseDatabase.getInstance().reference.child("Users")
        mLate_view = itemView.findViewById(R.id.Latest_time)
        m_read = itemView.findViewById(R.id.chat_unread)
        m_distance = itemView.findViewById(R.id.distance_text)
        mStatus = itemView.findViewById(R.id.on_off_matches)
        mMatchId = itemView.findViewById(R.id.id)
        userID = FirebaseAuth.getInstance().uid
        mLate = itemView.findViewById(R.id.Latest_chat)
        mMatchName = itemView.findViewById(R.id.Matches_name)
        mMatchImage = itemView.findViewById(R.id.Match_Image)
        last = itemView.findViewById(R.id.Latest_chat)
        mLinear = itemView.findViewById(R.id.dd_22)
        progressBar = itemView.findViewById(R.id.progress_image)
        mLinear?.setOnLongClickListener(OnLongClickListener {
            mLinear?.background = ContextCompat.getDrawable(context!!, R.drawable.background_click_tran)
            showdialog()
            true
        })
        mLinear?.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            val b = Bundle()
            b.putString("time_chk", mLate_view?.hint.toString())
            b.putString("matchId", mMatchId?.text.toString())
            b.putString("nameMatch", mMatchName?.text.toString())
            b.putString("first_chat", last?.text.toString())
            b.putString("unread", m_read?.text.toString())
            //b.putString("gender", matchesList?.get(position!!)!!.getGender())
            matchesList?.get(position!!)?.count_unread = 0
            intent.putExtras(b)
            context?.startActivity(intent)
            m_read?.visibility = View.GONE
            m_read?.text = "0"
        })
    }
}