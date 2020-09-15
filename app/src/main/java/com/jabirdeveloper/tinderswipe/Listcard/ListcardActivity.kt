package com.jabirdeveloper.tinderswipe.Listcard

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.jabirdeveloper.tinderswipe.Functions.CalculateDistance
import com.jabirdeveloper.tinderswipe.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ListcardActivity : Fragment() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMatchesAdapter: RecyclerView.Adapter<*>
    private lateinit var mMatchesLayoutManager: RecyclerView.LayoutManager
    var x_user = 0.0
    var y_user = 0.0
    var x_opposite = 0.0
    var y_opposite = 0.0
    private var isScroll = false
    private var percentageMath:Map<*,*>? = null
    private lateinit var currentUserId: String
    private var oppositUserSex: String? = null
    private var age = 0
    private var startNode = 20
    private var OppositeUserAgeMin = 0
    private var OppositeUserAgeMax = 0
    private var distanceUser = 0.0
    private var count = 0
    private var count2: Int = 0
    private var currentItem = 0
    private var totalItem = 0
    private var scrollOutItem = 0
    private var sum_string: String? = null
    private lateinit var pro: ProgressBar
    private lateinit var search: ConstraintLayout
    private lateinit var anime1: ImageView
    private lateinit var anime2: ImageView
    private lateinit var handler: Handler
    private lateinit var supportFragmentManager: Fragment
    private var functions = Firebase.functions
    private lateinit var resultLimit: ArrayList<*>
    private var countLimit = 100
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_listcard, container, false)
        super.onCreate(savedInstanceState)

        pro = view.findViewById(R.id.view_pro)
        search = view.findViewById(R.id.layout_in)
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        mMatchesLayoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = mMatchesLayoutManager
        mMatchesAdapter = ListcardAdapter(getDataSetMatches(), requireContext())
        mRecyclerView.adapter = mMatchesAdapter
        anime1 = view.findViewById(R.id.anime1)
        anime2 = view.findViewById(R.id.anime2)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) { // launch a new coroutine in background and continue


            percentage()

        }

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScroll = true

                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItem = mMatchesLayoutManager.childCount
                totalItem = mMatchesLayoutManager.itemCount
                scrollOutItem = (mMatchesLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                Log.d("scrcc", "$currentItem $totalItem $scrollOutItem")
                Log.d("scrcc", resultLimit.size.toString() + "$countLimit")

                if (isScroll && currentItem + scrollOutItem == totalItem) {
                    isScroll = false

                    if (startNode < countLimit) {
                        getUser(resultLimit, startNode, false, resultMatches.size - 1)
                        startNode += 20
                    }
                    if ((currentItem + scrollOutItem) % countLimit == 0) {
                        callFunction(countLimit, false, resultMatches.size)
                        startNode = 20
                    }
                    /*   Log.d("valueofStartNOde",startNode.toString()+" , "+br_check+" , "+resultMatches2.size )
                       startNode += 20
                       if(startNode > resultMatches2.size){
                           startNode = resultMatches2.size-1
                           count2 = 1;
                           Log.d("valueofStartNOde",startNode.toString())
                       }
                       Log.d("valueofStartNOde_final",startNode.toString())
                       if(count2 != 1) {
                           for (i in startNode - 20 until startNode) {
                               val obj = ListcardObject(resultMatches2.elementAt(i)!!.userId, resultMatches2.elementAt(i)!!.name, resultMatches2.elementAt(i)!!.profileImageUrl,
                                       resultMatches2.elementAt(i)!!.distance, resultMatches2.elementAt(i)!!.status_opposite, resultMatches2.elementAt(i)!!.time,
                                       resultMatches2.elementAt(i)!!.Age, resultMatches2.elementAt(i)!!.gender, resultMatches2.elementAt(i)!!.myself, resultMatches2.elementAt(i)!!.off_status)
                               resultMatches.add(obj)
                           }
                           mMatchesAdapter.notifyDataSetChanged()
                       }*/

                    //FecthMatchformation()

                }

            }
        })
        mRecyclerView.addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                pro.visibility = View.GONE
            }

            override fun onChildViewDetachedFromWindow(view: View) {}
        })

        return view


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

    private fun getStartAt() {
        val userdb = FirebaseDatabase.getInstance().reference.child("Users")
        userdb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                br_check = snapshot.childrenCount.toInt()
                Log.d("dddddd", br_check.toString())
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                    getUsergender()
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getUsergender() {
        val preferences = requireActivity().getSharedPreferences("MyUser", Context.MODE_PRIVATE)
        oppositUserSex = preferences.getString("OppositeUserSex", "All").toString()
        OppositeUserAgeMin = preferences.getInt("OppositeUserAgeMin", 0)
        OppositeUserAgeMax = preferences.getInt("OppositeUserAgeMax", 0)
        x_user = preferences.getString("X", "").toString().toDouble()
        y_user = preferences.getString("Y", "").toString().toDouble()
        distanceUser = when (preferences.getString("Distance", "Untitled")) {
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
        callFunction(100, true, 0)

    }
    private fun percentage(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val data = hashMapOf(
                    "question" to "Questions"
            )
            functions
                    .getHttpsCallable("getPercentageMatching")
                    .call(data)
                    .addOnSuccessListener { task ->
                        val data = task.data as Map<*, *>
                        Log.d("testDatatatat", data.toString())
                        percentageMath = data.get("dictionary") as Map<*, *>

                        getStartAt()
                    }
                    .addOnFailureListener {
                        Log.d("testDatatatat", "error")
                    }
        }
    }

    private fun callFunction(limit: Int, type: Boolean, count: Int) {
        var pre = count
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) { // launch a new coroutine in background and continue
            val data = hashMapOf(
                    "sex" to oppositUserSex,
                    "min" to OppositeUserAgeMin,
                    "max" to OppositeUserAgeMax,
                    "x_user" to x_user,
                    "y_user" to y_user,
                    "distance" to distanceUser,
                    "limit" to count + limit,
                    "prelimit" to count
            )
            //FecthMatchformation()
            functions.getHttpsCallable("getUserList")
                    .call(data)
                    .addOnFailureListener { Log.d("ghu", "failed") }
                    .addOnSuccessListener { task ->
                        // This continuation runs on either success or failure, but if the task
                        // has failed then result will throw an Exception which will be
                        // propagated down.
                        val result1 = task.data as Map<*, *>

                        Log.d("ghu", result1.toString())
                        resultLimit = result1["o"] as ArrayList<*>
                        if (resultLimit.isNotEmpty())
                            if (type)
                                getUser(resultLimit, 0, type, 0)
                            else
                                getUser(resultLimit, 0, type, resultMatches.size - 1)


                    }
        }
    }

    private fun getUser(result2: ArrayList<*>, start: Int, type: Boolean, startNoti: Int) {
        var max = start + 20
        var myself = ""
        var off_status = false
        var typetime = ""
        var time = ""
        Log.d("max", (start + 20).toString() + " " + result2.size)
        if (result2.size < start + 20) {
            max = result2.size
        }
        for (x in start until max) {
            val user = result2[x] as Map<*, *>
            Log.d("ghu", user["name"].toString() + " , " + user["distance_other"].toString())

//
//            var time_opposite = "null"
//            var date_opposite = "null"
//            if (user["time"] != null) {
//                time_opposite = user["time"].toString()
//            }
//            if (user["date"] != null) {
//                date_opposite = user["date"].toString()
//            }
//            time_change(time_opposite, date_opposite)
            if (user["typeTime"] != null) {
                typetime = user["typeTime"].toString()
                Log.d("type55", "0")
            }
            if (user["time"] != null) {
                time = user["time"].toString()
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
            if (user["status"] == 1) {
                status = "online"
            }
            val df2 = DecimalFormat("#.#")
            val dis = df2.format(user["distance_other"])
            var percentAdd:String? = "0"
            if(percentageMath!!.get(user["key"].toString()) != null){
                percentAdd = percentageMath!!.get(user["key"].toString()).toString()
                //percentAdd = percentAdd.toString()
                //Log.d("testDatatatat", percentAdd)
            }
            //Log.d("testDatatatat", percentageMath!!.get(user["key"].toString()).toString())
            val obj = ListcardObject(user["key"].toString(), user["name"].toString(), profileImageUrl, dis, status, user["Age"].toString(), user["sex"].toString(), myself, off_status, typetime, time,percentAdd)

            resultMatches.add(obj)
            if (resultMatches.size > 0) {
                pro.visibility = View.GONE
                // search.visibility = View.GONE
                // handler.removeCallbacks(runnable)
            }
        }
        Log.d("sss", "$startNoti " + resultMatches.size)
        if (type)
            mMatchesAdapter.notifyDataSetChanged()
        else mMatchesAdapter.notifyItemRangeChanged(startNoti, resultMatches.size)
    }

    private val UidMatch: MutableList<String?>? = java.util.ArrayList()
    private var chk_num1 = 0
    private var chk_num2 = 0
    private fun FecthMatchformation() {
        val userDb = FirebaseDatabase.getInstance().reference.child("Users")
        userDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                Log.d("checksnap", dataSnapshot.key)
                ++br_check2
                age = dataSnapshot.child("Age").value.toString().toInt()
                if (age in OppositeUserAgeMin..OppositeUserAgeMax && !dataSnapshot.child("connection").child("matches").hasChild(currentUserId)
                        && dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")
                        && currentUserId != dataSnapshot.key
                        && !dataSnapshot.hasChild("off_list")) {
                    x_opposite = dataSnapshot.child("Location").child("X").value.toString().toDouble()
                    y_opposite = dataSnapshot.child("Location").child("Y").value.toString().toDouble()
                    val distance = CalculateDistance.calculate(x_user, y_user, x_opposite, y_opposite)
                    if (distance < distanceUser) {
                        if (oppositUserSex == "All") {
                            if (!UidMatch!!.contains(dataSnapshot.key))
                                fet(dataSnapshot, distance)
                        } else if (dataSnapshot.child("sex").value.toString() == oppositUserSex) {
                            if (!UidMatch!!.contains(dataSnapshot.key))
                                fet(dataSnapshot, distance)
                        }
                    }
                }
                Log.d("ddf", resultMatches.size.toString())
                if (resultMatches.size > 0) {
                    pro.visibility = View.GONE
                    // search.visibility = View.GONE
                    // handler.removeCallbacks(runnable)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var br_check = 0
    private var br_check2 = 0
    private fun fet(dataSnapshot: DataSnapshot, distance_1: Double) {
        UidMatch!!.add(dataSnapshot.key)
        var time_opposite = "null"
        var date_opposite = "null"

        if (dataSnapshot.child("Status").hasChild("time")) {
            time_opposite = dataSnapshot.child("Status").child("time").value.toString()
        }
        if (dataSnapshot.child("Status").hasChild("date")) {
            date_opposite = dataSnapshot.child("Status").child("date").value.toString()
        }
        //time_change(time_opposite, date_opposite)
        var name = ""
        var profileImageUrl = ""
        val x = ""
        val y = ""
        var myself = ""
        var off_status = false
        val Age: String = dataSnapshot.child("Age").value.toString()
        val gender: String = dataSnapshot.child("sex").value.toString()
        if (dataSnapshot.hasChild("myself")) {
            myself = dataSnapshot.child("myself").value.toString()
        }
        if (dataSnapshot.child("name").value != null) {
            name = dataSnapshot.child("name").value.toString()
        }
        profileImageUrl = "default"
        if (dataSnapshot.hasChild("ProfileImage")) {
            profileImageUrl = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()
        }
        var status = "offline"
        if (dataSnapshot.child("Status").hasChild("status")) {
            status = dataSnapshot.child("Status").child("status").value.toString()
        }
        if (dataSnapshot.hasChild("off_status")) {
            off_status = true
        }
        // val obj = ListcardObject(userId, name, profileImageUrl, distance_1, status, sum_string, Age, gender, myself, off_status)

        //resultMatches2.add(obj)

        /*
        resultMatches2.sortWith(Comparator { o1, o2 ->
            val chk = o1!!.time!!.substring(0, 1)
            val chk2 = o2!!.time!!.substring(0, 1)
            if (chk == "d") {
                var sum = 0
                sum = o1.time!!.substring(1).toInt()
                chk_num1 = sum * 60 * 24
            } else {
                chk_num1 = o1.time!!.toInt()
            }
            if (chk2 == "d") {
                var sum2 = 0
                sum2 = o2.time!!.substring(1).toInt()
                chk_num2 = sum2 * 60 * 24
            } else {
                chk_num2 = o2.time!!.toInt()
            }
            Log.d("125", o1.getName())
            chk_num1.compareTo(chk_num2)
        })
        resultMatches2.sortWith(Comparator { o1, o2 -> ((o1!!.getDistance() * 10).toInt()).compareTo((o2!!.getDistance() * 10).toInt()) })
        resultMatches2.sortWith(Comparator { o1, o2 ->
            var b1 = false
            var b2 = false
            var chk_b1 = 0
            var chk_b2 = 0
            if (o1!!.getStatus_opposite() == "online") {
                b1 = true
            }
            if (o2!!.getStatus_opposite() == "online") {
                b2 = true
            }
            if (b1) {
                chk_b1 = 1
            }
            if (b2) {
                chk_b2 = 1
            }
            chk_b2 - chk_b1
        })*/
        Log.d("checkBrbrbrbr", (br_check - 1).toString() + " : " + startNode)
        if (br_check2 == br_check) {
            /*resultMatches2.sortWith(Comparator { o1, o2 ->
                val chk = o1!!.time!!.substring(0, 1)
                val chk2 = o2!!.time!!.substring(0, 1)
                if (chk == "d") {
                    var sum = 0
                    sum = o1.time!!.substring(1).toInt()
                    chk_num1 = sum * 60 * 24
                } else {
                    chk_num1 = o1.time!!.toInt()
                }
                if (chk2 == "d") {
                    var sum2 = 0
                    sum2 = o2.time!!.substring(1).toInt()
                    chk_num2 = sum2 * 60 * 24
                } else {
                    chk_num2 = o2.time!!.toInt()
                }
                Log.d("125", o1.name)
                chk_num1.compareTo(chk_num2)
            })
            resultMatches2.sortWith(Comparator { o1, o2 -> ((o1!!.distance * 10).toInt()).compareTo((o2!!.distance * 10).toInt()) })
            resultMatches2.sortWith(Comparator { o1, o2 ->
                var b1 = false
                var b2 = false
                var chk_b1 = 0
                var chk_b2 = 0
                if (o1!!.status_opposite == "online") {
                    b1 = true
                }
                if (o2!!.status_opposite == "online") {
                    b2 = true
                }
                if (b1) {
                    chk_b1 = 1
                }
                if (b2) {
                    chk_b2 = 1
                }
                chk_b2 - chk_b1
            })*/
//
//            if(resultMatches2.size-1 < startNode){
//                startNode = resultMatches2.size-1
//            }
//            for (i in 0 until startNode) {
//
//                val obj = ListcardObject(resultMatches2.elementAt(i)!!.userId, resultMatches2.elementAt(i)!!.name, resultMatches2.elementAt(i)!!.profileImageUrl,
//                        resultMatches2.elementAt(i)!!.distance, resultMatches2.elementAt(i)!!.status_opposite, resultMatches2.elementAt(i)!!.time,
//                        resultMatches2.elementAt(i)!!.Age, resultMatches2.elementAt(i)!!.gender, resultMatches2.elementAt(i)!!.myself, resultMatches2.elementAt(i)!!.off_status)
//                resultMatches.add(obj)
//
//
//            }
//
//
//            mMatchesAdapter.notifyDataSetChanged()
        }
        // mMatchesAdapter.notifyDataSetChanged()

    }

    private val resultMatches: ArrayList<ListcardObject?> = ArrayList()
    private val resultMatches2: ArrayList<ListcardObject?> = ArrayList()
    private fun getDataSetMatches(): ArrayList<ListcardObject?> {
        return resultMatches
    }


    private fun time_change(time_opposite: String, date_opposite: String) {
        val dateUser: String
        val timeUser: String
        var diffDated = 0
        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd/MM/yyyy")
        dateUser = currentDate.format(calendar.time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
        timeUser = currentTime.format(calendar.time)
        if (date_opposite !== "null") {
            var oppositeDate: Date? = null
            try {
                oppositeDate = currentDate.parse(date_opposite)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            var userDate: Date? = null
            try {
                userDate = currentDate.parse(dateUser)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val diffDate = userDate!!.time - oppositeDate!!.time
            diffDated = TimeUnit.DAYS.convert(diffDate, TimeUnit.MILLISECONDS).toInt()
        }
        if (time_opposite !== "null") {
            val timeOppositeDate = date_opposite.substring(0, 2).toInt()
            val timeUserDate = dateUser.substring(0, 2).toInt()
            val timeOppositeHr = time_opposite.substring(0, 2).toInt()
            val timeOppositeMm = time_opposite.substring(3, 5).toInt()
            val timeUserMm = timeUser.substring(3, 5).toInt()
            val timeUserHr = timeUser.substring(0, 2).toInt()
            var sum = 0
            if (diffDated < 2) {
                if (timeUserHr >= timeOppositeHr && diffDated >= 1) {
                    sum_string = "d1"
                } else {
                    if (timeUserMm > timeOppositeMm) {
                        val time_mm = timeUserMm - timeOppositeMm
                        sum += time_mm
                        if (timeOppositeDate != timeUserDate) {
                            sum += (24 - timeOppositeHr + timeUserHr) * 60
                        } else if (timeOppositeHr != timeUserHr) {
                            sum += (timeUserHr - timeOppositeHr) * 60
                        }
                        sum_string = sum.toString()
                    } else if (timeUserMm < timeOppositeMm) {
                        sum = 60 - timeOppositeMm + timeUserMm
                        sum = if (timeOppositeDate != timeUserDate) {
                            (24 - timeOppositeHr - 1 + timeUserHr) * 60 + sum
                        } else {
                            (timeUserHr - timeOppositeHr - 1) * 60 + sum
                        }
                        sum_string = sum.toString()
                    } else if (timeUserMm == timeOppositeMm && timeUserHr != timeOppositeHr) {
                        sum = if (timeOppositeDate != timeUserDate) {
                            (24 - timeOppositeHr + timeUserHr) * 60
                        } else {
                            val time_mm = timeUserHr - timeOppositeHr
                            time_mm * 60
                        }
                        sum_string = sum.toString()
                    } else if (timeUserMm == timeOppositeMm && timeOppositeHr == timeUserHr) {
                        sum_string = "1"
                    }
                }
            } else if (diffDated >= 2) {
                sum_string = "d"
                val jj = diffDated.toString()
                sum_string += jj
            }
        } else {
            sum_string = "null"
        }
    }

    suspend fun fetchAndShowFeedData() {
        getStartAt()
    }


    override fun onStart() {
        super.onStart()

    }

}



