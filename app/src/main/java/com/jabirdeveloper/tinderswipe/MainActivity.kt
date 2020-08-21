package com.jabirdeveloper.tinderswipe

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails

import com.bumptech.glide.Glide
import com.github.demono.AutoScrollViewPager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.jabirdeveloper.tinderswipe.Cards.arrayAdapter
import com.jabirdeveloper.tinderswipe.Cards.cards
import com.jabirdeveloper.tinderswipe.Chat.ChatActivity
import com.yuyakaido.android.cardstackview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.relex.circleindicator.CircleIndicator
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : Fragment(), LocationListener, BillingProcessor.IBillingHandler  {

    private lateinit var mLocationManager: LocationManager
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGPSDialog: Dialog
    private lateinit var oppositUserSex: String
    private var dis: String? = null
    private val xx = true
    private var OppositeUserAgeMin = 0
    private var OppositeUserAgeMax = 0
    private var age = 0
    private lateinit var arrayAdapter: arrayAdapter
    private lateinit var usersDb: DatabaseReference
    private lateinit var get_status: DatabaseReference
    private var distance = 0.0
    private var x_user = 0.0
    private var y_user = 0.0
    private var distance_1 = 0.0
    private lateinit var like: FloatingActionButton
    private lateinit var dislike: FloatingActionButton
    private lateinit var star: FloatingActionButton
    private lateinit var layout_gps: ConstraintLayout
    private lateinit var anime1: ImageView
    private lateinit var anime2: ImageView
    private lateinit var textgps: TextView
    private lateinit var textgps2: TextView
    private lateinit var handler: Handler
    private var maxlike = 0
    private var maxstar = 0
    private var maxadmob = 0
    private lateinit var dialog: Dialog
    private var status_vip = false
    private lateinit var rowItem: ArrayList<cards>
    private lateinit var po: cards
    private lateinit var currentUid: String
    private var time_send: String? = null
    private lateinit var touch_gps: ImageView
    private var noti_match: String? = null
    private lateinit var cardStackView: CardStackView
    lateinit var manager:CardStackLayoutManager
    private  var functions = Firebase.functions
    lateinit var rewardedAd: RewardedAd
    private lateinit var bp: BillingProcessor
    private var countLimit = 0
    private var countLimit2 = 0
    private var countLimit3 = 1
    private var countDataSet = 100
    private var countDataSefdfffft = 100
    private lateinit var resultlimit : ArrayList<*>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("ghj","สร้างละ")
        val view = inflater.inflate(R.layout.activity_main, container, false)
        val view2 = inflater.inflate(R.layout.item, container, false)
        bp = BillingProcessor(requireContext(), Id.Id, this)
        bp.initialize()
        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser == null) {
            val intent = Intent(context, ChooseLoginRegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        val data = hashMapOf(
                "sex" to "Male",
                "min" to 18,
                "max" to 30
        )

        layout_gps = view.findViewById(R.id.layout_in)
        textgps = view.findViewById(R.id.textView8)
        textgps2 = view.findViewById(R.id.textView9)
        touch_gps = view.findViewById(R.id.imageView3)
        textgps.setText(R.string.touch_settings)
        textgps2.setText(R.string.Area)
        like = view.findViewById(R.id.like_button)
        dislike = view.findViewById(R.id.dislike_button)
        star = view.findViewById(R.id.star_button)
        anime1 = view.findViewById(R.id.anime1)
        anime2 = view.findViewById(R.id.anime2)
        rowItem = ArrayList()
        currentUid = mAuth.currentUser!!.uid
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")
        mLocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf<String?>(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            ), 1)
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0f, this)
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) { // launch a new coroutine in background and continue
                getdis()
            }
        }
        cardStackView = view.findViewById(R.id.frame)
        manager = CardStackLayoutManager(context, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {
                val data = hashMapOf(
                        "sex" to oppositUserSex,
                        "min" to OppositeUserAgeMin,
                        "max" to OppositeUserAgeMax,
                        "x_user" to x_user,
                        "y_user" to y_user,
                        "vip" to status_vip,
                        "distance" to distance

                )
               /* functions.getHttpsCallable("get2223")
                        .call()
                        .addOnFailureListener { Log.d("ghj","failed") }
                        .addOnSuccessListener {  task ->
                            // This continuation runs on either success or failure, but if the task
                            // has failed then result will throw an Exception which will be
                            // propagated down.
                            val result5 = task.data as Map<*,*>
                            val result6 = result5["parse_obj"] as Map<*,*>
                            Log.d("ghj",result6.toString())
                        }*/
            }
            override fun onCardSwiped(direction: Direction?) {
                po = rowItem.get(manager.topPosition - 1)
                val UserId = po.userId!!
                val calendar = Calendar.getInstance()
                val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
                val time_user = currentTime.format(calendar.time)
                val currentDate = SimpleDateFormat("dd/MM/yyyy")
                val date_user = currentDate.format(calendar.time)

                if (direction == Direction.Right) {
                    if (maxlike > 0 || status_vip) {
                        val Datetime = hashMapOf<String, Any>()
                        Datetime["date"] = date_user
                        Datetime["time"] = time_user
                        usersDb.child(UserId).child("connection").child("yep").child(currentUid).updateChildren(Datetime)
                        maxlike--
                        usersDb.child(currentUid).child("MaxLike").setValue(maxlike)
                        isConnectionMatches(UserId)
                    }
                    else {
                        handler.postDelayed(Runnable { cardStackView.rewind() }, 200)
                        openDialog()
                    }
                }
                if (direction == Direction.Left) {
                    usersDb.child(UserId).child("connection").child("nope").child(currentUid).setValue(true)
                }
                if (direction == Direction.Top) {
                    if (maxstar > 0 || status_vip) {
                        val Datetime = hashMapOf<String, Any>()
                        Datetime["date"] = date_user
                        Datetime["time"] = time_user
                        Datetime["super"] = true
                        usersDb.child(UserId).child("connection").child("yep").child(currentUid).updateChildren(Datetime)
                        usersDb.child(currentUid).child("star_s").child(UserId).setValue(true)
                        maxstar--
                        usersDb.child(currentUid).child("MaxStar").setValue(maxstar)
                        isConnectionMatches(UserId)
                    }
                    else {
                        handler.postDelayed(Runnable { cardStackView.rewind() }, 200)
                        openDialog()
                    }
                }

                if (arrayAdapter.itemCount <= 1) {
                    layout_gps.visibility = View.VISIBLE
                }
            }

            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View?, position: Int) {
                Log.d("ggg", "$position $countLimit $countLimit3 " + rowItem.size)

                if(countLimit2 == 5 && countLimit< countDataSet)
                {
                    getUser(resultlimit,false,rowItem.size,5)
                    countLimit2=0
                }
                if(countLimit3%countDataSet == 0 && countLimit3>0)
                {
                    val handler = Handler()
                    handler.postDelayed({
                        callFunctions(countDataSet,false,rowItem.size)
                        countLimit=0
                        countLimit2=0
                    }, 300)

                }
                if (arrayAdapter.itemCount >= 1) {
                    layout_gps.visibility = View.GONE
                }
            }

            override fun onCardDisappeared(view: View?, position: Int) {
                Log.d("ggg", "$position $countLimit $countLimit3 " + rowItem.size)
                countLimit2++
                countLimit3++
            }
        })
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(2)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        arrayAdapter = arrayAdapter(rowItem, context, this)
        cardStackView.layoutManager = manager
        cardStackView.adapter = arrayAdapter
        cardStackView.itemAnimator = DefaultItemAnimator()
        like.setOnClickListener(View.OnClickListener {
                val setting = SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(AccelerateInterpolator())
                        .build()
                manager.setSwipeAnimationSetting(setting)
                cardStackView.swipe()
        })
        dislike.setOnClickListener(View.OnClickListener {
                val setting = SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(AccelerateInterpolator())
                        .build()
                manager.setSwipeAnimationSetting(setting)
                cardStackView.swipe()
        })
        touch_gps.setOnClickListener(View.OnClickListener { startActivityForResult(Intent(context, Setting2Activity::class.java), 1112) })
        star.setOnClickListener(View.OnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Top)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        })
        handler = Handler()
        runnable!!.run()
        rewardedAd = RewardedAd(requireActivity(),
                "ca-app-pub-3940256099942544/5224354917")
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                Toast.makeText(requireContext(), "สวย", Toast.LENGTH_SHORT).show()
            }
            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                Toast.makeText(requireContext(), errorCode.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        Log.d("458", this::manager.isInitialized.toString())

        return view
    }
    fun aa ()
    {
        val data = hashMapOf(
                "sex" to "Male",
                "min" to 18,
                "max" to 30
        )
        functions.getHttpsCallable("get2222")
                .call(data)
                .addOnFailureListener { Log.d("ghj","failed") }
                .addOnSuccessListener {  task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then result will throw an Exception which will be
                    // propagated down.
                    val result1 = task.data as Map<*, *>
                    Log.d("ghj",result1.toString())

                }
    }
    fun createAndLoadRewardedAd(): RewardedAd {
        rewardedAd = RewardedAd(requireActivity(),
                "ca-app-pub-3940256099942544/5224354917")
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                Toast.makeText(requireContext(), "สวย", Toast.LENGTH_SHORT).show()
            }
            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                Toast.makeText(requireContext(), errorCode.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        return rewardedAd
    }
    fun openDialog() {
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.vip_dialog, null)
        val b1 = view.findViewById<Button>(R.id.buy)
        val b2 = view.findViewById<Button>(R.id.admob)
        val text = view.findViewById<TextView>(R.id.test_de)
        if(maxadmob <= 0)
        {
            text.text = "โฆษณาที่คุณสามารถดูได้ในวันนี้หมดแล้ว \n สมัคร Dessert VIP เพื่อรับสิทธิพิเศษ"
            b2.visibility = View.GONE
        }
        b2.setOnClickListener {
            if (rewardedAd.isLoaded) {
                val activityContext: Activity = requireActivity()
                val adCallback = object: RewardedAdCallback() {
                    override fun onRewardedAdOpened() {
                        rewardedAd = createAndLoadRewardedAd()
                    }
                    override fun onRewardedAdClosed() {

                    }
                    override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                        Log.d("TAG", maxlike.toString())
                        maxlike += 1
                        maxadmob -= 1
                        if(maxlike >= 10)
                            dialog.dismiss()
                        else if(maxadmob <= 0)
                        {
                            b2.visibility = View.GONE
                        }

                        usersDb.child(currentUid).child("MaxLike").setValue(maxlike)
                        usersDb.child(currentUid).child("MaxAdmob").setValue(maxadmob)
                    }
                    override fun onRewardedAdFailedToShow(errorCode: Int) {
                        Log.d("TAG", "The rewarded ad wasn't loaded yet.")
                    }
                }
                rewardedAd.show(activityContext, adCallback)
            }
            else {
                Log.d("TAG", "The rewarded ad wasn't loaded yet.")
            }
        }
        b1.setOnClickListener {
            bp.subscribe(requireActivity(), "YOUR SUBSCRIPTION ID FROM GOOGLE PLAY CONSOLE HERE");
            usersDb.child(currentUid).child("Vip").setValue(true)
            dialog.dismiss()
            requireActivity().finish()
            requireActivity().overridePendingTransition(0, 0)
            requireActivity().startActivity(requireActivity().intent)
            requireActivity().overridePendingTransition(0, 0)
        }
        dialog = Dialog(requireContext())
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        val pagerModels: ArrayList<PagerModel?> = ArrayList()
        pagerModels.add(PagerModel("สมัคร Desert เพื่อกดถูกใจได้ไม่จำกัด ปัดขวาได้เต็มที่ ไม่ต้องรอเวลา","จำนวนการกดถูกใจของคุณหมด", R.drawable.ic_heart))
        pagerModels.add(PagerModel( "คนที่คุณส่งดาวให้จะเห็นคุณก่อนใคร","รับ 5 Star ฟรีทุกวัน",R.drawable.ic_starss))
        pagerModels.add(PagerModel( "สามารถทักทายได้เต็มที ไม่จำกัดจำนวน","ทักทายได้ไม่จำกัด",R.drawable.ic_hand))
        pagerModels.add(PagerModel("ดูว่าใครบ้างที่เข้ามากดถูกใจให้คุณ","ใครถูกใจคุณ", R.drawable.ic_love2))
        pagerModels.add(PagerModel( "ดูว่าใครบ้างที่เข้าชมโปรไฟล์ของคุณ","ใครเข้ามาดูโปรไฟล์คุณ",R.drawable.ic_vision))
        val adapter = VipSlide(requireContext(), pagerModels)
        val pager: AutoScrollViewPager = dialog.findViewById(R.id.viewpage)
        pager.adapter = adapter
        pager.startAutoScroll()
        val indicator: CircleIndicator = view.findViewById(R.id.indicator)
        indicator.setViewPager(pager)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    private val runnable: Runnable? = object : Runnable {
        override fun run() {
            anime1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(800).withEndAction {
                anime1.scaleX = 1f
                anime1.scaleY = 1f
                anime1.alpha = 1f
            }
            anime2.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1200).withEndAction {
                anime2.scaleX = 1f
                anime2.scaleY = 1f
                anime2.alpha = 1f
            }
            handler.postDelayed(this, 1500)
        }
    }

    private fun isConnectionMatches(userId: String) {
        val currentuserConnectionDb = usersDb.child(currentUid).child("connection").child("yep").child(userId)
        currentuserConnectionDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val key = FirebaseDatabase.getInstance().reference.child("Chat").push().key
                    usersDb.child(dataSnapshot.key!!)
                            .child("connection").child("matches")
                            .child(currentUid).child("ChatId")
                            .setValue(key)
                    usersDb.child(currentUid)
                            .child("connection")
                            .child("matches")
                            .child(dataSnapshot.key!!)
                            .child("ChatId").setValue(key)
                    usersDb.child(dataSnapshot.key!!)
                            .child("connection")
                            .child("yep")
                            .child(currentUid)
                            .setValue(null)
                    usersDb.child(currentUid)
                            .child("connection")
                            .child("yep")
                            .child(dataSnapshot.key!!)
                            .setValue(null)
                    if (noti_match == "1") {
                        dialog = Dialog(requireContext())
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
                            val intent = Intent(context, ChatActivity::class.java)
                            val b = Bundle()
                            b.putString("time_chk", time_send)
                            b.putString("matchId", po.userId)
                            b.putString("nameMatch", po.name)
                            b.putString("first_chat", "")
                            b.putString("unread", "0")
                            intent.putExtras(b)
                            requireContext().startActivity(intent)
                        }
                        textView2.setOnClickListener { dialog.dismiss() }
                        textView.text = po.name
                        if (dataSnapshot.hasChild("super")) {
                            star.visibility = View.VISIBLE
                            //textView3.setText("Star");
                            textView4.text = "  " + "ส่งดาวให้คุณให้คุณ"
                        } else textView4.text = "  " + "ถูกใจคุณเหมือนกัน"
                        Glide.with(requireContext()).load(po.profileImageUrl).into(imageView)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.setContentView(view)
                        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                        dialog.show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getdis() {
        val preferences = requireContext().getSharedPreferences("MyUser", Context.MODE_PRIVATE)
        oppositUserSex = preferences.getString("OppositeUserSex", "All").toString()
        OppositeUserAgeMin = preferences.getInt("OppositeUserAgeMin", 0)
        OppositeUserAgeMax = preferences.getInt("OppositeUserAgeMax", 0)

        x_user = preferences.getString("X", "").toString().toDouble()
        y_user = preferences.getString("Y", "").toString().toDouble()
        maxlike = preferences.getInt("MaxLike", 0)
        maxadmob = preferences.getInt("MaxAdmob", 0)
        maxstar = preferences.getInt("MaxStar", 0)
        status_vip = preferences.getBoolean("Vip", false)
        distance = when (preferences.getString("Distance", "Untitled")) {
            "true" -> {
                1000.0
            }
            "Untitled" -> {
                1000.0
            }
            else -> {
                preferences.getString("Distance", "Untitled").toString().toDouble()
            }
        }
       // getOppositSexUser()
        callFunctions(countDataSet,true,0)



    }
    private fun callFunctions(limit:Int,type:Boolean,count:Int)
    {
        val preferences2 = requireActivity().getSharedPreferences("notification_match", Context.MODE_PRIVATE)
        noti_match = preferences2.getString("noti", "1")
        var pre = 0
        if(!type) pre = 0
        val data = hashMapOf(
                "sex" to oppositUserSex,
                "min" to OppositeUserAgeMin,
                "max" to OppositeUserAgeMax,
                "x_user" to x_user,
                "y_user" to y_user,
                "vip" to status_vip,
                "distance" to distance,
                "limit" to pre+limit,
                "prelimit" to pre
        )
        Log.d("tagkl",data.toString())
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) { // launch a new coroutine in background and continue
            status("offline", currentUid)
        }
        functions.getHttpsCallable("get2222")
                .call(data)
                .addOnFailureListener { Log.d("ghj","failed") }
                .addOnSuccessListener {  task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then result will throw an Exception which will be
                    // propagated down.
                    val result1 = task.data as Map<*, *>
                    Log.d("ghjlast",result1.toString())
                    resultlimit = result1["o"] as ArrayList<*>
                    if(resultlimit.isNotEmpty())
                    getUser(resultlimit,type,count,10)



                }
    }

    private fun getUser(result2:ArrayList<*>,type:Boolean,count:Int,limit: Int)
    {
        val preferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val langure = preferences.getString("My_Lang", "")
        val ff: Geocoder
        var addresses: MutableList<Address?>? = null
        ff = if (langure == "th") {
            Geocoder(context)
        } else {
            Geocoder(context, Locale.UK)
        }
        Log.d("iop","")
        var a = countLimit+limit
        if(result2.size < countDataSet)
            a = result2.size
        for(x in countLimit until a)
        {
            countLimit++
            Log.d("iop","$countLimit ${result2.size}")
            val user = result2[x] as Map<*, *>
            Log.d("ghj", user["name"].toString() + " , "+user["distance_other"].toString())
            var myself = ""
            var citysend: String? = ""
            var off_status = false
            var vip = false
            var star_s = false
            val location = user["Location"] as Map<*, *>
            try {
                addresses = ff.getFromLocation(location["X"].toString().toDouble(), location["Y"].toString().toDouble(), 1)
                val city = addresses[0]!!.adminArea
                citysend = city
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (user["myself"] != null) {
                myself = user["myself"].toString()
            }
            if (user["off_status"] != null) {
                off_status = true
            }
            (user["ProfileImage"] as Map<*, *>)["profileImageUrl0"]
            val profileImageUrl = (user["ProfileImage"] as Map<*, *>)["profileImageUrl0"].toString()

            var status = "offline"
            (user["Status"] as Map<*, *>)["status"]
            if ((user["Status"] as Map<*, *>)["status"] != null) {
                status = (user["Status"] as Map<*, *>)["status"].toString()
            }
            if (user["Vip"] != null) {
                vip = true
            }

            if (user["star_s"] != null) {
                if((user["star_s"] as Map<*, *>)[currentUid] != null)
                    star_s = true
            }
            dis = df2.format(user["distance_other"])
            rowItem.add(cards(user["key"].toString()
                        , user["name"].toString()
                        , profileImageUrl
                        , user["Age"].toString()
                        , dis
                        , citysend
                        , status
                        , myself
                        , off_status
                        , vip
                        , star_s))

        }

        if(type)
        arrayAdapter.notifyDataSetChanged()
        else {
            arrayAdapter.notifyItemRangeChanged(count,rowItem.size)
        }
    }


    private val df2: DecimalFormat = DecimalFormat("#.#")
    fun getOppositSexUser() {
        var getuser: Query? = usersDb
        getuser = usersDb.orderByChild("Age").startAt(OppositeUserAgeMin.toDouble()).endAt(OppositeUserAgeMax.toDouble())
        getuser.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {

                val x = dataSnapshot.child("Location").child("X").value.toString().toDouble()
                val y = dataSnapshot.child("Location").child("Y").value.toString().toDouble()
                val dsv = MainActivity()
                distance_1 = dsv.Calllat(x_user, y_user, x, y)
                if (currentUid != dataSnapshot.key && distance_1 <= distance
                        && !dataSnapshot.child("connection").child("matches").hasChild(currentUid)
                        && !dataSnapshot.child("connection").child("yep").hasChild(currentUid)
                        && !dataSnapshot.child("connection").child("nope").hasChild(currentUid)
                        && dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")
                        && !dataSnapshot.hasChild("off_card")) {
                    dis = df2.format(distance_1)
                    val preferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
                    val langure = preferences.getString("My_Lang", "")
                    val ff: Geocoder
                    var myself = ""
                    var citysend: String? = ""
                    var off_status = false
                    var vip = false
                    var star_s = false

                    ff = if (langure == "th") {
                        Geocoder(context)
                    } else {
                        Geocoder(context, Locale.UK)
                    }
                    var addresses: MutableList<Address?>? = null
                    try {
                        addresses = ff.getFromLocation(x, y, 1)
                        val city = addresses[0]!!.adminArea
                        citysend = city
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    if (dataSnapshot.hasChild("myself")) {
                        myself = dataSnapshot.child("myself").value.toString()
                    }
                    if (dataSnapshot.hasChild("off_status")) {
                        off_status = true
                    }

                    val profileImageUrl = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()

                    var status = "offline"
                    if (dataSnapshot.child("Status").hasChild("status")) {
                        status = dataSnapshot.child("Status").child("status").value.toString()
                    }
                    if (dataSnapshot.hasChild("Vip")) {
                        vip = true
                    }
                    if (dataSnapshot.child("star_s").hasChild(currentUid)) {
                        star_s = true
                    }
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) { // launch a new coroutine in background and continue


                        if (oppositUserSex == "All") {
                            if (dataSnapshot.exists() && distance_1 < distance) {
                                rowItem.add(cards(dataSnapshot.key
                                        , dataSnapshot.child("name").value.toString()
                                        , profileImageUrl
                                        , dataSnapshot.child("Age").value.toString()
                                        , dis
                                        , citysend
                                        , status
                                        , myself
                                        , off_status
                                        , vip
                                        , star_s))
                            }
                        } else {
                            if (dataSnapshot.exists()
                                    && dataSnapshot.child("sex").value.toString() == oppositUserSex && distance_1 < distance) {
                                rowItem.add(cards(dataSnapshot.key
                                        , dataSnapshot.child("name").value.toString()
                                        , profileImageUrl
                                        , dataSnapshot.child("Age").value.toString()
                                        , dis
                                        , citysend
                                        , status
                                        , myself
                                        , off_status
                                        , vip
                                        , star_s))
                            }
                        }


                    }

                    /*   rowItem.sortWith(Comparator { o1, o2 ->
                           var i = 0
                           var j = 0
                           if (o1!!.getVip()) i = 1
                           if (o2!!.getVip()) j = 1
                           j - i
                       })
                       if (star_s) {
                           Log.d("dda", dataSnapshot.child("name").value.toString())
                           rowItem.sortWith(Comparator { o1, o2 ->
                               var i = 0
                               var j = 0
                               if (o1!!.getStar()) i = 1
                               if (o2!!.getStar()) j = 1
                               j - i
                           })
                       }*/

                    arrayAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

        })
        /*usersDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                if (currentUid != dataSnapshot.key
                        && age <= OppositeUserAgeMax && age >= OppositeUserAgeMin && !dataSnapshot.child("connection").child("matches").hasChild(currentUid)
                        && !dataSnapshot.child("connection").child("yep").hasChild(currentUid)
                        && !dataSnapshot.child("connection").child("nope").hasChild(currentUid)
                        && dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")
                        && !dataSnapshot.hasChild("off_card")) {
                    val x: String
                    val y: String
                    var myself = ""
                    var citysend: String? = ""
                    var off_status = false
                    var vip = false
                    var star_s = false
                    if (dataSnapshot.hasChild("Location")) {
                        if (dataSnapshot.child("Location").child("X").value != null && dataSnapshot.child("Location").child("Y").value != null) {
                            x = dataSnapshot.child("Location").child("X").value.toString()
                            y = dataSnapshot.child("Location").child("Y").value.toString()
                            x_opposite = java.lang.Double.valueOf(x)
                            y_opposite = java.lang.Double.valueOf(y)
                            val dsv = MainActivity()
                            distance_1 = dsv.Calllat(x_user, y_user, x_opposite, y_opposite)
                            dis = df2.format(distance_1)
                            val preferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
                            val langure = preferences.getString("My_Lang", "")
                            val ff: Geocoder
                            ff = if (langure == "th") {
                                Geocoder(context)
                            } else {
                                Geocoder(context, Locale.UK)
                            }
                            var addresses: MutableList<Address?>? = null
                            try {
                                addresses = ff.getFromLocation(x_opposite, y_opposite, 1)
                                val city = addresses[0]!!.adminArea
                                citysend = city
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    if (dataSnapshot.hasChild("myself")) {
                        myself = dataSnapshot.child("myself").value.toString()
                    }
                    if (dataSnapshot.hasChild("off_status")) {
                        off_status = true
                    }
                    var profileImageUrl = "default"
                    if (dataSnapshot.hasChild("ProfileImage")) {
                        profileImageUrl = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()
                    }
                    var status = "offline"
                    if (dataSnapshot.child("Status").hasChild("status")) {
                        status = dataSnapshot.child("Status").child("status").value.toString()
                    }
                    if (dataSnapshot.hasChild("Vip")) {
                        vip = true
                    }
                    if (dataSnapshot.child("star_s").hasChild(currentUid)) {
                        star_s = true
                    }
                    if (oppositUserSex == "All") {
                        if (dataSnapshot.exists() && distance_1 < distance) {
                            rowItem.add(cards(dataSnapshot.key
                                    , dataSnapshot.child("name").value.toString()
                                    , profileImageUrl
                                    , dataSnapshot.child("Age").value.toString()
                                    , dis
                                    , citysend
                                    , status
                                    , myself
                                    , off_status
                                    , vip
                                    , star_s))
                        }
                    } else {
                        if (dataSnapshot.exists()
                                && dataSnapshot.child("sex").value.toString() == oppositUserSex && distance_1 < distance) {
                            rowItem.add(cards(dataSnapshot.key
                                    , dataSnapshot.child("name").value.toString()
                                    , profileImageUrl
                                    , dataSnapshot.child("Age").value.toString()
                                    , dis
                                    , citysend
                                    , status
                                    , myself
                                    , off_status
                                    , vip
                                    , star_s))
                        }
                    }
                 /*   rowItem.sortWith(Comparator { o1, o2 ->
                        var i = 0
                        var j = 0
                        if (o1!!.getVip()) i = 1
                        if (o2!!.getVip()) j = 1
                        j - i
                    })
                    if (star_s) {
                        Log.d("dda", dataSnapshot.child("name").value.toString())
                        rowItem.sortWith(Comparator { o1, o2 ->
                            var i = 0
                            var j = 0
                            if (o1!!.getStar()) i = 1
                            if (o2!!.getStar()) j = 1
                            j - i
                        })
                    }*/
                    arrayAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })*/
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        val Locationinfo_data = FirebaseDatabase.getInstance().reference.child("Users").child(currentUid).child("Location")
        val locationinfo= hashMapOf<String,Any>()
        locationinfo["X"] = latitude
        locationinfo["Y"] = longitude
        Locationinfo_data.updateChildren(locationinfo)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {
        if (provider == LocationManager.GPS_PROVIDER) {
            showGPSDiabledDialog()
        }
    }

    fun showGPSDiabledDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.GPS_Disabled)
        builder.setMessage(R.string.GPS_open)
        builder.setPositiveButton(R.string.open_gps) { dialog, which -> startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0) }.setNegativeButton(R.string.report_close) { _, _ ->
            val intent = Intent(context, show_gps_open::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            requireActivity().finish()
            startActivity(intent)
        }
        mGPSDialog = builder.create()
        mGPSDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.myrect2))
        mGPSDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            requireActivity().recreate()
            if (mLocationManager == null) {
                mLocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            }
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDiabledDialog()
            }
        }
        if (requestCode == 1112) {
            handler.postDelayed(Runnable {
                requireActivity().finish()
                requireActivity().overridePendingTransition(0, 0)
                requireActivity().startActivity(requireActivity().intent)
                requireActivity().overridePendingTransition(0, 0)
            }, 400)
        }
        if (requestCode == 115) {
            when(resultCode)
            {
                1 -> Like()
                2 -> DisLike()
                3 -> star()
            }
        }
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requireActivity().recreate()
                getdis()
            }
        }
    }

    fun Calllat(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val lonlon = Math.toRadians(lon2 - lon1)
        val latlat = Math.toRadians(lat2 - lat1)
        val lat1r = Math.toRadians(lat1)
        val lat2r = Math.toRadians(lat2)
        val R = 6371.0
        val a = sin(latlat / 2) * sin(latlat / 2) + cos(lat1r) * cos(lat2r) * sin(lonlon / 2) * sin(lonlon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    private val idd = 0
    fun status(Status_User: String, current: String) {
        val date_user: String
        val time_user: String
        get_status = FirebaseDatabase.getInstance().reference.child("Users").child(current).child("Status")
        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd/MM/yyyy")
        date_user = currentDate.format(calendar.time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
        time_user = currentTime.format(calendar.time)
        time_send = time_user
        if (Status_User == "offline") {
            val status_up = HashMap<String?, Any?>()
            status_up["date"] = date_user
            status_up["status"] = Status_User
            status_up["time"] = time_user
            get_status.updateChildren(status_up)
        } else {
            val status_up = HashMap<String?, Any?>()
            status_up["status"] = Status_User
            get_status.updateChildren(status_up)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("4581", this::manager.isInitialized.toString())


    }
    override fun onResume() {
        super.onResume()
        status("online", currentUid)
    }

    override fun onPause() {
        super.onPause()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) { // launch a new coroutine in background and continue
            status("offline", currentUid)
        }
        mLocationManager.removeUpdates(this)
    }
    private fun Like() {
        val handler = Handler()
        handler.postDelayed({
            val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }, 300)


    }
    private fun DisLike() {
        val handler = Handler()
        handler.postDelayed({
            val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }, 300)
    }
    private fun star() {
        val handler = Handler()
        handler.postDelayed({
            val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Top)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }, 300)
    }

    override fun onBillingInitialized() {

    }

    override fun onPurchaseHistoryRestored() {

    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {

    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {

    }
    override fun onDestroy() {
        bp.release()
        super.onDestroy()
    }

}