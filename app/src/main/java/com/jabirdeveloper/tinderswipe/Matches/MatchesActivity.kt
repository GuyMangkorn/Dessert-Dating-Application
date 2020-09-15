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
    private lateinit var layout_chatna: LinearLayout
    private var currentUserId: String? = null
    private var date_user: String? = ""
    private val chk = 0
    private var count = 0
    private var p1: AVLoadingIndicatorView? = null
    private lateinit var text_empty: TextView
    private lateinit var chat_empty: TextView

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_matches, container, false)
        super.onCreate(savedInstanceState)
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        text_empty = view.findViewById(R.id.textempty)
        chat_empty = view.findViewById(R.id.chatempty)
        layout_chatna = view.findViewById(R.id.layoutRe)
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
        date_user = currentDate.format(calendar.time)
        mRecyclerView.visibility = View.GONE
        Chat_na_check()
        CheckNode()
        return view
    }

    private var check_first_connection = true
    private var check_first_matches = true
    private var check_first_remove: String? = "null"
    private var UserMatch_count = 0
    private fun CheckNode() {
        val matchDbCheck = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString());
        matchDbCheck.orderByKey().addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key.toString() == ("connection")) {
                    //Checkconnection()
                    CheckNodeMatch()
                    Log.d("test_check_matches", "connection_accept")
                } else if (check_first_connection) {
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

    /*private fun Checkconnection(){
        val matchDbCheck = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString());
        matchDbCheck.orderByKey().equalTo("connection").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    CheckNodeMatch()
                    Log.d("test_check_matches", "connection_accept")
                } else {
                    Log.d("test_check_matches", "connection_reject")
                    p1!!.hide()
                    chat_empty.visibility = View.VISIBLE
                    mRecyclerView.visibility = View.GONE
                }
            }
        })
    }*/
    private fun CheckNodeMatch() {
        val matchDbCheck = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection");
        matchDbCheck.orderByKey().addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key.toString() == ("matches")) {
                    getUserMarchId()
                    Log.d("test_check_matches", "matches_accept")
                    //CheckNodeMatch2()
                } else if (check_first_matches) {
                    check_first_matches = false
                    Log.d("test_check_matches", "matches_reject")
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
    }

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


    private fun getUserMarchId() {
        val matchDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches")
        matchDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                ++UserMatch_count
                Log.d("test_check_matches", "onChildAdd : ${dataSnapshot.key}")
                val chatID = dataSnapshot.child("ChatId").value.toString()
                Check_data(dataSnapshot.key, chatID)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                // Toast.makeText(mContext,dataSnapshot.getKey(),Toast.LENGTH_SHORT).show();
                if (check_first_remove == "null") {
                    --UserMatch_count
                    if (UserMatch_count == 0) {
                        chat_empty.visibility = View.VISIBLE
                        mRecyclerView.visibility = View.GONE
                    }
                    check_first_remove = dataSnapshot.key
                    UnMatch(dataSnapshot.key)
                } else if (check_first_remove != dataSnapshot.key) {
                    --UserMatch_count
                    if (UserMatch_count == 0) {
                        chat_empty.visibility = View.VISIBLE
                        mRecyclerView.visibility = View.GONE
                    }
                    check_first_remove = dataSnapshot.key
                    UnMatch(dataSnapshot.key)
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun UnMatch(key: String?) {
        var index = resultMatches!!.map { T -> T!!.userId.equals(key) }.indexOf(element = true)
        Log.d("count_indexsomethings", "$key ,index: $index")
        val MatchIDStored = mContext!!.getSharedPreferences(currentUserId + "Match_first", Context.MODE_PRIVATE)
        val editor2 = MatchIDStored.edit()
        editor2.remove(key).apply()
        //Toast.makeText(mContext,"i :"+(i)+" , "+resultMatches.get(i).getUserId(),Toast.LENGTH_SHORT).show();
        val MyUnread2 = mContext!!.getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
        val dd = MyUnread2.getInt("total", 0)
        //Toast.makeText(mContext,"total_before : "+(dd),Toast.LENGTH_SHORT).show();
        var unread = resultMatches[index]!!.count_unread
        //Toast.makeText(mContext,(dd)+"-"+(unread),Toast.LENGTH_SHORT).show();
        if (unread == -1) {
            unread = 0
        }
        val total = dd - unread

        //Toast.makeText(mContext,"total_after : "+(total),Toast.LENGTH_SHORT).show();
        (mContext as SwitchpageActivity?)!!.setCurrentIndex(total)
        val MyUnread1 = mContext!!.getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
        val editorRead = MyUnread1.edit()
        editorRead.putInt("total", total)
        editorRead.apply()
        resultMatches.removeAt(index)
        mMatchesAdapter.notifyItemRemoved(index)
        mMatchesAdapter.notifyItemRangeChanged(index, resultMatches.size)
    }

    private var UserChatNa_count = 0
    private var Check_first_remove_Chatna: String? = "null"
    private var First_system_2 = true
    private fun Chat_na_check() {
        val DBChatNa = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("chatna")
        DBChatNa.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (First_system_2) {
                    First_system_2 = false
                    if (dataSnapshot.exists()) {
                        Chat_na()
                    } else {
                        text_empty.visibility = View.VISIBLE
                        mHiRecyclerView.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun Chat_na() {
        val DBChatNa = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("chatna")
        DBChatNa.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                ++UserChatNa_count
                val ChatId = dataSnapshot.value.toString()
                last_chat_Hi(dataSnapshot.key, ChatId)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                if (Check_first_remove_Chatna == "null") {
                    --UserChatNa_count
                    if (UserChatNa_count == 0) {
                        text_empty.visibility = View.VISIBLE
                        mHiRecyclerView.visibility = View.GONE
                    }
                    Check_first_remove_Chatna = dataSnapshot.key
                    DeleteChatNA(dataSnapshot.key)
                } else if (Check_first_remove_Chatna != dataSnapshot.key) {
                    --UserChatNa_count
                    if (UserChatNa_count == 0) {
                        text_empty.visibility = View.VISIBLE
                        mHiRecyclerView.visibility = View.GONE
                    }
                    Check_first_remove_Chatna = dataSnapshot.key
                    DeleteChatNA(dataSnapshot.key)
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun DeleteChatNA(uIdChatNa: String?) {
        for (i in resultHi!!.indices) {
            if (resultHi[i]!!.userId == uIdChatNa) {
                resultHi.removeAt(i)
                mHiAdapter.notifyItemRemoved(i)
                mHiAdapter.notifyItemRangeChanged(i, resultHi.size)
            }
        }
    }

    private fun Check_data(key: String?, chatID: String?) {
        val check = FirebaseDatabase.getInstance().reference
        check.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("Users").hasChild(key.toString())) {
                    ++count
                    if (dataSnapshot.hasChild("Chat")) {
                        if (dataSnapshot.child("Chat").hasChild(chatID.toString())) {
                            latestChat(key, chatID)
                        } else {
                            val last_chat = ""
                            val time = "-1"
                            val count = -1
                            FecthMatchformation(key, last_chat, time, count)
                            get_node(key, chatID)
                        }
                    } else {
                        Log.d("test_check_matches", "datachage $key")
                        checkNodeFirst(key, chatID)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

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

    private fun checkNodeFirst(key: String?, ChatID: String?) {
        val check = FirebaseDatabase.getInstance().reference
        check.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild("Chat")) {
                    latestChat(key, ChatID)
                } else {
                    val last_chat = ""
                    val time = "-1"
                    val count = -1
                    //Log.d("test_check_matches",key)
                    FecthMatchformation(key, last_chat, time, count)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun get_node(key: String?, chatID: String?) {
        val chatdb = FirebaseDatabase.getInstance().reference.child("Chat")
        val cc = chatdb.orderByKey().equalTo(chatID)
        cc.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                check_getNode(key, chatID, dataSnapshot.key)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun check_getNode(key: String?, chatID: String?, key_chatId: String?) {
        val check = FirebaseDatabase.getInstance().reference.child("Chat")
        check.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (chatID == key_chatId) {
                    latestChat(key, chatID)
                } else {
                    val last_chat = ""
                    val time = "-1"
                    val count = -1
                    FecthMatchformation(key, last_chat, time, count)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun startNode(key: String?, keynode: String?, last_chat: String?, time: String?, count: Int) {
        val startdb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches").child(key.toString()).child("Start")
        startdb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    if (dataSnapshot.value.toString() == keynode) {
                        val last_chat2 = ""
                        val time2 = "-1"
                        FecthMatchformation(key, last_chat2, time2, count)
                    } else {
                        FecthMatchformation(key, last_chat, time, count)
                    }
                } else {
                    FecthMatchformation(key, last_chat, time, count)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    //private val chk_notification = false
    private fun latestChat(key: String?, chatID: String?) {
        val chatdb = FirebaseDatabase.getInstance().reference.child("Chat")
        val lastNode = chatdb.child(chatID.toString()).orderByKey().limitToLast(1)
        lastNode.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val last_chat = dataSnapshot.child("text").value.toString()
                var time = dataSnapshot.child("time").value.toString()
                val date = dataSnapshot.child("date").value.toString()
                val createBy = dataSnapshot.child("createByUser").value.toString()
                if (date != date_user) {
                    time = date.substring(0, 5)
                }
                if (createBy != currentUserId) {
                    Chat_check_read(chatID, key, time, last_chat)
                } else {
                    startNode(key, dataSnapshot.key, last_chat, time, 0)
                }
                /*if(resultMatches.size() == count || resultMatches.size() == count+1) {
                    if (createBy.equals(key)) {
                        chk_notification = true;
                    }
                }*/
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var count_read = 0
    private var mDatabaseChat: DatabaseReference? = null
    private fun Chat_check_read(ChatId: String?, key: String?, time: String?, last_chat: String?) {
        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat").child(ChatId.toString())
        val dd = mDatabaseChat!!.orderByChild("read").equalTo("Unread")
        dd.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val count_read = dataSnapshot.childrenCount.toInt()
                startNode(key, dataSnapshot.key, last_chat, time, count_read)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var gi = 0
    private var Check_hi = false
    private var first_hi = true
    private var local = 0
    private fun last_chat_Hi(key: String?, ChatId: String?) {
        val chatdb = FirebaseDatabase.getInstance().reference.child("Chat")
        val lastNode = chatdb.child(ChatId.toString()).orderByKey().limitToLast(1)
        lastNode.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val last_chat = dataSnapshot.child("text").value.toString()
                val time = dataSnapshot.child("time").value.toString()
                check_sentBack(key, last_chat, time, ChatId)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun check_sentBack(key: String?, lastChat: String?, time: String?, ChatId: String?) {
        val chatdb = FirebaseDatabase.getInstance().reference.child("Chat").child(ChatId.toString())
        val ww = chatdb.orderByChild("createByUser").equalTo(currentUserId)
        ww.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val MatchHiDb = FirebaseDatabase.getInstance().reference.child("Users").child(key.toString()).child("connection").child("matches").child(currentUserId.toString())
                    MatchHiDb.child("ChatId").setValue(ChatId)
                    val MatchHiDbMatch = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches").child(key.toString())
                    MatchHiDbMatch.child("ChatId").setValue(ChatId)
                    val datadelete = FirebaseDatabase.getInstance().reference.child("Users")
                    datadelete.child(currentUserId.toString()).child("connection").child("chatna").child(key.toString()).removeValue()
                    first_hi = false
                    Check_hi = true
                    FecthHi(key, lastChat, time, ChatId)
                } else {
                    FecthHi(key, lastChat, time, ChatId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var Inception = false
    private fun FecthHi(key: String?, lastChat: String?, time: String?, ChatId: String?) {
        val userDb = FirebaseDatabase.getInstance().reference.child("Users").child(key.toString())
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")) {
                    var profileImageUrl = ""
                    profileImageUrl = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()
                    val userId = dataSnapshot.key
                    val name = dataSnapshot.child("name").value.toString()
                    val gender = dataSnapshot.child("sex").value.toString()
                    if (!first_hi) {
                        Inception = false
                        var j = resultHi!!.map { T -> T!!.userId.equals(key) }.indexOf(element = true)
                        //for (j in 0 until resultHi!!.size-1) {
                        //if (resultHi[j]!!.userId == key) {
                        if (resultHi.size == 1) {
                            mHiRecyclerView.visibility = (View.GONE)
                            text_empty.visibility = (View.VISIBLE)
                        }
                        resultHi.removeAt(j)
                        mHiAdapter.notifyItemRemoved(j)
                        mHiAdapter.notifyItemRangeChanged(j, resultHi.size)
                        //}
                        //}
                        Chat_check_read(ChatId, key, time, lastChat)
                    } else {
                        val obj2 = HiObject(userId, profileImageUrl, name, gender)
                        resultHi!!.add(obj2)
                        mHiAdapter.notifyDataSetChanged()
                    }
                }
                if (resultHi!!.size == 0) {
                    text_empty.visibility = View.VISIBLE
                    mHiRecyclerView.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val bitmap: Bitmap? = null
    private fun FecthMatchformation(key: String?, last_chat: String?, time: String?, count_unread: Int) {
        val userDb = FirebaseDatabase.getInstance().reference.child("Users").child(key.toString())
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userId = dataSnapshot.key
                    var name = ""
                    var profileImageUrl = ""
                    var status = ""
                    var gender: String? = "null"
                    if (dataSnapshot.child("name").value != null) {
                        name = dataSnapshot.child("name").value.toString()
                    }
                    if (dataSnapshot.child("Status").hasChild("status")) {
                        status = dataSnapshot.child("Status").child("status").value.toString()
                    }
                    if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")) {
                        profileImageUrl = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()
                    }
                    gender = dataSnapshot.child("sex").value.toString()
                    var obj: MatchesObject? = null
                    if (count_read != 0) {
                        obj = MatchesObject(userId, name, profileImageUrl, status, last_chat, time, count_read, bitmap)
                        val MyUnread = mContext?.getSharedPreferences("NotificationMessage", Context.MODE_PRIVATE)
                        val editorRead = MyUnread?.edit()
                        editorRead?.putInt(key, count_read)
                        editorRead?.apply()
                        count_read = 0
                    } else {
                        obj = MatchesObject(userId, name, profileImageUrl, status, last_chat, time, count_unread, bitmap)
                    }
                    resultMatches?.add(obj)
                    mMatchesAdapter.notifyDataSetChanged()
                    if (Check_hi) {
                        mRecyclerView.visibility = View.VISIBLE;
                        chat_empty.visibility = View.GONE;
                    }
                    if (resultMatches?.size == count) {
                        mRecyclerView.visibility = View.VISIBLE;
                        p1?.hide()
                    }
                    //Toast.makeText(mContext,resultMatches!!.elementAt(resultMatches.size - 1)!!.getUserId(),Toast.LENGTH_SHORT).show()
                    /*if (resultMatches!!.size > count) {    //ลูปเช็ค list ซ้ำ

                        for (j in 0 until (resultMatches.size-1)) {
                            //var index = resultMatches!!.map { T -> T!!.userId.equals(S1)  }.indexOf(element = true)
                            //Log.d("count_indexsomethings","$index , $S1")
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
                                Inception = true
                            }
                        }
                        if (!Inception) {
                            if (resultMatches.size > 1) {
                                for (b in resultMatches.size - 1 downTo 1) {
                                    Collections.swap(resultMatches, b, b - 1)
                                }
                            }
                        }
                    }*/
                }
                if (resultMatches!!.size > 1) { // ลูปเรียงจากเวลาการแชทล่าสุด
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
                            var chk_b1 = 0
                            var chk_b2 = 0
                            if (o1!!.time!! == "-1") {
                                b1 = true
                            }
                            if (o2!!.time!! == "-1") {
                                b2 = true
                            }
                            if (b1) {
                                chk_b1 = 1
                            }
                            if (b2) {
                                chk_b2 = 1
                            }
                            chk_b2 - chk_b1
                        })
                        resultMatches.subList(local, resultMatches.size).sortWith(Comparator { o1, o2 ->
                            var b1 = false
                            var b2 = false
                            var chk_b1 = 0
                            var chk_b2 = 0
                            if (o1!!.time!!.substring(2, 3) == ":") {
                                b1 = true
                            }
                            if (o2!!.time!!.substring(2, 3) == ":") {
                                b2 = true
                            }
                            if (b1) {
                                chk_b1 = 1
                            }
                            if (b2) {
                                chk_b2 = 1
                            }
                            chk_b2 - chk_b1
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

    /*private List<String> IDNotification = new ArrayList<String>();
    private List<Integer> IndexNotification = new ArrayList<Integer>();
    private int id_plus  = 0;
    private void Notification_chat(Bitmap icon, String lastChat, String Name, String time, String ID){
        Intent intent = new Intent(mContext, ChatActivity.class);
        Bundle b = new Bundle();
        Random random = new Random();
        boolean TwoItems = false;
        int id = 0;
        if(IDNotification.size() == 0) {
            id = ++id_plus;
            IDNotification.add(ID);
            IndexNotification.add(id);
        }else{
            for(int i = 0;i<IDNotification.size();i++){
                if(IDNotification.get(i).equals(ID)){
                    TwoItems = true;
                    id = IndexNotification.get(i);
                }
            }
            if(TwoItems == false){
                id = ++id_plus;
                Toast.makeText(mContext,"id :"+(id),Toast.LENGTH_SHORT).show();
                IDNotification.add(ID);
                IndexNotification.add(id);
            }
        }
        b.putString("time_chk",time);
        b.putString("matchId",ID);
        b.putString("nameMatch",Name);
        b.putString("unread","-1");
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,1,intent,PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new NotificationCompat.Builder(mContext,CHANNEL_ID)
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
                .build();
        Notification Sum = new NotificationCompat.Builder(mContext,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_love)
                .setStyle(new NotificationCompat.InboxStyle().setBigContentTitle("ข้อความใหม่").setSummaryText("ข้อความใหม่ต้าวหี"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup("Chat")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .build();

        notificationManager = NotificationManagerCompat.from(mContext);

        if(id == 2){
            notificationManager.notify(id+random.nextInt(9999 - 1000) + 1000,Sum);
        }
        notificationManager.notify(id,notification);
    }*/
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

        val MyUnread = mContext!!.getSharedPreferences("NotificationActive", Context.MODE_PRIVATE)
        val S1 = MyUnread.getString("ID", "null")
        if (S1 != "null") {
            var index = resultMatches!!.map { T -> T!!.userId.equals(S1) }.indexOf(element = true)
            Log.d("count_indexsomethings", "$index , $S1")
            if (resultMatches!!.size > 0) {
                resultMatches.elementAt(index)?.count_unread = 0
                mMatchesAdapter.notifyDataSetChanged()
            } else {
                resultMatches.elementAt(0)?.count_unread = 0
                mMatchesAdapter.notifyDataSetChanged()
            }
            MyUnread.edit().clear().apply()
        }
        val MyDelete = mContext!!.getSharedPreferences("DeleteChatActive", Context.MODE_PRIVATE)
        val S2 = MyDelete.getString("ID", "null")
        if (S2 != "null") {
            var index = resultMatches!!.map { T -> T!!.userId.equals(S2) }.indexOf(element = true)
            Log.d("count_indexsomethings", "$index , $S2")
            if (resultMatches!!.size > 0) {
                resultMatches.elementAt(index)?.late = ""
                resultMatches.elementAt(index)?.time = "-1"
                mMatchesAdapter.notifyDataSetChanged()
            } else {
                resultMatches.elementAt(0)?.late = ""
                resultMatches.elementAt(0)?.time = "-1"
                mMatchesAdapter.notifyDataSetChanged()
            }
            MyDelete.edit().clear().apply()
        }
    }

    private var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onPause() {
        super.onPause()
        count_read = 0

    }
}