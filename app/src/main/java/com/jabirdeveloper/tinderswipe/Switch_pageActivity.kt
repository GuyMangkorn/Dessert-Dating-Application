package com.jabirdeveloper.tinderswipe

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.jabirdeveloper.tinderswipe.Listcard.ListcardActivity
import com.jabirdeveloper.tinderswipe.Matches.MatchesActivity
import com.jabirdeveloper.tinderswipe.QAStore.ExampleClass
import com.jabirdeveloper.tinderswipe.QAStore.QAObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Switch_pageActivity : AppCompatActivity() {
    private lateinit var selectedFragment: Fragment
    private lateinit var bottomNav: BottomNavigationView
    private var id = R.id.item2
    private var language: String? = null
    private val Accept: String? = null
    private var First: String = ""
    private lateinit var dialog: Dialog
    lateinit var mInterstitialAd: InterstitialAd
    lateinit var rewardedAd: RewardedAd
    private val page1 = SettingMainActivity()
    private val page2 = MainActivity()
    private val page3 = ListcardActivity()
    private val page4 = MatchesActivity()
    private  var functions = Firebase.functions
    private var activeFragment: Fragment = MainActivity()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocal()
        setContentView(R.layout.activity_switch_page)
        getDataOncall()
        getMyUser()
        /*MobileAds.initialize(this) {}
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.show()*/
        rewardedAd = RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917")
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                Toast.makeText(this@Switch_pageActivity, "สวย", Toast.LENGTH_SHORT).show()
            }
            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                Toast.makeText(this@Switch_pageActivity, errorCode.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        bar = findViewById(R.id.bar2)
        if (intent.hasExtra("warning")) {
            val choice = this.resources.getStringArray(R.array.report_item)
            var nameAndValue = ""
            val name = intent.getStringArrayListExtra("warning")
            val value = intent.getIntegerArrayListExtra("warning_value")
            for (i in name.indices) {
                nameAndValue += "${i + 1}.${choice[Integer.valueOf(name[i])]}${getString(R.string.count_report)}	${value[i]} ${getString(R.string.times)}"
            }

            val inflater = layoutInflater
            val view = inflater.inflate(R.layout.warning_dialog, null)
            dialog = Dialog(this@Switch_pageActivity)
            val textView = view.findViewById<TextView>(R.id.text_warning)
            textView.text = nameAndValue
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(view)
            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
            dialog.show()
        }
        if (intent.hasExtra("first")) {
            First = intent.getStringExtra("first")
            if (First != "0") {
                bar!!.showBadge(R.id.item4, Integer.valueOf(First))
            }
            id = R.id.item2
            intent.removeExtra("first")
        }
        if (intent.hasExtra("accept")) {
            id = R.id.item4
            intent.removeExtra("accept")
        }
        if (intent.hasExtra("back")) {
            id = R.id.item1
            intent.removeExtra("back")
        }

        bar!!.setOnItemSelectedListener(object : ChipNavigationBar.OnItemSelectedListener {
            override fun onItemSelected(i: Int) {
                Log.d("num", i.toString())
                if (isOnline(applicationContext)) {
                    when (i) {
                        R.id.item1 -> {
                            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).hide(activeFragment).show(page1).commit()
                            activeFragment = page1
                            true
                        }
                        R.id.item2 -> {
                            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).hide(activeFragment).show(page2).commit()
                            activeFragment = page2
                            true
                        }
                        R.id.item3 -> {
                            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).hide(activeFragment).show(page3).commit()
                            activeFragment = page3
                            true
                        }
                        R.id.item4 -> {
                            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).hide(activeFragment).show(page4).commit()
                            activeFragment = page4
                            true
                        }
                        else -> false
                    }
                } else {
                    val builder = AlertDialog.Builder(this@Switch_pageActivity)
                    builder.setTitle("Internet ของคุณปิดอยุ่")
                    builder.setMessage("กรุณาเปิด Internet บนอุปกรณ์ของคุณเพื่อใช้งานแอปพลิเคชัน")
                    builder.setPositiveButton("เปิด internet") { dialog, which ->
                        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(intent)
                    }
                            .setNegativeButton("ปิด app") { _, _ ->
                                val intent = Intent(this@Switch_pageActivity, show_gps_open::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                finish()
                                startActivity(intent)
                            }
                    val mGPSDialog: Dialog = builder.create()
                    mGPSDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@Switch_pageActivity, R.drawable.myrect2))
                    mGPSDialog.show()
                }

            }
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
    private fun getMyUser()
    {
        var current = FirebaseAuth.getInstance().uid.toString()
        val userDb = Firebase.database.reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())

        val date_user: String
        val time_user: String
        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd/MM/yyyy")
        date_user = currentDate.format(calendar.time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
        time_user = currentTime.format(calendar.time)
        val connectedRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                if (connected) {
                    val status_up = HashMap<String?, Any?>()
                    status_up["status"] = 1
                    userDb.updateChildren(status_up)
                } else {
                    Log.d("TAG112", "not connected")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG112", "Listener was cancelled")
            }
        })

       // userDb.child("lastOnline").onDisconnect().setValue(ServerValue.TIMESTAMP)
                userDb.onDisconnect().let {
            val status_up2 = HashMap<String?, Any?>()
            status_up2["date"] = ServerValue.TIMESTAMP
            status_up2["status"] = 0
            //status_up2["time"] = time_user
            it.updateChildren(status_up2)
        }
        val MyUser = getSharedPreferences("MyUser", Context.MODE_PRIVATE).edit()
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                /*   val gender  = if (dataSnapshot.child("sex").value == "Male") {
                    MyUser.putInt("gender",R.drawable.ic_man)
                } else MyUser.putInt("gender",R.drawable.ic_woman)*/
                /*   if(dataSnapshot.hasChild("lastOnline")){
                    val date = Date(dataSnapshot.child("lastOnline").value as Long)
                    val sfd = SimpleDateFormat("dd-MM-yyyy HH:mm",
                            Locale.getDefault())
                    val text = sfd.format(date)
                    var ty = date.time
                    Log.d("time", text)
                    Log.d("time", ty.toString())
                }*/
                val df: LocalDate
                val sfd = SimpleDateFormat("dd-MM-yyyy",
                        Locale.getDefault())
                Date().time
                Log.d("time111", sfd.format(1598428015326))
                /*   if (dataSnapshot.hasChild("birth")) {
                    val date = Date(dataSnapshot.child("birth").value as Long)
                    val text = sfd.format(date)
                    Log.d("time111", sfd.format(dataSnapshot.child("birth").value as Long))

                    sfd.format(Calendar.getInstance().time)
                    Log.d("time111", sfd.format(Calendar.getInstance().time).toString())
                    val t = Instant.ofEpochMilli( dataSnapshot.child("birth").value as Long).atZone(ZoneId.systemDefault()).toLocalDate()


                    Log.d("time111", Period.between( t, LocalDate.now() ).years.toString())


                    //val period = Period(date, end, PeriodType.yearMothDay())
                }*/

                if (dataSnapshot.child("Vip").value.toString().toInt() == 1) {
                    Log.d("vvv", "1")
                    MyUser.putBoolean("Vip", true)
                } else MyUser.putBoolean("Vip", false)
                if (dataSnapshot.child("connection").hasChild("yep")) {
                    MyUser.putInt("c", dataSnapshot.child("connection").child("yep").childrenCount.toInt())
                }
                if (dataSnapshot.hasChild("see_profile")) {
                    MyUser.putInt("s", dataSnapshot.child("see_profile").childrenCount.toInt())
                }
                MyUser.putString("name", dataSnapshot.child("name").value.toString())
                MyUser.putInt("Age", dataSnapshot.child("Age").value.toString().toInt())
                MyUser.putInt("MaxLike", dataSnapshot.child("MaxLike").value.toString().toInt())
                MyUser.putInt("MaxChat", dataSnapshot.child("MaxChat").value.toString().toInt())
                MyUser.putInt("MaxAdmob", dataSnapshot.child("MaxAdmob").value.toString().toInt())
                MyUser.putInt("MaxStar", dataSnapshot.child("MaxStar").value.toString().toInt())
                MyUser.putInt("OppositeUserAgeMin", dataSnapshot.child("OppositeUserAgeMin").value.toString().toInt())
                MyUser.putInt("OppositeUserAgeMax", dataSnapshot.child("OppositeUserAgeMax").value.toString().toInt())
                MyUser.putString("OppositeUserSex", dataSnapshot.child("OppositeUserSex").value.toString())
                MyUser.putString("Distance", dataSnapshot.child("Distance").value.toString())

                if (dataSnapshot.hasChild("Location")) {
                    MyUser.putString("X", dataSnapshot.child("Location").child("X").value.toString())
                    MyUser.putString("Y", dataSnapshot.child("Location").child("Y").value.toString())

                }
                if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")) {
                    MyUser.putString("image", dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString())

                } else {
                    MyUser.putString("image", "")
                }
                MyUser.apply()
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.fragment_container2, page1).hide(page1)
                    add(R.id.fragment_container2, page2).hide(page2)
                    add(R.id.fragment_container2, page3).hide(page3)
                    add(R.id.fragment_container2, page4).hide(page4)
                }.commit()
                bar!!.setItemSelected(id, true)


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("tagh", "3")
            }

        })
    }
    private var resultFetchQA:ArrayList<QAObject> = ArrayList()
    private var text:String = ""
    private fun getDataOncall(): Task<HttpsCallableResult> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
                "questions" to text
        )

        return functions
                .getHttpsCallable("addQuestions")
                .call(data)
                .addOnSuccessListener { task ->
                    val data = task.data as Map<*, *>
                    val questions = data.get("questions") as Map<*, *>
                    Log.d("testDatatatat", questions.toString())
                    val keys = questions.keys

                    val Set = questions.get("Set1") as Map<*, *>


                        for(entry2 in Set.keys){
                            val value: String = entry2.toString()
                            val key = Set.get(value) as Map<*,*>
                            val keyString = key.keys.toString().replace("[", "").replace("]", "")
                            Log.d("testDatatatat", keyString)
                            val on = QAObject(keyString,key.get(keyString) as ArrayList<String>)
                            resultFetchQA.add(on)

                        }

                    OpenDialog(resultFetchQA)
                }
                .addOnFailureListener{
                    Log.d("testDatatatat", "error")
                }
    }
   private fun OpenDialog(ListChoice: ArrayList<QAObject>){
        val exampleClass: ExampleClass = ExampleClass()
        exampleClass.setData(ListChoice)
        exampleClass.show(supportFragmentManager, "example Dialog")
    }

    fun setCurrentIndex(newValueFormCurrentIndex: Int) {
        if (newValueFormCurrentIndex > 0) {
            bar!!.showBadge(R.id.item4, newValueFormCurrentIndex)
        } else {
            bar!!.dismissBadge(R.id.item4)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 8) {
            Toast.makeText(this@Switch_pageActivity, "fail GPS", Toast.LENGTH_SHORT).show()
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this@Switch_pageActivity, "fail GPS", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
                return true
        }
        return false
    }

    var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1000)
    }

    fun setLocal(lang: String?) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val configuration = Configuration()
        resources.configuration.setLocale(locale)
        baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", lang)
        editor.apply()
        Log.d("My", lang)
    }

    fun loadLocal() {
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val langure = preferences.getString("My_Lang", "")
        language = langure
        Log.d("My2", langure)
        setLocal(langure)
    }

    companion object {
        var bar: ChipNavigationBar? = null
        fun hide() {
            bar!!.visibility = View.GONE
        }
    }
}