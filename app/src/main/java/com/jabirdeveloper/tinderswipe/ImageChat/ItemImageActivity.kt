package com.jabirdeveloper.tinderswipe.ImageChat

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.R
import java.util.*

class ItemImageActivity : AppCompatActivity() {
    var viewPager: ViewPager? = null
    private lateinit var mRecyclerview: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: RecyclerView.Adapter<*>
    private var countImg = 0
    private var count = 0
    private var count_real: Int? = 0
    private lateinit var mLinearView: LinearLayout
    private lateinit var mLinearRe: LinearLayout
    private lateinit var screenAdapterImage: ScreenAdapterImage
    private lateinit var Nest: NestedScrollView
    private var chk_1time = false
    private lateinit var All_image_click: Button
    private lateinit var mCount_img: TextView
    private lateinit var set_date: TextView
    private lateinit var name_sender: TextView
    private var findImage: DatabaseReference? = null
    private var mDatabaseName: DatabaseReference? = null
    private var currentUid: String? = null
    private var matchId: String? = null
    private var url: String? = null
    private var nameUser: String? = ""
    private var nameMatch: String? = null
    private var MatchIdReal: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_image)
        matchId = intent.extras?.getString("matchId")
        MatchIdReal = intent.extras?.getString("matchIdReal")
        val count_st = intent.extras!!.getString("ChkImage")
        val count_st2 = intent.extras!!.getString("ChkImage2")
        count = Integer.valueOf(count_st.toString())
        All_image_click = findViewById(R.id.button_all_image)
        count_real = Integer.valueOf(count_st2.toString())
        name_sender = findViewById(R.id.name_sender_image)
        currentUid = FirebaseAuth.getInstance().uid
        viewPager = findViewById(R.id.page_image_all)
        screenAdapterImage = ScreenAdapterImage(this@ItemImageActivity, getDataSetImage())
        mDatabaseName = FirebaseDatabase.getInstance().reference.child("Users")
        findImage = FirebaseDatabase.getInstance().reference.child("Chat").child(matchId.toString())
        mCount_img = findViewById(R.id.count_image)
        set_date = findViewById(R.id.image_date_front)
        Nest = findViewById(R.id.nest_scroll)
        mLinearView = findViewById(R.id.linear_viewpager)
        mLinearRe = findViewById(R.id.linear_recycler_image)
        mCount_img.text = ("$count_real/$count")
        mRecyclerview = findViewById(R.id.recyclerView_image)
        mRecyclerview.isNestedScrollingEnabled = false
        mRecyclerview.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        mRecyclerview.layoutManager = layoutManager
        adapter = ImageAllAdapter(getDataSetImage(), this)
        All_image_click.setOnClickListener(View.OnClickListener {
            All_image_click.visibility = View.GONE
            mLinearView.visibility = View.GONE
            mCount_img.visibility = View.GONE
            name_sender.visibility = View.GONE
            set_date.visibility = View.GONE
            mLinearRe.visibility = View.VISIBLE
            mRecyclerview.adapter = adapter
            Nest.post(Runnable { Nest.fullScroll(View.FOCUS_DOWN) })
        })
        getName()
        chk_1time = true
        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position == 0 && chk_1time) {
                    chk_1time = if (resultImage!![position]!!.create) {
                        name_sender.text = nameUser
                        set_date.text = (resultImage[position]!!.date + " " + resultImage[position]!!.time)
                        false
                    } else {
                        name_sender.text = nameMatch
                        set_date.text = (resultImage[position]!!.date + " " + resultImage[position]!!.time)
                        false
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                val dd = count.toString()
                mCount_img.text = ((position + 1).toString() + "/" + dd)
                set_date.hint = resultImage!![position]!!.date + " " + resultImage[position]!!.time
                if (resultImage.elementAt(position)!!.create) {
                    name_sender.text = nameUser
                } else {
                    name_sender.text = nameMatch
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun getName() {
        mDatabaseName!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(currentUid.toString())) {
                    nameUser = dataSnapshot.child(currentUid.toString()).child("name").value.toString()
                }
                if (dataSnapshot.hasChild(MatchIdReal.toString())) {
                    nameMatch = dataSnapshot.child(MatchIdReal.toString()).child("name").value.toString()
                }
                findImage()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun findImage() {
        findImage!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.child("image").value != null) {
                    url = dataSnapshot.child("image").value.toString()
                    val date = dataSnapshot.child("date").value.toString()
                    val time = dataSnapshot.child("time").value.toString()
                    val create = dataSnapshot.child("createByUser").value.toString()
                    var check_user = false
                    if (create == currentUid) {
                        check_user = true
                    }
                    val dd = ScreenObject(url, date, time, check_user, matchId, MatchIdReal)
                    resultImage!!.add(dd)
                    ++countImg
                }
                if (count == countImg) {
                    adapter.notifyDataSetChanged()
                    screenAdapterImage.notifyDataSetChanged()
                    viewPager?.adapter = screenAdapterImage
                    mRecyclerview.scrollToPosition(count / 3 - 1)
                    viewPager?.currentItem = count_real!! - 1
                    viewPager?.visibility = View.VISIBLE
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private val resultImage: ArrayList<ScreenObject?>? = ArrayList()
    private fun getDataSetImage(): MutableList<ScreenObject?>? {
        return resultImage
    }
}