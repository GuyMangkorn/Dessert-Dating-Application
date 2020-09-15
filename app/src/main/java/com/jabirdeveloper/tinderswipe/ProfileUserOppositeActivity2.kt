package com.jabirdeveloper.tinderswipe

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.bumptech.glide.Glide
import com.github.demono.AutoScrollViewPager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.Chat.ChatActivity
import com.jabirdeveloper.tinderswipe.Functions.CalculateDistance
import com.tapadoo.alerter.Alerter
import me.relex.circleindicator.CircleIndicator
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NAME_SHADOWING")
class ProfileUserOppositeActivity2 : AppCompatActivity(), BillingProcessor.IBillingHandler {
    private lateinit var currentUserId: String
    private lateinit var matchId: String
    private lateinit var findImage: DatabaseReference
    private lateinit var mUserDatabase: DatabaseReference
    private lateinit var mLinear: LinearLayout
    private lateinit var l1: LinearLayout
    private lateinit var l2: LinearLayout
    private lateinit var l3: LinearLayout
    private lateinit var l4: LinearLayout
    private lateinit var l5: LinearLayout
    private lateinit var l6: LinearLayout
    private lateinit var madoo: LinearLayout
    private var Url0: String? = null
    private var Url1: String? = null
    private var Url2: String? = null
    private var Url3: String? = null
    private var Url4: String? = null
    private var Url5: String? = null
    private var language_2: String? = null
    private lateinit var currentUid: String
    private var send: String? = null
    private lateinit var bp: BillingProcessor
    private var noti_match: String? = null
    private lateinit var listItems: Array<String?>
    private lateinit var listItems2: Array<String?>
    private lateinit var listItems3: Array<String?>
    private lateinit var name: TextView
    private lateinit var age: TextView
    private lateinit var gender: TextView
    private lateinit var study: TextView
    private lateinit var career: TextView
    private lateinit var religion: TextView
    private lateinit var myself: TextView
    private lateinit var language: TextView
    private lateinit var city1: TextView
    private lateinit var report: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var flexboxLayout: FlexboxLayout
    private lateinit var params: GridLayout.LayoutParams
    private lateinit var fab: FloatingActionButton
    private var return_d = 0
    private var click = true
    private var x_user = 0.0
    private var y_user = 0.0
    private var x_opposite = 0.0
    private var y_opposite = 0.0
    private lateinit var like: FloatingActionButton
    private lateinit var dislike: FloatingActionButton
    private lateinit var star: FloatingActionButton
    private var i = 0
    private var chk = 11
    private lateinit var viewPager: WrapContentHeightViewPager
    private lateinit var dialog: Dialog
    private var text: String? = null
    private lateinit var adapter: ScreenAdapter
    private lateinit var mDatabaseChat: DatabaseReference
    private lateinit var usersDb: DatabaseReference
    private var maxlike = 0
    private var maxstar = 0
    private var maxadmob = 0
    private var time_user: String? = null
    private var date_user: String? = null
    private var drawable_gender = 0
    private var maxChat = 0
    private var statusVip = false
    lateinit var rewardedAd: RewardedAd
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user_opposite2)
        mAuth = FirebaseAuth.getInstance()
        matchId = intent.extras!!.getString("User_opposite")!!
        currentUserId = mAuth.uid.toString()
        bp = BillingProcessor(this, Id.Id, this)
        bp.initialize()
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        language_2 = preferences.getString("My_Lang", "")
        currentUid = mAuth.currentUser!!.uid
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(matchId)
        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat")
        flexboxLayout = findViewById(R.id.Grid_profile)
        flexboxLayout.flexDirection = FlexDirection.ROW
        params = GridLayout.LayoutParams()
        name = findViewById(R.id.name_profile)
        age = findViewById(R.id.age_profile)
        gender = findViewById(R.id.gender_profile)
        study = findViewById(R.id.study_profile)
        career = findViewById(R.id.career_profile)
        religion = findViewById(R.id.religion_profile)
        myself = findViewById(R.id.myself_profile)
        language = findViewById(R.id.language_profile)
        city1 = findViewById(R.id.city_profile)
        viewPager = findViewById(R.id.slide_main)
        mLinear = findViewById<View?>(R.id.main) as LinearLayout
        fab = findViewById(R.id.floatingActionButton)
        report = findViewById(R.id.report)
        dialog = Dialog(this@ProfileUserOppositeActivity2)
        listItems = resources.getStringArray(R.array.shopping_item)
        listItems2 = resources.getStringArray(R.array.pasa_item)
        listItems3 = resources.getStringArray(R.array.religion_item)
        l1 = findViewById(R.id.linearLayout33)
        l2 = findViewById(R.id.linearLayout34)
        l3 = findViewById(R.id.linearLayout35)
        l4 = findViewById(R.id.linearLayout36)
        l5 = findViewById(R.id.linearLayout37)
        l6 = findViewById(R.id.linearLayout38)
        l1.visibility = View.GONE
        l2.visibility = View.GONE
        l3.visibility = View.GONE
        l4.visibility = View.GONE
        l5.visibility = View.GONE
        l6.visibility = View.GONE
        val preferences2 = getSharedPreferences("notification_match", Context.MODE_PRIVATE)
        noti_match = preferences2.getString("noti", "1")
        // findIamge()
        getdis()
        getUserinfo()
        madoo = findViewById(R.id.linearLayout17)
        if (intent.hasExtra("form_main")) {
            madoo.visibility = View.VISIBLE
        }
        if (intent.hasExtra("form_like")) {
            madoo.visibility = View.VISIBLE
        }
        if (intent.hasExtra("form_list")) {
            fab.visibility = View.VISIBLE
        }
        rewardedAd = RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917")
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                Toast.makeText(this@ProfileUserOppositeActivity2, "สวย", Toast.LENGTH_SHORT).show()
            }
            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                Toast.makeText(this@ProfileUserOppositeActivity2, errorCode.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        like = findViewById(R.id.like_button)
        dislike = findViewById(R.id.dislike_button)
        star = findViewById(R.id.star_button)
        val calendar = Calendar.getInstance()
        val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
        time_user = currentTime.format(calendar.time)
        val currentDate = SimpleDateFormat("dd/MM/yyyy")
        date_user = currentDate.format(calendar.time)
        val main = MainActivity()
        like.setOnClickListener(View.OnClickListener {
            if (!intent.hasExtra("form_like")) {
                setResult(1)

            } else {
                val DateTime = hashMapOf<String,Any>()
                DateTime["date"] = date_user!!
                DateTime["time"] = time_user!!
                usersDb.child(matchId).child("connection").child("yep").child(currentUid).updateChildren(DateTime)
                maxlike--
                usersDb.child(currentUid).child("MaxLike").setValue(maxlike)
            }
            onBackPressed()
            isConnectionMatches2()
        })
        dislike.setOnClickListener(View.OnClickListener {
            if (!intent.hasExtra("form_like")) {
                setResult(2)
            } else {
                usersDb.child(matchId).child("connection").child("nope").child(currentUid).setValue(true)

            }
            onBackPressed()
        })
        star.setOnClickListener(View.OnClickListener {
            if (!intent.hasExtra("form_like")) {
                setResult(3)
            } else {
                val Datetime = hashMapOf<String,Any>()
                Datetime["date"] = date_user!!
                Datetime["time"] = time_user!!
                Datetime["super"] = true
                usersDb.child(matchId).child("connection").child("yep").child(currentUid).updateChildren(Datetime)
                maxstar--
                usersDb.child(currentUid).child("MaxStar").setValue(maxstar)
            }
            onBackPressed()
        })
        report.setOnClickListener(View.OnClickListener {
            val choice = resources.getStringArray(R.array.report_item)
            val checked_item = BooleanArray(choice.size)
            val builder = AlertDialog.Builder(this@ProfileUserOppositeActivity2)
            builder.setTitle(R.string.dialog_reportUser)
            builder.setMultiChoiceItems(R.array.report_item, checked_item) { _, which, isChecked ->
                checked_item[which] = isChecked
                val item = choice[which]
            }
            builder.setPositiveButton(R.string.dialog_report) { _, _ ->
                return_d = 0
                i = 0
                while (i < choice.size) {
                    val checked = checked_item[i]
                    if (checked) {
                        update(i.toString()!!)
                    }
                    i++
                }
                //UpdateDate()
            }
            builder.setNegativeButton(R.string.cancle) { dialog, which -> dialog.dismiss() }
            val mDialog = builder.create()
            mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@ProfileUserOppositeActivity2, R.drawable.myrect2))
            mDialog.show()
        })
        fab.setOnClickListener(View.OnClickListener {
            if (click || statusVip) {
                val inflater = layoutInflater
                val view2 = inflater.inflate(R.layout.sayhi_dialog, null)
                val b1 = view2.findViewById<Button>(R.id.buy)
                val close = view2.findViewById<ImageView>(R.id.close)
                val textsend = view2.findViewById<EditText>(R.id.text_send)
                b1.setOnClickListener {
                    text = textsend.text.toString()
                    if (text!!.trim { it <= ' ' }.isNotEmpty()) {
                        Toast.makeText(this@ProfileUserOppositeActivity2, text, Toast.LENGTH_SHORT).show()
                        send = text
                        fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ProfileUserOppositeActivity2, R.color.text_gray))
                        fab.isClickable = false
                        val key = FirebaseDatabase.getInstance().reference.child("Chat").push().key!!
                        usersDb.child(matchId).child("connection").child("chatna").child(currentUid).setValue(key)
                        val calendar = Calendar.getInstance()
                        val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
                        val time_user = currentTime.format(calendar.time)
                        val currentDate = SimpleDateFormat("dd/MM/yyyy")
                        val date_user = currentDate.format(calendar.time)
                        val newMessageDb = mDatabaseChat.child(key).push()
                        val newMessage = hashMapOf<String,Any>()
                        newMessage["createByUser"] = currentUid
                        newMessage["text"] = text!!
                        newMessage["time"] = time_user
                        newMessage["date"] = date_user
                        newMessage["read"] = "Unread"
                        newMessageDb.updateChildren(newMessage)
                        dialog.dismiss()
                        maxChat--
                        usersDb.child(currentUid).child("MaxChat").setValue(maxChat)
                    }
                    else {Toast.makeText(this@ProfileUserOppositeActivity2, "พิมพ์ข้อความสักหน่อยสิ", Toast.LENGTH_SHORT).show()}
                }
                close.setOnClickListener { dialog.dismiss() }
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(view2)
                val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
                dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
                dialog.show()
            } else {
                val inflater = layoutInflater
                val view = inflater.inflate(R.layout.vip_dialog, null)
                val b1 = view.findViewById<Button>(R.id.buy)
                val b2 = view.findViewById<Button>(R.id.admob)
                val text = view.findViewById<TextView>(R.id.test_de)
                text.text = "ดูโฆษณาเพื่อรับ จำนวนกดทักทาย เพิ่ม \nหรือ สมัคร Dessert VIP เพื่อรับสิทธิพิเศษต่างๆ"
                if(maxadmob <= 0)
                {
                    text.text = "โฆษณาที่คุณสามารถดูได้ในวันนี้หมดแล้ว \n สมัคร Dessert VIP เพื่อรับสิทธิพิเศษ"
                    b2.visibility = View.GONE
                }
                b2.setOnClickListener {
                    if (rewardedAd.isLoaded) {
                        val activityContext: Activity = this
                        val adCallback = object: RewardedAdCallback() {
                            override fun onRewardedAdOpened() {
                                rewardedAd = createAndLoadRewardedAd()
                            }
                            override fun onRewardedAdClosed() {

                            }
                            override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                                maxChat++
                                maxadmob--
                                if(maxChat >= 10)
                                    dialog.dismiss()
                                else if(maxadmob <= 0)
                                    b2.visibility = View.GONE
                                usersDb.child(currentUid).child("MaxChat").setValue(maxChat)
                                usersDb.child(currentUid).child("MaxAdmob").setValue(maxChat)
                            }
                            override fun onRewardedAdFailedToShow(errorCode: Int) {
                                // Ad failed to display.
                            }
                        }
                        rewardedAd.show(activityContext, adCallback)
                    }
                    else {
                        Log.d("TAG", "The rewarded ad wasn't loaded yet.")
                    }
                }
                b1.setOnClickListener {
                    usersDb.child(currentUid).child("Vip").setValue(1)
                    bp.subscribe(this, "YOUR SUBSCRIPTION ID FROM GOOGLE PLAY CONSOLE HERE");
                    dialog.dismiss()
                }
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(view)
                val pagerModels: ArrayList<PagerModel?> = ArrayList()
                pagerModels.add(PagerModel("สมัคร Dessert VIP เพื่อทักทายแบบไม่จำกัดจำนวน","จำนวนคำทักทายของคุณหมด", R.drawable.ic_hand))
                pagerModels.add(PagerModel("ปัดขวาได้เต็มที่ ไม่ต้องรอเวลา","ถูกใจได้ไม่จำกัด", R.drawable.ic_heart))
                pagerModels.add(PagerModel( "คนที่คุณส่งดาวให้จะเห็นคุณก่อนใคร","รับ 5 Star ฟรีทุกวัน",R.drawable.ic_starss))
                pagerModels.add(PagerModel("ดูว่าใครบ้างที่เข้ามากดถูกใจให้คุณ","ใครถูกใจคุณ", R.drawable.ic_love2))
                pagerModels.add(PagerModel( "ดูว่าใครบ้างที่เข้าชมโปรไฟล์ของคุณ","ใครเข้ามาดูโปรไฟล์คุณ",R.drawable.ic_vision))
                val adapter = VipSlide(this@ProfileUserOppositeActivity2, pagerModels)
                val pager: AutoScrollViewPager = dialog.findViewById(R.id.viewpage)
                pager.adapter = adapter
                pager.startAutoScroll()
                val indicator: CircleIndicator = view.findViewById(R.id.indicator)
                indicator.setViewPager(pager)
                val width = (resources.displayMetrics.widthPixels * 0.90) .toInt()
                dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
                dialog.show()
            }
        })
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (chk == 11) {
                    if (i > 1) {
                        val dd = findViewById<View?>(position) as LinearLayout
                        dd.background = ContextCompat.getDrawable(this@ProfileUserOppositeActivity2, R.drawable.image_selected)
                    }
                }
                chk = 0
            }

            override fun onPageSelected(position: Int) {
                val total = adapter.count
                for (jk in total - 1 downTo 0) {
                    if (jk == position) {
                        val dd = findViewById<View?>(position) as LinearLayout
                        dd.background = ContextCompat.getDrawable(this@ProfileUserOppositeActivity2, R.drawable.image_selected)
                    } else {
                        val dd = findViewById<View?>(jk) as LinearLayout
                        dd.background = ContextCompat.getDrawable(this@ProfileUserOppositeActivity2, R.drawable.image_notselector)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

    }
    fun createAndLoadRewardedAd(): RewardedAd {
        val rewardedAd = RewardedAd(this, "ca-app-pub-3940256099942544/5224354917")
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                // Ad successfully loaded.
            }
            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                // Ad failed to load.
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        return rewardedAd
    }
    private fun isConnectionMatches2() {
        val currentuserConnectionDb = usersDb.child(currentUid).child("connection").child("yep").child(matchId)
        currentuserConnectionDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val key = FirebaseDatabase.getInstance().reference.child("Chat").push().key
                    usersDb.child(dataSnapshot.key!!).child("connection").child("matches").child(currentUid).child("ChatId").setValue(key)
                    usersDb.child(currentUid).child("connection").child("matches").child(dataSnapshot.key!!).child("ChatId").setValue(key)
                    usersDb.child(dataSnapshot.key!!).child("connection").child("yep").child(currentUid).setValue(null)
                    usersDb.child(currentUid).child("connection").child("yep").child(dataSnapshot.key!!).setValue(null)
                    if (noti_match == "1") {
                        dialog = Dialog(this@ProfileUserOppositeActivity2)
                        val inflater = layoutInflater
                        val view = inflater.inflate(R.layout.show_match, null)
                        val imageView = view.findViewById<ImageView>(R.id.image_match)
                        val star = view.findViewById<ImageView>(R.id.star)
                        val textView = view.findViewById<TextView>(R.id.textmatch)
                        val textView2 = view.findViewById<TextView>(R.id.io)
                        val textView3 = view.findViewById<TextView>(R.id.textBig)
                        val textView4 = view.findViewById<TextView>(R.id.textmatch2)
                        val button = view.findViewById<Button>(R.id.mess)
                        button.setOnClickListener {
                            dialog.dismiss()
                            val intent = Intent(this@ProfileUserOppositeActivity2, ChatActivity::class.java)
                            val b = Bundle()
                            b.putString("time_chk", time_user)
                            b.putString("matchId", matchId)
                            b.putString("nameMatch", name.text.toString())
                            b.putString("first_chat", "")
                            b.putString("unread", "0")
                            intent.putExtras(b)
                            startActivity(intent)
                        }
                        textView2.setOnClickListener { dialog.dismiss() }
                        textView.text = name.text.toString()
                        if (dataSnapshot.hasChild("super")) {
                            star.visibility = View.VISIBLE
                            //textView3.setText("Star");
                            textView4.text =" ส่งดาวให้คุณให้คุณ"
                        } else textView4.text = " ถูกใจคุณเหมือนกัน"
                        Glide.with(this@ProfileUserOppositeActivity2).load(Url0).into(imageView)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.setContentView(view)
                        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
                        val height = (resources.displayMetrics.heightPixels * 0.90).toInt()
                        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                        dialog.show()
                    }
                } else onBackPressed()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun update(Child: String) {
        usersDb.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //val date_user: String
                //val date_user_before: String
                var date_before:Boolean = true
                /*var date_after = 0
                val currentDate = SimpleDateFormat("dd/MM/yyyy")
                val calendar = Calendar.getInstance()
                date_user = currentDate.format(calendar.time)
                val after = date_user.substring(0, 2)
                date_after = Integer.valueOf(after)*/
                if (dataSnapshot.child(currentUserId).child("PutReportId").hasChild(matchId)) {
                    date_before = false
                }else{
                    val date_user: String
                    val currentDate = SimpleDateFormat("dd/MM/yyyy")
                    val calendar = Calendar.getInstance()
                    date_user = currentDate.format(calendar.time)
                    val ff = hashMapOf<String,Any>()
                    ff["date"] = date_user
                    usersDb.child(currentUserId).child("PutReportId").child(matchId).updateChildren(ff)
                }
                Log.d("test_boolean","$date_before , $matchId")
                if (date_before) {
                    if (return_d == 0) {
                        return_d = -1
                        Alerter.create(this@ProfileUserOppositeActivity2)
                                .setTitle(getString(R.string.report_suc))
                                .setText(getString(R.string.report_suc2))
                                .setBackgroundColorInt(Color.parseColor("#7AFFCF"))
                                .setIcon(ContextCompat.getDrawable(this@ProfileUserOppositeActivity2, R.drawable.ic_check)!!)
                                .show()
                    }
                    if (!dataSnapshot.child(matchId).hasChild("Report")) {
                        val jj = hashMapOf<String,Any>()
                        jj[Child] = "1"
                        usersDb.child(matchId).child("Report").updateChildren(jj)
                    } else if (dataSnapshot.child(matchId).hasChild("Report")) {
                        if (dataSnapshot.child(matchId).child("Report").hasChild(Child)) {
                            val count_rep = Integer.valueOf(dataSnapshot.child(matchId).child("Report").child(Child).value.toString()) + 1
                            val input_count = count_rep.toString()
                            val jj = hashMapOf<String,Any>()
                            jj[Child] = input_count
                            usersDb.child(matchId).child("Report").updateChildren(jj)
                        } else {
                            val jj = hashMapOf<String,Any>()
                            jj[Child] = "1"
                            usersDb.child(matchId).child("Report").updateChildren(jj)
                        }
                    }
                } else {
                    if (return_d == 0) {
                        return_d = -1
                        Alerter.create(this@ProfileUserOppositeActivity2)
                                .setTitle(getString(R.string.report_failed))
                                .setText(getString(R.string.report_fail))
                                .setBackgroundColorInt(Color.parseColor("#FF5050"))
                                .setIcon(ContextCompat.getDrawable(applicationContext, R.drawable.ic_do_not_disturb_black_24dp)!!)
                                .show()
                        val builder = AlertDialog.Builder(this@ProfileUserOppositeActivity2)
                        val view = LayoutInflater.from(this@ProfileUserOppositeActivity2).inflate(R.layout.alert_dialog, null)
                        val title = view.findViewById<View?>(R.id.title_alert) as TextView
                        val li = view.findViewById<View?>(R.id.linear_alert) as LinearLayout
                        val icon = view.findViewById<View?>(R.id.icon_alert) as ImageView
                        val message = view.findViewById<View?>(R.id.message_alert) as TextView
                        val dis = view.findViewById<View?>(R.id.dis_alert) as TextView
                        val yes = view.findViewById<View?>(R.id.yes_alert) as TextView
                        yes.setText(R.string.report_close)
                        li.gravity = Gravity.CENTER
                        dis.visibility = View.GONE
                        title.setText(R.string.report_alert)
                        message.setText(R.string.report_reset)
                        icon.background = ContextCompat.getDrawable(this@ProfileUserOppositeActivity2, R.drawable.ic_warning_black_24dp)
                        builder.setView(view)
                        val mDialog = builder.show()
                        yes.setOnClickListener { mDialog.dismiss() }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun isConnectionMatches(userId: String?) {
        val currentuserConnectionDb = usersDb.child(currentUid).child("connection").child("yep").child(userId!!)
        currentuserConnectionDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val key = FirebaseDatabase.getInstance().reference.child("Chat").push().key
                    usersDb.child(dataSnapshot.key!!).child("connection").child("matches").child(currentUid).child("ChatId").setValue(key)
                    usersDb.child(currentUid).child("connection").child("matches").child(dataSnapshot.key!!).child("ChatId").setValue(key)
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D941FF")))
                    click = false
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun findIamge() {
        findImage = FirebaseDatabase.getInstance().reference.child("Users").child(matchId).child("ProfileImage")
        findImage.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profileImageUrl0")) {
                        Url0 = dataSnapshot.child("profileImageUrl0").value.toString()
                        ++i
                        if (dataSnapshot.hasChild("profileImageUrl1")) {
                            Url1 = dataSnapshot.child("profileImageUrl1").value.toString()
                            ++i
                        } else {
                            Url1 = "null"
                        }
                        if (dataSnapshot.hasChild("profileImageUrl2")) {
                            Url2 = dataSnapshot.child("profileImageUrl2").value.toString()
                            ++i
                        } else {
                            Url2 = "null"
                        }
                        if (dataSnapshot.hasChild("profileImageUrl3")) {
                            Url3 = dataSnapshot.child("profileImageUrl3").value.toString()
                            ++i
                        } else {
                            Url3 = "null"
                        }
                        if (dataSnapshot.hasChild("profileImageUrl4")) {
                            Url4 = dataSnapshot.child("profileImageUrl4").value.toString()
                            ++i
                        } else {
                            Url4 = "null"
                        }
                        if (dataSnapshot.hasChild("profileImageUrl5")) {
                            Url5 = dataSnapshot.child("profileImageUrl5").value.toString()
                            ++i
                        } else {
                            Url5 = "null"
                        }
                        if (i > 1) {
                            for (j in 0 until i) {
                                val layoutParams = LinearLayout.LayoutParams(
                                        50, 12, 0.5f)
                                layoutParams.setMargins(5, 0, 5, 0)
                                val layout = LinearLayout(this@ProfileUserOppositeActivity2)
                                layout.background = ContextCompat.getDrawable(this@ProfileUserOppositeActivity2, R.drawable.image_notselector)
                                layout.layoutParams = LinearLayout.LayoutParams(50, 12)
                                layout.id = j
                                mLinear.addView(layout, layoutParams)
                            }
                        }
                        adapter = ScreenAdapter(this@ProfileUserOppositeActivity2, i, Url0, Url1, Url2, Url3, Url4, Url5, 0)
                        Log.d("111", "1")
                    } else {
                        adapter = ScreenAdapter(this@ProfileUserOppositeActivity2, 1, Url0, Url1, Url2, Url3, Url4, Url5, drawable_gender)
                    }
                } else {
                    adapter = ScreenAdapter(this@ProfileUserOppositeActivity2, 1, Url0, Url1, Url2, Url3, Url4, Url5, drawable_gender)
                }
                viewPager.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val df2: DecimalFormat = DecimalFormat("#.#")
    private fun getUserinfo() {
        mUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("ProfileImage").exists()) {
                    if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")) {
                        Url0 = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()
                        i++
                        if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl1")) {
                            Url1 = dataSnapshot.child("ProfileImage").child("profileImageUrl1").value.toString()
                            i++
                        } else {
                            Url1 = "null"
                        }
                        if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl2")) {
                            Url2 = dataSnapshot.child("ProfileImage").child("profileImageUrl2").value.toString()
                            i++
                        } else {
                            Url2 = "null"
                        }
                        if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl3")) {
                            Url3 = dataSnapshot.child("ProfileImage").child("profileImageUrl3").value.toString()
                            i++
                        } else {
                            Url3 = "null"
                        }
                        if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl4")) {
                            Url4 = dataSnapshot.child("ProfileImage").child("profileImageUrl4").value.toString()
                            i++
                        } else {
                            Url4 = "null"
                        }
                        if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl5")) {
                            Url5 = dataSnapshot.child("ProfileImage").child("profileImageUrl5").value.toString()
                            i++
                        } else {
                            Url5 = "null"
                        }
                        if (i > 1) {
                            for (j in 0 until i) {
                                val layoutParams = LinearLayout.LayoutParams(
                                        50, 12, 0.5f)
                                layoutParams.setMargins(5, 0, 5, 0)
                                val layout = LinearLayout(this@ProfileUserOppositeActivity2)
                                layout.background = ContextCompat.getDrawable(this@ProfileUserOppositeActivity2, R.drawable.image_notselector)
                                layout.layoutParams = LinearLayout.LayoutParams(50, 12)
                                layout.id = j
                                mLinear.addView(layout, layoutParams)
                            }
                        }
                        adapter = ScreenAdapter(this@ProfileUserOppositeActivity2, i, Url0, Url1, Url2, Url3, Url4, Url5, 0)
                        Log.d("111", "1")
                    } else {
                        adapter = ScreenAdapter(this@ProfileUserOppositeActivity2, 1, Url0, Url1, Url2, Url3, Url4, Url5, drawable_gender)
                    }
                } else {
                    adapter = ScreenAdapter(this@ProfileUserOppositeActivity2, 1, Url0, Url1, Url2, Url3, Url4, Url5, drawable_gender)
                }
                viewPager.adapter = adapter
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val map = dataSnapshot.value as MutableMap<*, *>
                    var x: String = dataSnapshot.child("Location").child("X").value.toString()
                    var y: String = dataSnapshot.child("Location").child("Y").value.toString()
                    x_opposite = java.lang.Double.valueOf(x)
                    y_opposite = java.lang.Double.valueOf(y)
                    val dss = MainActivity()
                    val distance = CalculateDistance.calculate(x_user, y_user, x_opposite, y_opposite)
                    val distance1 = df2.format(distance)
                    val ff: Geocoder = if (language_2 == "th") {
                        Geocoder(this@ProfileUserOppositeActivity2)
                    } else {
                        Geocoder(this@ProfileUserOppositeActivity2, Locale.UK)
                    }
                    var addresses: MutableList<Address?>? = null
                    try {
                        addresses = ff.getFromLocation(x_opposite, y_opposite, 1)
                        val city = addresses[0]!!.getAdminArea()
                        city1.text = "$city ,  $distance1 ${getString(R.string.kilometer)}"
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    maxlike = if (dataSnapshot.hasChild("MaxLike")) {
                        val a = dataSnapshot.child("MaxLike").value.toString()
                        a.toInt()
                    } else {
                        0
                    }
                    maxadmob= if (dataSnapshot.hasChild("MaxAdmob")) {
                        val a = dataSnapshot.child("MaxAdmob").value.toString()
                        a.toInt()
                    } else {
                        0
                    }
                    maxstar = if (dataSnapshot.hasChild("MaxStar")) {
                        val a = dataSnapshot.child("MaxStar").value.toString()
                        a.toInt()
                    } else {
                        0
                    }
                    if (map["myself"] != null && map["myself"] != "") {
                        l3.visibility = View.VISIBLE
                        myself.text = map["myself"].toString()
                    }
                    if (map["sex"] != null) {
                        drawable_gender = if (map["sex"].toString() == "Male") {
                            gender.setText(R.string.Male_gender)
                            R.drawable.ic_man
                        } else {
                            gender.setText(R.string.Female_gender)
                            R.drawable.ic_woman
                        }
                    }
                    if (map["Age"] != null) {
                        age.text = map["Age"].toString()
                    }
                    if (map["name"] != null) {
                        name.text = map["name"].toString()
                        report.text = "${getString(R.string.dialog_report)}  ${map["name"].toString()}"
                    }
                    if (map["career"] != null && map["career"] != "") {
                        l1.visibility = View.VISIBLE
                        career.text = map["career"].toString()
                    }
                    if (map["study"] != null && map["study"] != "") {
                        l2.visibility = View.VISIBLE
                        study.text = map["study"].toString()
                    }
                    if (map["language"] != null && map["language"] != "") {
                        l4.visibility = View.VISIBLE
                        val size = dataSnapshot.child("language").childrenCount.toInt()
                        var str = ""
                        for (u in 0 until size) {
                            val position = dataSnapshot.child("language").child("language$u").value.toString().toInt()
                            str += listItems2[position]
                            if (u != size - 1) {
                                str = "$str, "
                            }
                        }
                        language.text = str
                    }
                    if (map["religion"] != null && map["religion"] != "") {
                        l5.visibility = View.VISIBLE
                        religion.text = listItems3.get(map["religion"].toString().toInt())
                    }
                    if (map["hobby"] != null && map["hobby"] != "") {
                        l6.visibility = View.VISIBLE
                        val size = dataSnapshot.child("hobby").childrenCount.toInt()
                        val str = ""
                        for (u in 0 until size) {
                            val position = dataSnapshot.child("hobby").child("hobby$u").value.toString().toInt()
                            addT(listItems.get(position))
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        usersDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                maxChat = dataSnapshot.child(currentUid).child("MaxChat").value.toString().toInt();
                if(dataSnapshot.child(currentUid).child("Vip").value == 1){statusVip = true}
                if (maxChat <= 0)
                {
                    click = false
                }
                if (dataSnapshot.child(matchId).child("connection").child("chatna").hasChild(currentUid)) {
                    fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ProfileUserOppositeActivity2, R.color.text_gray))
                    fab.isClickable = false
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun addT(string: String?) {
        flexboxLayout = findViewById(R.id.Grid_profile)
        params = GridLayout.LayoutParams()
        val textView = TextView(this)
        textView.text = string
        textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.c2))
        textView.setPadding(13, 10, 13, 10)
        textView.setBackgroundResource(R.drawable.tag)
        params.setMargins(10, 10, 10, 10)
        textView.layoutParams = params
        flexboxLayout.addView(textView)
    }

    private fun getdis() {
        /*val userdb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
        userdb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val x: String = dataSnapshot.child("Location").child("X").value.toString()
                val y: String = dataSnapshot.child("Location").child("Y").value.toString()
                x_user = java.lang.Double.valueOf(x)
                y_user = java.lang.Double.valueOf(y)

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })*/
        val preferences = getSharedPreferences("MyUser", Context.MODE_PRIVATE)
        x_user = preferences.getString("X", "").toString().toDouble()
        y_user = preferences.getString("Y", "").toString().toDouble()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    override fun onBillingInitialized() {

    }

    override fun onPurchaseHistoryRestored() {

    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {

    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {

    }


}