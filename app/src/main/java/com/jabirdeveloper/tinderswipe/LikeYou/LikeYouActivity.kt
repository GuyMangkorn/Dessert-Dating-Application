package com.jabirdeveloper.tinderswipe.LikeYou

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.Functions.CalculateDistance
import com.jabirdeveloper.tinderswipe.MainActivity
import com.jabirdeveloper.tinderswipe.R
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LikeYouActivity : AppCompatActivity() {
    private lateinit var LikeYouRecycleview: RecyclerView
    private lateinit var LikeYouAdapter: RecyclerView.Adapter<*>
    private lateinit var LikeYouLayoutManager: RecyclerView.LayoutManager
    private lateinit var currentUserId: String
    private lateinit var userDb: DatabaseReference
    private var x_user = 0.0
    private var y_user = 0.0
    private var x_opposite = 0.0
    private var y_opposite = 0.0
    private lateinit var blurView: BlurView
    private lateinit var button: Button
    private lateinit var empty: TextView
    private lateinit var dialog: Dialog
    private lateinit var ff: Geocoder
    private var activity = this
    var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like_you)
        button = findViewById(R.id.buttonsee)
        empty = findViewById(R.id.empty)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        button.setOnClickListener(View.OnClickListener { openDialog() })
        blurView = findViewById(R.id.blurView)
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val langure = preferences.getString("My_Lang", "")
        ff = if (langure == "th") {
            Geocoder(this@LikeYouActivity)
        } else {
            Geocoder(this@LikeYouActivity, Locale.UK)
        }
        val radius = 10f
        val decorView = window.decorView
        val windowBackground = decorView.background
        blurView.setupWith(findViewById<View?>(R.id.like_you_recycle) as ViewGroup)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true)
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        LikeYouRecycleview = findViewById(R.id.like_you_recycle)
        LikeYouRecycleview.isNestedScrollingEnabled = false
        LikeYouRecycleview.setHasFixedSize(true)
        LikeYouLayoutManager = LinearLayoutManager(this@LikeYouActivity)
        LikeYouRecycleview.layoutManager = LikeYouLayoutManager
        LikeYouAdapter = LikeYouAdapter(getDataSetMatches()!!, this@LikeYouActivity)
        LikeYouRecycleview.adapter = LikeYouAdapter
        userDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId).child("connection").child("yep")
        if (intent.hasExtra("See")) {
            intent.extras!!.remove("See")
            userDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId).child("see_profile")
            button.setText(R.string.see_people)
            empty.setText(R.string.see_empty)
            supportActionBar!!.setTitle(R.string.People_view)
        } else {
            button.setText(R.string.see_like)
            empty.setText(R.string.like_empty)
            supportActionBar!!.setTitle(R.string.People_like_you)
        }
        MainScope().launch(Dispatchers.IO) { // launch a new coroutine in background and continue
            val userdb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
            userdb.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child("Vip").value == 1) {
                        blurView.visibility = View.GONE
                        button.visibility = View.GONE
                    }
                    if (intent.hasExtra("See")) {
                        if (dataSnapshot.hasChild("buy_see")) {
                            blurView.visibility = View.GONE
                            button.visibility = View.GONE
                        }
                    } else {
                        if (dataSnapshot.hasChild("buy_like")) {
                            blurView.visibility = View.GONE
                            button.visibility = View.GONE
                        }
                    }
                    if (dataSnapshot.hasChild("Location")) {
                        val x: String = dataSnapshot.child("Location").child("X").value.toString()
                        val y: String = dataSnapshot.child("Location").child("Y").value.toString()
                        x_user = java.lang.Double.valueOf(x)
                        y_user = java.lang.Double.valueOf(y)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
            userDb.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    if (dataSnapshot.exists()) {
                        time(dataSnapshot.key!!)
                    } else {
                        run {
                            empty.visibility = View.VISIBLE
                            button.visibility = View.GONE
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })

        }

    }

    fun openDialog() {
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.money, null)
        dialog = Dialog(this@LikeYouActivity)
        val imageView = view.findViewById<ImageView>(R.id.image_vip)
        val textView = view.findViewById<TextView>(R.id.text)
        val textView2 = view.findViewById<TextView>(R.id.text_second)
        val b1 = view.findViewById<Button>(R.id.buy)
        if (intent.hasExtra("See")) {
            imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_vision))
            textView.text = "ใครเข้ามาดูโปรไฟล์คุณ"
            textView2.text = "ดูว่าใครบ้างที่เข้าชมโปรไฟล์ของคุณ"
            b1.text = "฿20.00 / เดือน"
            b1.setOnClickListener {
                dialog.dismiss()
                buy_see()
            }
        } else {
            imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_love2))
            textView.text = "ใครถูกใจคุณ"
            textView2.text = "ดูว่าใครบ้างที่เข้ามากดถูกใจให้คุณ"
            b1.setOnClickListener {
                dialog.dismiss()
                buy_like()
            }
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    private fun time(key: String) {
        val dd = userDb.child(key)
        dd.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var time = "วันนี้ 00:00"
                if (dataSnapshot.hasChild("date") && dataSnapshot.hasChild("time")) {
                    val calendar = Calendar.getInstance()
                    val currentDate = SimpleDateFormat("dd/MM/yyyy")
                    val date_now = currentDate.format(calendar.time)
                    val time_user = dataSnapshot.child("time").value.toString()
                    val date_user = dataSnapshot.child("date").value.toString()
                    time = if (date_now != date_user) {
                        Toast.makeText(this@LikeYouActivity, "$time_user , $date_user", Toast.LENGTH_SHORT).show()
                        date_user
                    } else {
                        getString(R.string.today) + " " + time_user
                    }
                }
                FecthHi(key, time)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun FecthHi(key: String, time: String?) {
        val userDb = FirebaseDatabase.getInstance().reference.child("Users").child(key)
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")) {
                    var profileImageUrl = ""
                    profileImageUrl = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()

                    var city = ""
                    val userId = dataSnapshot.key
                    val name = dataSnapshot.child("name").value.toString()
                    val status = dataSnapshot.child("Status").child("status").value.toString()
                    var myself = ""
                    val Age: String = dataSnapshot.child("Age").value.toString()
                    val gender: String = dataSnapshot.child("sex").value.toString()
                    if (dataSnapshot.hasChild("myself")) {
                        myself = dataSnapshot.child("myself").value.toString()
                    }
                    val x: Double = dataSnapshot.child("Location").child("X").value.toString().toDouble()
                    val y: Double = dataSnapshot.child("Location").child("Y").value.toString().toDouble()
                    val dss = MainActivity()
                    val distance = CalculateDistance.calculate(x_user, y_user, x, y)
                    val preferences = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE)
                    val langure = preferences.getString("My_Lang", "")
                    val ff: Geocoder
                    ff = if (langure == "th") {
                        Geocoder(this@LikeYouActivity)
                    } else {
                        Geocoder(this@LikeYouActivity, Locale.UK)
                    }
                    var addresses: MutableList<Address?>? = null
                    try {
                        addresses = ff.getFromLocation(x, y, 1)
                        city = addresses[0]!!.adminArea
                        Log.d("123", city)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    resultLike!!.add(LikeYouObject(
                            userId, profileImageUrl, name, status, Age, gender, myself, distance, city, time))
                }
                LikeYouAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val resultLike: ArrayList<LikeYouObject?>? = ArrayList()
    private fun getDataSetMatches(): MutableList<LikeYouObject?>? {
        return resultLike
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun buy_see() {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
        databaseReference.child("buy_see").setValue(true).addOnSuccessListener {
            activity.finish()
            activity.overridePendingTransition(0, 0)
            activity.startActivity(activity.intent)
            activity.overridePendingTransition(0, 0)
        }
    }

    private fun buy_like() {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
        databaseReference.child("buy_like").setValue(true).addOnSuccessListener {
            activity.finish()
            activity.overridePendingTransition(0, 0)
            activity.startActivity(activity.intent)
            activity.overridePendingTransition(0, 0)
        }
    }

}