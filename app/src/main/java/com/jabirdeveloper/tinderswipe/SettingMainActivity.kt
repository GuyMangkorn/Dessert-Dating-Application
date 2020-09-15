package com.jabirdeveloper.tinderswipe

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.demono.AutoScrollViewPager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jabirdeveloper.tinderswipe.LikeYou.LikeYouActivity
import me.relex.circleindicator.CircleIndicator
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class SettingMainActivity : Fragment(), BillingProcessor.IBillingHandler {
    private lateinit var setting: TextView
    private lateinit var name: TextView
    private lateinit var age: TextView
    private lateinit var mcity: TextView
    private lateinit var count: TextView
    private lateinit var see: TextView
    private lateinit var vip: TextView
    private lateinit var ad: TextView
    private lateinit var setting2: LinearLayout
    private lateinit var edit: LinearLayout
    private lateinit var like_you: LinearLayout
    private lateinit var see_profile_you: LinearLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var imageView: ImageView
    private lateinit var mUserDatabase: DatabaseReference
    private lateinit var p1: ProgressBar
    private lateinit var dialog: Dialog
    private lateinit var dialog2: Dialog
    private lateinit var bp: BillingProcessor
    private var s = 0
    private var c = 0
    private lateinit var vvip: ImageView
    private var statusDialog = false
    private lateinit var setimage: ImageView
    private var gotoProfile = true
    lateinit var rewardedAd: RewardedAd
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_setting_main, container, false)
        super.onCreate(savedInstanceState)
        bp = BillingProcessor(requireContext(), Id.Id, this)
        bp.initialize()
        rewardedAd = RewardedAd(requireActivity(),
                "ca-app-pub-3940256099942544/5224354917")
        val adLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                Toast.makeText(requireContext(), "สวย", Toast.LENGTH_SHORT).show()
            }

            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                Toast.makeText(requireContext(), errorCode.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        val ff = 0
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        dialog = Dialog(requireContext())
        val view2 = inflater.inflate(R.layout.progress_dialog, null)
        dialog2 = Dialog(requireContext())
        dialog2.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog2.setContentView(view2)
        val width = (resources.displayMetrics.widthPixels * 0.80).toInt()
        dialog2.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        imageView = view.findViewById(R.id.pre_Image_porfile)
        name = view.findViewById(R.id.pre_name_profile)
        age = view.findViewById(R.id.pre_age_profile)
        count = view.findViewById(R.id.count_like)
        see = view.findViewById(R.id.see_porfile)
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        mcity = view.findViewById(R.id.aa)
        p1 = view.findViewById(R.id.progress_bar_pre_pro)
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        setting = view.findViewById(R.id.b1)
        setting2 = view.findViewById(R.id.linearLayout20)
        edit = view.findViewById(R.id.linearLayout21)
        like_you = view.findViewById(R.id.like_you)
        see_profile_you = view.findViewById(R.id.see_porfile_you)
        vip = view.findViewById(R.id.vip)
        vvip = view.findViewById(R.id.vvip)
        setimage = view.findViewById(R.id.goto_set_image)
        ad = view.findViewById(R.id.admob_setting)
        vip.setOnClickListener(View.OnClickListener {
            openDialog()
        })
        ad.setOnClickListener {
            if (rewardedAd.isLoaded) {
                val activityContext: Activity = requireActivity()
                val adCallback = object : RewardedAdCallback() {
                    override fun onRewardedAdOpened() {
                        rewardedAd = createAndLoadRewardedAd()
                    }

                    override fun onRewardedAdClosed() {

                    }

                    override fun onUserEarnedReward(@NonNull reward: RewardItem) {

                    }

                    override fun onRewardedAdFailedToShow(errorCode: Int) {
                        // Ad failed to display.
                    }
                }
                rewardedAd.show(activityContext, adCallback)
            } else {
                Log.d("TAG", "The rewarded ad wasn't loaded yet.")
            }
        }
        like_you.setOnClickListener(View.OnClickListener {
            dialog2.show()
            val intent = Intent(context, LikeYouActivity::class.java)
            startActivity(intent)
        })
        see_profile_you.setOnClickListener(View.OnClickListener {
            dialog2.show()
            val intent = Intent(context, LikeYouActivity::class.java)
            intent.putExtra("See", "1")
            startActivity(intent)
        })
        setting2.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, Setting2Activity::class.java)
            startActivityForResult(intent, 15)
        })
        edit.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SettingActivity::class.java)
            startActivityForResult(intent, 14)
        })
        imageView.setOnClickListener(View.OnClickListener {
            if (gotoProfile) {
                val intent = Intent(context, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(context, SettingActivity::class.java)
                startActivity(intent)
            }
        })
        setimage.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SettingActivity::class.java)
            intent.putExtra("setImage", "1")
            startActivity(intent)
        })
        view.findViewById<LinearLayout>(R.id.linearLayout22).setOnClickListener {
//            val intent = Intent(context, SendEmail::class.java)
//            startActivity(intent)
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("dessert2500@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Dessert 1.0.0 ข้อเสนอแนะ")
            intent.putExtra(Intent.EXTRA_TEXT, "มีอะไรก็พูดมา")
            try {
                startActivity(Intent.createChooser(intent, "Choose email"))
            } catch (e: Exception) {

            }
        }

        return view
    }

    @SuppressLint("InflateParams")
    fun openDialog() {

        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.vip_dialog, null)
        val b1 = view.findViewById<Button>(R.id.buy)
        val b2 = view.findViewById<Button>(R.id.admob)
        val text = view.findViewById<TextView>(R.id.test_de)
        if (!statusDialog) {
            b1.setOnClickListener {
                bp.subscribe(requireActivity(), "YOUR SUBSCRIPTION ID FROM GOOGLE PLAY CONSOLE HERE")
                mUserDatabase.child("Vip").setValue(1)
                val MyUser = requireActivity().getSharedPreferences("MyUser", Context.MODE_PRIVATE).edit()
                MyUser.putBoolean("Vip", true)
                MyUser.apply()
                getData()
                dialog.dismiss()
            }
        } else {
            b1.setText(R.string.back)
            b1.setOnClickListener { dialog.dismiss() }
        }
        b2.visibility = View.GONE
        text.text = "สมัคร Desert VIP เพื่อรับสิทธิพิเศษต่างๆ"
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        val pagerModels: ArrayList<PagerModel?> = ArrayList()
        pagerModels.add(PagerModel("ปัดขวาได้เต็มที่ ไม่ต้องรอเวลา", "ถูกใจได้ไม่จำกัด", R.drawable.ic_heart))
        pagerModels.add(PagerModel("ทักทายคนที่คุณอยากทำความรู้จักได้ไม่จำกัดจำนวน", "ทักทายได้ไม่จำกัด", R.drawable.ic_hand))
        pagerModels.add(PagerModel("คนที่คุณส่งดาวให้จะเห็นคุณก่อนใคร", "รับ 5 Star ฟรีทุกวัน", R.drawable.ic_starss))
        pagerModels.add(PagerModel("ดูว่าใครบ้างที่เข้ามากดถูกใจให้คุณ", "ใครถูกใจคุณ", R.drawable.ic_love2))
        pagerModels.add(PagerModel("ดูว่าใครบ้างที่เข้าชมโปรไฟล์ของคุณ", "ใครเข้ามาดูโปรไฟล์คุณ", R.drawable.ic_vision))
        val adapter = VipSlide(requireContext(), pagerModels)
        val pager: AutoScrollViewPager = dialog.findViewById(R.id.viewpage)
        pager.adapter = adapter
        pager.startAutoScroll()
        val indicator: CircleIndicator = view.findViewById(R.id.indicator)
        indicator.setViewPager(pager)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()

        /*val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dfg, null)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        val width = (resources.displayMetrics.widthPixels * 0.90) .toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()*/
    }

    fun createAndLoadRewardedAd(): RewardedAd {
        val rewardedAd = RewardedAd(requireContext(), "ca-app-pub-3940256099942544/5224354917")
        val adLoadCallback = object : RewardedAdLoadCallback() {
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

    private fun getData() {
        val preferences = requireActivity().getSharedPreferences("MyUser", Context.MODE_PRIVATE)
        val gender = if (preferences.getString("image", "") == "Male") {
            R.drawable.ic_man
        } else R.drawable.ic_woman
        if (preferences.getString("image", "")!!.isNotEmpty()) {
            Glide.with(requireContext()).load(preferences.getString("image", ""))
                    .placeholder(R.color.background_gray).listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            p1.visibility = View.GONE
                            return false
                        }
                    })
                    .apply(RequestOptions().override(300, 300)).into(imageView)
        } else {
            gotoProfile = false
            Glide.with(requireContext()).load(gender).placeholder(R.color.background_gray)
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            p1.visibility = View.GONE
                            return false
                        }
                    })
                    .apply(RequestOptions().override(300, 300)).into(imageView)
        }
        if (preferences.getBoolean("Vip", false)) {
            vip.setText(R.string.You_are_vip)
            statusDialog = true
        }
        count.text = preferences.getInt("c", 0).toString()
        see.text = preferences.getInt("s", 0).toString()
        name.text = preferences.getString("name", "")
        age.text = ", " + preferences.getInt("Age", 18).toString()
        val lat_double = preferences.getString("X", "").toString().toDouble()
        val lon_double = preferences.getString("Y", "").toString().toDouble()
        val preferences2 = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val langure = preferences2.getString("My_Lang", "")
        val ff: Geocoder
        ff = if (langure == "th") {
            Geocoder(context)
        } else {
            Geocoder(context, Locale.UK)
        }
        var addresses: MutableList<Address?>? = null
        try {
            addresses = ff.getFromLocation(lat_double, lon_double, 1)
            val city = addresses[0]!!.adminArea
            mcity.text = city
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }


    override fun onResume() {
        super.onResume()
        val handler = Handler()

        getData()



        gotoProfile = true
    }

    override fun onStop() {
        super.onStop()
        dialog2.dismiss()
    }

    override fun onDestroy() {
        bp.release()
        super.onDestroy()
    }

    override fun onBillingInitialized() {

    }

    override fun onPurchaseHistoryRestored() {

    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {

    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 14) {
                getData()
                onAttach(requireContext())
                Log.d("ghj", "1")
            }
        }
    }


}