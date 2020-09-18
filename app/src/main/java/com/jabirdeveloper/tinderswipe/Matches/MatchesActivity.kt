package com.jabirdeveloper.tinderswipe.Matches

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.R
import com.jabirdeveloper.tinderswipe.SwitchpageActivity
import com.wang.avi.AVLoadingIndicatorView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MatchesActivity : Fragment() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mHiRecyclerView: RecyclerView
    private lateinit var mMatchesAdapter: RecyclerView.Adapter<*>
    private lateinit var mHiAdapter: RecyclerView.Adapter<*>
    private lateinit var mMatchesLayoutManager: RecyclerView.LayoutManager
    private lateinit var mHiLayout: RecyclerView.LayoutManager
    private lateinit var layoutChatNa: LinearLayout
    private var currentUserId: String? = null
    private var dateUser: String? = ""
    private var count = 0
    private var p1: AVLoadingIndicatorView? = null
    private lateinit var textEmpty: TextView
    private lateinit var chatEmpty: TextView

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_matches, container, false)
        super.onCreate(savedInstanceState)
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        textEmpty = view.findViewById(R.id.textempty)
        chatEmpty = view.findViewById(R.id.chatempty)
        layoutChatNa = view.findViewById(R.id.layoutRe)
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        mMatchesLayoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = mMatchesLayoutManager
        mMatchesAdapter = MatchesAdapter(getDataSetMatches(), context, currentUserId)
        mRecyclerView.adapter = mMatchesAdapter
        p1 = view.findViewById(R.id.progress_bar_pre_pro)
        mHiRecyclerView = view?.findViewById(R.id.recyclerView2)!!
        mHiRecyclerView.isNestedScrollingEnabled = false
        mHiRecyclerView.setHasFixedSize(true)
        mHiLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mHiRecyclerView.layoutManager = mHiLayout
        mHiAdapter = HiAdapter(getDataSetHi(), requireContext())
        mHiRecyclerView.adapter = mHiAdapter
        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd/MM/yyyy")
        dateUser = currentDate.format(calendar.time)
        mRecyclerView.visibility = View.GONE
        chatNaCheck()
        checkFirst()
        return view
    }
    private var checkFirstRemove: String? = "null"
    private var userMatchCount = 0
    /*private fun checkNode(code:String) {
        val matchDbCheck = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString());
        matchDbCheck.orderByKey().equalTo(code).addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.key.equals("connection")) {
                        //Checkconnection()
                        CheckNodeMatch()
                        Log.d("test_check_matches", "connection_accept")
                    } else if(check_first_connection){
                        check_first_connection = false
                        Log.d("test_check_matches", "connection_reject ")
                        p1!!.hide()
                        chat_empty.visibility = View.VISIBLE
                        mRecyclerView.visibility = View.GONE
                    }
                }


            override fun onChildRemoved(snapshot: DataSnapshot) {
                if (snapshot.key.toString() == "connection") {
                    check_first_connection = true
                }
                Log.d("test_check_matches", snapshot.key)
            }


        })
    }

    private fun checkConnection(){
        val matchDbCheck = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString());
        matchDbCheck.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("connection")) {
                    /*CheckNodeMatch()*/
                    checkNode("connection")
                    Log.d("test_check_matches", "connection_accept")
                } else {
                    Log.d("test_check_matches", "connection_reject")
                    p1!!.hide()
                    chat_empty.visibility = View.VISIBLE
                    mRecyclerView.visibility = View.GONE
                }
            }
        })
    }
    private fun CheckNodeMatch() {
        val matchDbCheck = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection");
        matchDbCheck.orderByPriority().equalTo("matches").addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.key.toString() == ("matches")) {
                        check_first_matches = false
                        getUserMarchId()
                        Log.d("test_check_matches", "matches_accept" + snapshot.key)
                        chat_empty.visibility = View.GONE
                        //CheckNodeMatch2()
                    } else if(check_first_matches){
                        check_first_matches = false
                        Log.d("test_check_matches", "matches_reject" + snapshot.key)
                        p1!!.hide()
                        chat_empty.visibility = View.VISIBLE
                        mRecyclerView.visibility = View.GONE
                    }
                }



            override fun onChildRemoved(snapshot: DataSnapshot) {
                if (snapshot.key.toString() == "matches") {
                    check_first_matches = true
                }
                Log.d("test_check_matches", snapshot.key)
            }
        })
    }*/

    /*private fun CheckNodeMatch2(){
        val matchDbCheck = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection");
        matchDbCheck.orderByKey().equalTo("matches").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    getUserMarchId()
                    Log.d("test_check_matches","matches_accept")
                }else{
                    Log.d("test_check_matches","matches_reject")
                    p1!!.hide()
                    chat_empty.visibility = View.VISIBLE
                    mRecyclerView.visibility = View.GONE
                }
            }
        })
    }*/

    /*
    }*/
    /*private fun getUserMarchId_Check() {
        val matchDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection")
        matchDb.orderByKey().equalTo("matches").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d("test_check_matches","accept")
                        getUserMarchId()
                    } else {
                        Log.d("test_check_matches","reject")
                        p1!!.hide()
                        chat_empty.visibility = View.VISIBLE
                        mRecyclerView.visibility = View.GONE
                    }
            }
        });
    }*/

    private fun checkFirst(){
        val matchDbCheck = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches")
        matchDbCheck.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    chatEmpty.visibility = View.GONE
                    getUserMarchId()
                    Log.d("test_check_matches", "matches_accept")
                } else {
                    Log.d("test_check_matches", "matches_reject")
                    getUserMarchId()
                    p1!!.hide()
                    chatEmpty.visibility = View.VISIBLE
                    mRecyclerView.visibility = View.GONE
                }
            }
        })
    }
    private fun getUserMarchId() {
        val matchDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches")
        matchDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                ++userMatchCount
                if(userMatchCount == 1) chatEmpty.visibility = View.GONE
                Log.d("test_check_matches", "onChildAdd : ${dataSnapshot.key}")
                val chatID = dataSnapshot.child("ChatId").value.toString()
                //Check_data(dataSnapshot.key, chatID)
                testChatNode(chatID, dataSnapshot.key.toString())
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                if (checkFirstRemove == "null") {
                    --userMatchCount
                    if (userMatchCount == 0) {
                        chatEmpty.visibility = View.VISIBLE
                        mRecyclerView.visibility = View.GONE
                    }
                    checkFirstRemove = dataSnapshot.key
                    unMatch(dataSnapshot.key)
                } else if (checkFirstRemove != dataSnapshot.key) {
                    --userMatchCount
                    if (userMatchCount == 0) {
                        chatEmpty.visibility = View.VISIBLE
                        mRecyclerView.visibility = View.GONE
                    }
                    checkFirstRemove = dataSnapshot.key
                    unMatch(dataSnapshot.key)
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun unMatch(key: String?) {
        val index = resultMatches!!.map { T -> T!!.userId.equals(key) }.indexOf(element = true)
        Log.d("countIndexSomethings", "$key ,index: $index")
        val matchIDStored = mContext!!.getSharedPreferences(currentUserId + "Match_first", Context.MODE_PRIVATE)
        val editor2 = matchIDStored.edit()
        editor2.remove(key).apply()
        val myUnread2 = mContext!!.getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
        val dd = myUnread2.getInt("total", 0)
        var unread = resultMatches[index]!!.count_unread
        if (unread == -1) {
            unread = 0
        }
        val total = dd - unread
        (mContext as SwitchpageActivity?)!!.setCurrentIndex(total)
        val myUnread1 = mContext!!.getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
        val editorRead = myUnread1.edit()
        editorRead.putInt("total", total)
        editorRead.apply()
        resultMatches.removeAt(index)
        mMatchesAdapter.notifyItemRemoved(index)
        mMatchesAdapter.notifyItemRangeChanged(index, resultMatches.size)
    }

    private var userChatNaCount = 0
    private var checkFirstRemoveChatNa: String? = "null"
    private var firstSystem2 = true
    private fun chatNaCheck() {
        val dBChatNa = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("chatna")
        dBChatNa.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (firstSystem2) {
                    firstSystem2 = false
                    if (dataSnapshot.exists()) {
                        chatNa()
                    } else {
                        textEmpty.visibility = View.VISIBLE
                        mHiRecyclerView.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun chatNa() {
        val dBChatNa = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("chatna")
        dBChatNa.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                ++userChatNaCount
                val chatId = dataSnapshot.value.toString()
                lastChatHi(dataSnapshot.key, chatId)
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                if (checkFirstRemoveChatNa == "null") {
                    --userChatNaCount
                    if (userChatNaCount == 0) {
                        textEmpty.visibility = View.VISIBLE
                        mHiRecyclerView.visibility = View.GONE
                    }
                    checkFirstRemoveChatNa = dataSnapshot.key
                    deleteChatNA(dataSnapshot.key)
                } else if (checkFirstRemoveChatNa != dataSnapshot.key) {
                    --userChatNaCount
                    if (userChatNaCount == 0) {
                        textEmpty.visibility = View.VISIBLE
                        mHiRecyclerView.visibility = View.GONE
                    }
                    checkFirstRemoveChatNa = dataSnapshot.key
                    deleteChatNA(dataSnapshot.key)
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun deleteChatNA(uIdChatNa: String?) {
        for (i in resultHi!!.indices) {
            if (resultHi[i]!!.userId == uIdChatNa) {
                resultHi.removeAt(i)
                mHiAdapter.notifyItemRemoved(i)
                mHiAdapter.notifyItemRangeChanged(i, resultHi.size)
            }
        }
    }

    /*private fun checkData(key: String?, chatID: String?) {
        val check = FirebaseDatabase.getInstance().reference
        check.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("Users").hasChild(key.toString())) {
                    ++count
                    if (dataSnapshot.hasChild("Chat")) {
                        if (dataSnapshot.child("Chat").hasChild(chatID.toString())) {
                            latestChat(key, chatID)
                        } else {
                            val lastChat = ""
                            val time = "-1"
                            val count = -1
                            fetchMatchFormation(key, lastChat, time, count)
                            get_node(key, chatID)
                        }
                    } else {
                        Log.d("test_check_matches", "dataChange $key")
                        checkNodeFirst(key, chatID)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/

    /*private fun Node_first(key: String?, ChatID: String?) {
        val chatdb = FirebaseDatabase.getInstance().reference
        chatdb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                checkNodeFirst(key, ChatID)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/
    private fun testChatNode(chatId:String,uid:String){
        val check = FirebaseDatabase.getInstance().reference.child("Chat").child(chatId)
        check.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    latestChat(uid, chatId)
                }else{
                    latestChat(uid, chatId)
                    val lastChat = ""
                    val time = "-1"
                    val count = -1
                    Log.d("test_check_matches", "dataChange $uid")
                    fetchMatchFormation(uid, lastChat, time, count)
                }
            }
            override fun onCancelled(error: DatabaseError) {}

        })
    }
    /*private fun checkNodeFirst(key: String?, ChatID: String?) {
        val check = FirebaseDatabase.getInstance().reference
        check.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild("Chat")) {
                    latestChat(key, ChatID)
                } else {
                    val lastChat = ""
                    val time = "-1"
                    val count = -1
                    //Log.d("test_check_matches",key)
                    fetchMatchFormation(key, lastChat, time, count)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/

    /*private fun get_node(key: String?, chatID: String?) {
        val chatdb = FirebaseDatabase.getInstance().reference.child("Chat")
        val cc = chatdb.orderByKey().equalTo(chatID)
        cc.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                checkGetNode(key, chatID, dataSnapshot.key)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/

    /*private fun checkGetNode(key: String?, chatID: String?, key_chatId: String?) {
        val check = FirebaseDatabase.getInstance().reference.child("Chat")
        check.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (chatID == key_chatId) {
                    latestChat(key, chatID)
                } else {
                    val last_chat = ""
                    val time = "-1"
                    val count = -1
                    fetchMatchFormation(key, last_chat, time, count)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/

    private fun startNode(key: String?, keyNode: String?, lastChat: String?, time: String?, count: Int) {
        val startDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches").child(key.toString()).child("Start")
        startDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    if (dataSnapshot.value.toString() == keyNode) {
                        val lastChat2 = ""
                        val time2 = "-1"
                        fetchMatchFormation(key, lastChat2, time2, count)
                    } else {
                        fetchMatchFormation(key, lastChat, time, count)
                    }
                } else {
                    fetchMatchFormation(key, lastChat, time, count)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    private var createByBoolean:Boolean = true
    private fun latestChat(key: String?, chatID: String?) {
        val chatDb = FirebaseDatabase.getInstance().reference.child("Chat").child(chatID.toString()).orderByKey().limitToLast(1)
        chatDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val lastChat = dataSnapshot.child("text").value.toString()
                var time = dataSnapshot.child("time").value.toString()
                val date = dataSnapshot.child("date").value.toString()
                val createBy = dataSnapshot.child("createByUser").value.toString()
                if (date != dateUser) {
                    time = date.substring(0, 5)
                }
                createByBoolean = dataSnapshot.child("createByUser").value.toString() != (currentUserId)
                if (createBy != currentUserId) {
                    chatCheckRead(chatID, key, time, lastChat)
                } else {
                    startNode(key, dataSnapshot.key, lastChat, time, 0)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var countRead = 0
    private var mDatabaseChat: DatabaseReference? = null
    private fun chatCheckRead(ChatId: String?, key: String?, time: String?, last_chat: String?) {
        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat").child(ChatId.toString())
        val dd = mDatabaseChat!!.orderByChild("read").equalTo("Unread")
        dd.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val countRead = dataSnapshot.childrenCount.toInt()
                startNode(key, dataSnapshot.key, last_chat, time, countRead)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var checkHi = false
    private var firstHi = true
    private var local = 0
    private fun lastChatHi(key: String?, ChatId: String?) {
        val chatDb = FirebaseDatabase.getInstance().reference.child("Chat")
        val lastNode = chatDb.child(ChatId.toString()).orderByKey().limitToLast(1)
        lastNode.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val lastChat = dataSnapshot.child("text").value.toString()
                val time = dataSnapshot.child("time").value.toString()
                checkSentBack(key, lastChat, time, ChatId)
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun checkSentBack(key: String?, lastChat: String?, time: String?, ChatId: String?) {
        val chatDb = FirebaseDatabase.getInstance().reference.child("Chat").child(ChatId.toString())
        val ww = chatDb.orderByChild("createByUser").equalTo(currentUserId)
        ww.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val matchHiDb = FirebaseDatabase.getInstance().reference.child("Users").child(key.toString()).child("connection").child("matches").child(currentUserId.toString())
                    matchHiDb.child("ChatId").setValue(ChatId)
                    val matchHiDbMatch = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches").child(key.toString())
                    matchHiDbMatch.child("ChatId").setValue(ChatId)
                    val dataDelete = FirebaseDatabase.getInstance().reference.child("Users")
                    dataDelete.child(currentUserId.toString()).child("connection").child("chatna").child(key.toString()).removeValue()
                    firstHi = false
                    checkHi = true
                    fetchHi(key, lastChat, time, ChatId)
                } else {
                    fetchHi(key, lastChat, time, ChatId)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var inception = false
    private fun fetchHi(key: String?, lastChat: String?, time: String?, ChatId: String?) {
        val userDb = FirebaseDatabase.getInstance().reference.child("Users").child(key.toString())
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")) {
                    val profileImageUrl:String?
                    profileImageUrl = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()
                    val userId = dataSnapshot.key
                    val name = dataSnapshot.child("name").value.toString()
                    val gender = dataSnapshot.child("sex").value.toString()
                    if (!firstHi) {
                        inception = false
                        val j = resultHi!!.map { T -> T!!.userId.equals(key) }.indexOf(element = true)
                        if (resultHi.size == 1) {
                            mHiRecyclerView.visibility = (View.GONE)
                            textEmpty.visibility = (View.VISIBLE)
                        }
                        resultHi.removeAt(j)
                        mHiAdapter.notifyItemRemoved(j)
                        mHiAdapter.notifyItemRangeChanged(j, resultHi.size)
                        chatCheckRead(ChatId, key, time, lastChat)
                    } else {
                        val obj2 = HiObject(userId, profileImageUrl, name, gender)
                        resultHi!!.add(obj2)
                        mHiAdapter.notifyDataSetChanged()
                    }
                }
                if (resultHi!!.size == 0) {
                    textEmpty.visibility = View.VISIBLE
                    mHiRecyclerView.visibility = View.GONE
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val bitmap: Bitmap? = null
    private fun fetchMatchFormation(key: String?, last_chat: String?, time: String?, count_unread: Int) {
        val userDb = FirebaseDatabase.getInstance().reference.child("Users").child(key.toString())
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userId = dataSnapshot.key
                    var name = ""
                    var profileImageUrl = ""
                    var status = ""
                    if (dataSnapshot.child("name").value != null) {
                        name = dataSnapshot.child("name").value.toString()
                    }
                    if (dataSnapshot.child("Status").hasChild("status")) {
                        status = dataSnapshot.child("Status").child("status").value.toString()
                    }
                    if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")) {
                        profileImageUrl = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()
                    }
                    val obj: MatchesObject?
                    if (countRead != 0) {
                        obj = MatchesObject(userId, name, profileImageUrl, status, last_chat, time, countRead, bitmap)
                        val myUnread = mContext?.getSharedPreferences("NotificationMessage", Context.MODE_PRIVATE)
                        val editorRead = myUnread?.edit()
                        editorRead?.putInt(key, countRead)
                        editorRead?.apply()
                        countRead = 0
                    } else {
                        obj = MatchesObject(userId, name, profileImageUrl, status, last_chat, time, count_unread, bitmap)
                    }
                    resultMatches?.add(obj)
                    mMatchesAdapter.notifyDataSetChanged()
                    if (checkHi) {
                        mRecyclerView.visibility = View.VISIBLE
                        chatEmpty.visibility = View.GONE
                    }
                    if (resultMatches?.size == userMatchCount) {
                        mRecyclerView.visibility = View.VISIBLE
                        p1?.hide()
                    }
                    Log.d("chatNotificationTest"," ${resultMatches!!.size} > $userMatchCount")
                    if (resultMatches!!.size > userMatchCount) {
                        Log.d("chatNotificationTest","+1 $createByBoolean")
                        for (j in 0 until (resultMatches.size-1)) {
                            //var index = resultMatches!!.map { T -> T!!.userId.equals(S1)  }.indexOf(element = true)
                            //Log.d("countIndexSomethings","$index , $S1")a
                            Log.d("loop1","$j ${resultMatches.elementAt(j)!!.userId} , ${resultMatches.size} , $count")
                            if (resultMatches.elementAt(j)?.userId == key) {
                                resultMatches.elementAt(j)?.late = last_chat
                                resultMatches.elementAt(j)?.count_unread = resultMatches.elementAt(resultMatches.size - 1)!!.count_unread
                                resultMatches.elementAt(j)?.time = time
                                if (j > 0) {
                                    for (b in j downTo 1) {
                                        Collections.swap(resultMatches, b, b - 1)
                                    }
                                }
                                resultMatches.removeAt(resultMatches.size - 1)
                                if (count == resultMatches.size) {
                                    --count
                                    Log.d("loop1","นับ $count")
                                }
                                inception = true
                            }
                        }
                        if (!inception) {
                            if (resultMatches.size > 1) {
                                for (b in resultMatches.size - 1 downTo 1) {
                                    Collections.swap(resultMatches, b, b - 1)
                                }
                            }
                        }
                    }
                }
                if (resultMatches!!.size > 1) {
                    if (resultMatches.elementAt(resultMatches.size - 1)!!.time != "-1") {
                        val compare1 = resultMatches.elementAt(resultMatches.size - 1)!!.time
                        val compare2 = resultMatches.elementAt(resultMatches.size - 2)!!.time
                        if (compare2 == "-1" && compare1 != "-1") {
                            local = resultMatches.size - 1
                        } else if (compare2 != "-1" && compare1 != "-1" && resultMatches.size == 2) {
                            local = 0
                        }
                        resultMatches.sortWith(Comparator { o1, o2 ->
                            var b1 = false
                            var b2 = false
                            var checkB1 = 0
                            var checkB2 = 0
                            if (o1!!.time!! == "-1") {
                                b1 = true
                            }
                            if (o2!!.time!! == "-1") {
                                b2 = true
                            }
                            if (b1) {
                                checkB1 = 1
                            }
                            if (b2) {
                                checkB2 = 1
                            }
                            checkB2 - checkB1
                        })
                        resultMatches.subList(local, resultMatches.size).sortWith(Comparator { o1, o2 ->
                            var b1 = false
                            var b2 = false
                            var checkB1 = 0
                            var checkB2 = 0
                            if (o1!!.time!!.substring(2, 3) == ":") {
                                b1 = true
                            }
                            if (o2!!.time!!.substring(2, 3) == ":") {
                                b2 = true
                            }
                            if (b1) {
                                checkB1 = 1
                            }
                            if (b2) {
                                checkB2 = 1
                            }
                            checkB2 - checkB1
                        })
                        resultMatches.sortWith(Comparator { o1, o2 ->
                            try {
                                return@Comparator SimpleDateFormat("HH:mm").parse(o2!!.time).compareTo(SimpleDateFormat("HH:mm").parse(o1!!.time))
                            } catch (e: ParseException) {
                                return@Comparator 0
                            }
                        })
                        resultMatches.sortWith(Comparator { o1, o2 ->
                            try {
                                return@Comparator SimpleDateFormat("dd/MM").parse(o2!!.time).compareTo(SimpleDateFormat("dd/MM").parse(o1!!.time))
                            } catch (e: ParseException) {
                                return@Comparator 0
                            }
                        })
                        /*if (compare2 != "-1") {
                            if (resultMatches.size == count) {
                                val jj = resultMatches.size - local
                                for (be in jj downTo 1) {
                                    for (i in resultMatches.size downTo local + 1) {
                                        val compare_hr_mn1 = resultMatches.elementAt(i - 1)?.time?.substring(2, 3)
                                        if (resultMatches.elementAt(i - 2)?.time?.substring(0, 2) != "-1") {
                                            val compare_hr_mn2 = resultMatches.elementAt(i - 2)?.time?.substring(2, 3)
                                            if (compare_hr_mn2 == "/" && compare_hr_mn1 == ":") {
                                                Collections.swap(resultMatches, i - 2, i - 1)
                                            }
                                        }
                                    }
                                }
                                for (i in resultMatches.size - 1 downTo local) {
                                    val check_sign = resultMatches.elementAt(i)!!.time?.substring(2, 3)
                                    if (check_sign == "/") {
                                        ++gi
                                    }
                                }
                                if (gi > 1) {
                                    for (be in gi downTo 1) {
                                        for (h in resultMatches.size downTo resultMatches.size - gi + 1 + 1) {
                                            val compare2_minute_in = Integer.valueOf(resultMatches.elementAt(h - 1)?.time!!.substring(0, 2))
                                            if (resultMatches.elementAt(h - 2)?.time!!.substring(0, 2) != "-1") {
                                                val compare1_minute_in = Integer.valueOf(resultMatches.elementAt(h - 2)?.time!!.substring(0, 2))
                                                if (compare2_minute_in > compare1_minute_in) {
                                                    Collections.swap(resultMatches, h - 1, h - 2)
                                                }
                                            }
                                        }
                                    }
                                    for (be in gi downTo 1) {
                                        for (h in resultMatches.size downTo resultMatches.size - gi + 1 + 1) {
                                            val compare2_minute_in = Integer.valueOf(resultMatches.elementAt(h - 1)?.time!!.substring(3, 5))
                                            if (resultMatches.elementAt(h - 2)?.time!!.substring(0, 2) != "-1") {
                                                val compare1_minute_in = Integer.valueOf(resultMatches.elementAt(h - 2)?.time!!.substring(3, 5))
                                                if (compare2_minute_in > compare1_minute_in) {
                                                    Collections.swap(resultMatches, h - 1, h - 2)
                                                }
                                            }
                                        }
                                    }
                                }
                                val check_stop = resultMatches.size - gi
                                for (be in jj downTo 1) {
                                    for (h in check_stop downTo local + 1) {
                                        val compare2_minute_in = Integer.valueOf(resultMatches.elementAt(h - 1)?.time!!.substring(3, 5))
                                        if (resultMatches.elementAt(h - 2)?.time!!.substring(0, 2) != "-1") {
                                            val compare1_minute_in = Integer.valueOf(resultMatches.elementAt(h - 2)?.time!!.substring(3, 5))
                                            if (compare2_minute_in > compare1_minute_in) {
                                                Collections.swap(resultMatches, h - 1, h - 2)
                                            }
                                        }
                                    }
                                }
                                for (be in jj downTo 1) {
                                    for (h in check_stop downTo local + 1) {
                                        val compare2_hr_in = Integer.valueOf(resultMatches.elementAt(h - 1)?.time!!.substring(0, 2))
                                        val compare1_hr_in = Integer.valueOf(resultMatches.elementAt(h - 2)?.time!!.substring(0, 2))
                                        if (compare1_hr_in != -1) {
                                            if (compare2_hr_in > compare1_hr_in) {
                                                Collections.swap(resultMatches, h - 1, h - 2)
                                            }
                                        }
                                    }
                                }
                            }
                        }*/
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private val resultMatches: ArrayList<MatchesObject?>? = ArrayList()
    private fun getDataSetMatches(): MutableList<MatchesObject?>? {
        return resultMatches
    }

    private val resultHi: ArrayList<HiObject?>? = ArrayList()
    private fun getDataSetHi(): MutableList<HiObject?>? {
        return resultHi
    }


    override fun onResume() {
        super.onResume()

        val myUnread = mContext!!.getSharedPreferences("NotificationActive", Context.MODE_PRIVATE)
        val s1 = myUnread.getString("ID", "null")
        if (s1 != "null") {
            val index = resultMatches!!.map { T -> T!!.userId.equals(s1) }.indexOf(element = true)
            Log.d("countIndexSomethings", "$index , $s1")
            if (resultMatches.size > 0) {
                resultMatches.elementAt(index)?.count_unread = 0
                mMatchesAdapter.notifyDataSetChanged()
            } else {
                resultMatches.elementAt(0)?.count_unread = 0
                mMatchesAdapter.notifyDataSetChanged()
            }
            myUnread.edit().clear().apply()
        }
        val myDelete = mContext!!.getSharedPreferences("DeleteChatActive", Context.MODE_PRIVATE)
        val s2 = myDelete.getString("ID", "null")
        if (s2 != "null") {
            val index = resultMatches!!.map { T -> T!!.userId.equals(s2) }.indexOf(element = true)
            Log.d("countIndexSomethings", "$index , $s2")
            if (resultMatches.size > 0) {
                resultMatches.elementAt(index)?.late = ""
                resultMatches.elementAt(index)?.time = "-1"
                mMatchesAdapter.notifyDataSetChanged()
            } else {
                resultMatches.elementAt(0)?.late = ""
                resultMatches.elementAt(0)?.time = "-1"
                mMatchesAdapter.notifyDataSetChanged()
            }
            myDelete.edit().clear().apply()
        }
    }

    private var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onPause() {
        super.onPause()
        countRead = 0

    }
}