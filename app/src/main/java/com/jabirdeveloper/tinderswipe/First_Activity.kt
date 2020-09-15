package com.jabirdeveloper.tinderswipe

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.hanks.htextview.base.AnimationListener
import com.hanks.htextview.base.HTextView
import com.hanks.htextview.line.LineTextView
import com.jaredrummler.android.widget.AnimatedSvgView
import java.util.*

class First_Activity : AppCompatActivity() {
    private var firebaseAuthStateListener: AuthStateListener? = null
    private var mAuth: FirebaseAuth? = null
    private var usersDb: DatabaseReference? = null
    private var first = true
    private val plus: SwitchpageActivity? = SwitchpageActivity()
    private var hTextView: LineTextView? = null
    private var mContext: Context? = null
    private var mLocationManager: LocationManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_)
        MobileAds.initialize(this) {}
        mContext = applicationContext
        mAuth = FirebaseAuth.getInstance()
        firebaseAuthStateListener = AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val svgView: AnimatedSvgView = findViewById(R.id.animated_svg_view)
                svgView.start()
                usersDb = FirebaseDatabase.getInstance().reference.child("Users")
                usersDb!!.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.child(mAuth!!.currentUser!!.uid).child("sex").exists()) {
                            check_HaveMatch()
                            pushToken()
                        } else {
                            mAuth!!.signOut()
                            val intent = Intent(this@First_Activity, ChooseLoginRegistrationActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            } else {
                val intent = Intent(this@First_Activity, ChooseLoginRegistrationActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }
        mLocationManager = this@First_Activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDiabledDialog()
        } else if (ActivityCompat.checkSelfPermission(this@First_Activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this@First_Activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@First_Activity, arrayOf<String?>(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            ), 1)
        } else mAuth!!.addAuthStateListener(firebaseAuthStateListener!!)
        hTextView = findViewById(R.id.textview)
        hTextView!!.setAnimationListener(SimpleAnimationListener(this@First_Activity))
        hTextView!!.animateText("Welcome to my world")
    }
    private fun pushToken(){
        val token = FirebaseInstanceId.getInstance().token
        FirebaseDatabase.getInstance().reference.child("Users").child(mAuth!!.currentUser!!.uid).child("token").setValue(token)
    }
    /*private fun check_dd() {
        val matchDb = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth!!.currentUser!!.uid).child("connection")
        matchDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("matches")) {
                    } else {
                        Check_report()
                    }
                } else {
                    Check_report()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/
    //private var first_hasConnections = true
    //private var first_hasConnections2 = true
    private fun hasConnections() {
        val matchDb = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth!!.currentUser!!.uid)
        matchDb.orderByKey().equalTo("connection").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    Log.d("fisrtAct_testing","check_snapshot hasConnections")
                    hasMatches()
                }else{
                    Log.d("fisrtAct_testing","check_else hasConnections")
                    Check_report()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        /*matchDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                if (dataSnapshot.key == "connection") {
                    if (first_hasConnections) {
                        first_hasConnections = false
                        hasMatches()
                    }
                } else if (first_hasConnections2) {
                    first_hasConnections2 = false
                    check_dd()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Toast.makeText(this@First_Activity, "connection_remove", Toast.LENGTH_SHORT).show()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })*/
    }

    //private var first_hasMatches2 = true
    //private var first_hasMatches = true
    private fun hasMatches() {
        val matchDb = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth!!.currentUser!!.uid).child("connection")
        matchDb.orderByKey().equalTo("matches").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    Log.d("fisrtAct_testing","check_snapshot hasMatches")
                    getUserMarchId()
                }else{
                    Log.d("fisrtAct_testing","check_else hasMatches")
                    Check_report()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        /*matchDb.orderByKey().equalTo("matches").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                    if (dataSnapshot.key == "matches"){
                    if (first_hasMatches) {
                        first_hasMatches = false
                        getUserMarchId()
                    }
                } else if (first_hasMatches2) {
                    Log.d("fisrtAct_testing","check_else")
                    first_hasMatches2 = false
                      getUserMarchId()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })*/
    }

    private fun check_HaveMatch() {
        val prefs = getSharedPreferences(mAuth!!.currentUser!!.uid + "Match_first", Context.MODE_PRIVATE)
        val allPrefs = prefs.all
        val set = allPrefs.keys
        for (s in set) {
            UidMatch?.add(s)
            Toast.makeText(this@First_Activity, s, Toast.LENGTH_SHORT).show()
        }
        //Toast.makeText(First_Activity.this,"s"+(UidMatch.size()),Toast.LENGTH_SHORT).show();
        hasConnections()
    }

    fun showGPSDiabledDialog() {
        val builder = AlertDialog.Builder(this@First_Activity)
        builder.setTitle(R.string.GPS_Disabled)
        builder.setMessage(R.string.GPS_open)
        builder.setPositiveButton(R.string.open_gps) { dialog, which -> startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0) }.setNegativeButton(R.string.report_close) { dialog, which ->
            val intent = Intent(this@First_Activity, show_gps_open::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(intent)
        }
        val mGPSDialog: Dialog = builder.create()
        mGPSDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@First_Activity, R.drawable.myrect2))
        mGPSDialog.show()
    }

    private val UidMatch_New: MutableList<String?>? = ArrayList()
    private val UidMatch: MutableList<String?>? = ArrayList()
    private var count_number_chat:Int? = 0
    private var chk = 0
    private var chktotalhavechat = 0
    private var chkcountchat = 0
    private var chk_node = 0
    private var start = 0
    private fun getUserMarchId() {
        val matchDb = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth!!.currentUser!!.uid).child("connection").child("matches")
        matchDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                ++chk_node
                ++start
                val chatID = dataSnapshot.child("ChatId").value.toString()
                val MatchIDStored = getSharedPreferences(mAuth!!.currentUser!!.uid + "Match_first", Context.MODE_PRIVATE)
                val editorMatch = MatchIDStored.edit()
                editorMatch.putInt(dataSnapshot.key, start)
                editorMatch.apply()
                getMatchUnread(dataSnapshot.key, chatID)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                --chk_node
                val MatchIDStored = getSharedPreferences("Match_First", Context.MODE_PRIVATE)
                val editorMatch = MatchIDStored.edit()
                editorMatch.putInt(mAuth!!.currentUser!!.uid, --start)
                editorMatch.apply()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var bkk = true
    private fun getMatchUnread(MatchId: String?, ChatId: String?) {
        val chatDB = FirebaseDatabase.getInstance().reference.child("Chat")
        val dd_1 = chatDB.child(ChatId.toString()).orderByKey().limitToLast(1)
        dd_1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ++chk
                if (dataSnapshot.exists()) {
                    for (dd in dataSnapshot.children) {
                        if (dataSnapshot.child(dd.key.toString()).child("createByUser").value.toString() == MatchId) {
                            if (!first) {
                                Log.d("count_unread","$chk , $sum_reported")
                                if (chk > sum_reported) {
                                    if (dataSnapshot.child(dd.key.toString()).child("read").value.toString() != "Read") {
                                        val MyUnread = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                                        val dd2 = MyUnread.getInt("total", 0)
                                        count_number_chat = dd2 + 1
                                        val MyUnread2 = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                                        val editorRead = MyUnread2.edit()
                                        editorRead.putInt("total", count_number_chat!!)
                                        editorRead.apply()
                                        plus?.setCurrentIndex(count_number_chat!!)
                                    } else {
                                        val mySharedPreferences = getSharedPreferences("SentRead", Context.MODE_PRIVATE)
                                        val read = mySharedPreferences.getInt("Read", 0)
                                        val MyUnread = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                                        val dd2 = MyUnread.getInt("total", 0)
                                        count_number_chat = dd2 - read
                                        if (count_number_chat!! < 0) {
                                            count_number_chat = 0
                                        }
                                        val MyUnread2 = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                                        val editorRead = MyUnread2.edit()
                                        editorRead.putInt("total", count_number_chat!!)
                                        editorRead.apply()
                                        plus!!.setCurrentIndex(count_number_chat!!)
                                    }
                                }
                            } else {
                                bkk = false
                                ++chktotalhavechat;
                                Log.d("count_unread","before $ChatId")
                                getLastCheck(ChatId)
                            }
                        } else if (chk == chk_node) {
                            if (bkk) {
                                Check_report()
                            }
                        }
                    }
                } else {
                    if (chk == chk_node) {
                        if (bkk) {
                            Check_report()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getLastCheck(ChatId: String?) {
        val chatDB = FirebaseDatabase.getInstance().reference.child("Chat").child(ChatId.toString())
        val dd = chatDB.orderByChild("read").equalTo("Unread")
        dd.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("count_unread","after $ChatId")
                if (first) {
                    ++chkcountchat;
                    //Log.d("count_unread","$ChatId")
                    val count = dataSnapshot.childrenCount .toInt()
                    count_number_chat = count + count_number_chat!!
                    Log.d("count_unread","$count total $count_number_chat")
                    //Log.d("count_unread","$chk , $chk_node , $chktotalhavechat")
                    if (chkcountchat == chktotalhavechat) {
                        Check_report()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    var name_caution: MutableList<String?>? = ArrayList()
    var value_caution: MutableList<Int?>? = ArrayList()
    private var sum_reported = 0
    private fun Check_report() {
        val reportDb = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth!!.currentUser!!.uid).child("Report")
        reportDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (first) {
                    sum_reported = chk_node
                    first = false
                    bkk = false
                    val MyUnread = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                    val editorRead = MyUnread.edit()
                    editorRead.putInt("total", count_number_chat!!.toInt())
                    editorRead.apply()
                    val intent = Intent(this@First_Activity, SwitchpageActivity::class.java)
                    var sum_report:Int? = 0
                    if (UidMatch_New!!.size > 0) {
                        intent.putExtra("NewMatch", UidMatch_New as ArrayList<String?>?)
                    }
                    if (dataSnapshot.exists()) {
                        for (dd in dataSnapshot.children) {
                            sum_report = Integer.valueOf(dataSnapshot.child(dd.key.toString()).value.toString())
                            name_caution?.add(dd.key)
                            value_caution?.add(sum_report)
                        }
                        if (sum_report != 0) {
                            intent.putExtra("warning", name_caution as ArrayList<String?>?)
                            intent.putExtra("warning_value", value_caution as ArrayList<Int?>?)
                        }
                        intent.putExtra("first", count_number_chat.toString())
                        startActivity(intent)
                        finish()
                    } else {
                        intent.putExtra("first", count_number_chat.toString())
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /*private fun getDatabaseUser(Uid: String?) {
        val InfoDB = FirebaseDatabase.getInstance().reference.child("Users").child(Uid.toString()).child("name")
        InfoDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    NameMatch?.add(dataSnapshot.value.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/

    /*private val IDNotification: MutableList<String?>? = ArrayList()
    private val IndexNotification: MutableList<Int?>? = ArrayList()
    private var id_plus = 0
    private fun Notification_chat(lastChat: String?, time: String?, ID: String?) {
        var icon: Bitmap? = null
        var Name: String? = "null"
        for (i in UidMatch_Image!!.indices) {
            if (ID == UidMatch?.get(i)) {
                icon = UidMatch_Image?.get(i)
                Name = NameMatch?.get(i)
                break
            }
        }
        val intent = Intent(this@First_Activity, ChatActivity::class.java)
        val b = Bundle()
        val random = Random()
        var TwoItems = false
        var id = 0
        if (IDNotification!!.size == 0) {
            id = ++id_plus
            IDNotification?.add(ID)
            IndexNotification?.add(id)
        } else {
            for (i in IDNotification.indices) {
                if (IDNotification?.get(i) == ID) {
                    TwoItems = true
                    id = IndexNotification?.get(i)!!
                }
            }
            if (!TwoItems) {
                id = ++id_plus
                Toast.makeText(this@First_Activity, "id :$id", Toast.LENGTH_SHORT).show()
                IDNotification.add(ID)
                IndexNotification!!.add(id)
            }
        }
        b.putString("time_chk", time)
        b.putString("matchId", ID)
        b.putString("nameMatch", Name)
        b.putString("unread", "-1")
        intent.putExtras(b)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(this@First_Activity, 1, intent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this@First_Activity, App.Companion.CHANNEL_ID!!)
                .setSmallIcon(R.drawable.ic_love)
                .setContentTitle(Name)
                .setGroup("Chat")
                .setContentText(lastChat)
                .setLargeIcon(icon)
                .setColor(0xFFCC00)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        val Sum = NotificationCompat.Builder(this@First_Activity, App.Companion.CHANNEL_ID!!)
                .setSmallIcon(R.drawable.ic_love)
                .setStyle(NotificationCompat.InboxStyle().setBigContentTitle(getString(R.string.New_message)).setSummaryText(getString(R.string.You_have_new_message)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup("Chat")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .build()
        notificationManager = NotificationManagerCompat.from(this@First_Activity)
        if (id == 2) {
            notificationManager!!.notify(id + random.nextInt(9999 - 1000) + 1000, Sum)
        }
        notificationManager!!.notify(id, notification)
    }*/


    override fun onStop() {
        super.onStop()
        mAuth!!.removeAuthStateListener(firebaseAuthStateListener!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            recreate()
            if (mLocationManager == null) {
                mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.size > 0
                    && grantResults!!.get(0) == PackageManager.PERMISSION_GRANTED) {
                recreate()
            } else {
                val intent = Intent(this@First_Activity, show_gps_open::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("2", "2")
                finish()
                startActivity(intent)
            }
        }
    }



    override fun onStart() {
        super.onStart()
        ///////////////////////////////// เอา mAuth.start ออก
    }

    private inner class SimpleAnimationListener(private val context: Context?) : AnimationListener {
        override fun onAnimationEnd(hTextView: HTextView?) {
            hTextView!!.animateText("Welcome to my world")
        }

    }
}