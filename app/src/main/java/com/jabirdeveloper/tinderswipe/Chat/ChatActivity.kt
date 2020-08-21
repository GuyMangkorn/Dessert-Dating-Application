package com.jabirdeveloper.tinderswipe.Chat

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.jabirdeveloper.tinderswipe.*
import com.jabirdeveloper.tinderswipe.R
import com.tapadoo.alerter.Alerter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mChatAdapter: RecyclerView.Adapter<*>
    private lateinit var mChatLayoutManager: RecyclerView.LayoutManager
    private lateinit var linearLayout_oval_send: LinearLayout
    private lateinit var menu: LinearLayout
    private lateinit var LinearRecord: LinearLayout
    private lateinit var toolbar: Toolbar
    private lateinit var img_send: ImageView
    private lateinit var mSend_image: ImageView
    private lateinit var mCamera_open: ImageView
    private lateinit var menubar: ImageView
    private lateinit var profile: ImageView
    private lateinit var back: ImageView
    private lateinit var mRecord: ImageView
    private lateinit var mRecord_real: ImageView
    private lateinit var mName_chat: TextView
    private lateinit var mRecordStatus: TextView
    private lateinit var mSendButton: Button
    private lateinit var Open_menu: Button
    private var chk = 0
    private val count = 0
    private var chk2 = 0
    private var time_count = 0
    private var currentUserId: String? = null
    private var matchId: String? = null
    private var chatId: String? = null
    private var UrlImage: String? = null
    private var name_chat: String? = null
    private var time_chk: String? = null
    private val chatna: String? = null
    private val first_chat: String? = null
    private var fileName: String? = null

    private var file_uri: Uri? = null
    private var uri_camera: Uri? = null
    private var mSendEditText: CustomEdittext? = null
    private var cHeck_back = 0
    private var pro: ProgressBar? = null
    private var proAudio: ProgressBar? = null
    private var recorder: MediaRecorder? = null
    private var active = true
    private var T: Timer? = null
    private var i = 0
    private var return_d = 0
    private var dialog: Dialog? = null
    var mDatabaseUser: DatabaseReference? = null
    var mDatabaseChat: DatabaseReference? = null
    var mDatabaseImage: DatabaseReference? = null
    var user_database: DatabaseReference? = null
    var usersDb: DatabaseReference? = null
    private val MY_PERMISSIONS_REQUEST_READ_MEDIA = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf<String?>(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_MEDIA)
        } else {
        }
        //toolbar = findViewById(R.id.my_tools)
        mRecord = findViewById<View?>(R.id.record_audio) as ImageView
        profile = findViewById(R.id.pre_Image_porfile)
        back = findViewById(R.id.imageView5)
        img_send = findViewById<View?>(R.id.img_send) as ImageView
        mCamera_open = findViewById<View?>(R.id.camera_open) as ImageView
        proAudio = findViewById<View?>(R.id.progressBar_audio) as ProgressBar
        pro = findViewById<View?>(R.id.progressBar_Chat) as ProgressBar
        matchId = intent.extras!!.getString("matchId")
        var unread_count = intent!!.extras!!.getString("unread")
        if (unread_count == "-1") {
            val MyUnread = getSharedPreferences("NotificationMessage", Context.MODE_PRIVATE)
            val dd2 = MyUnread.getInt(matchId, 0)
            //Toast.makeText(ChatActivity.this, "MatchId " + matchId + " , "+(dd2), Toast.LENGTH_SHORT).show();
            val RemoveNotification = getSharedPreferences("NotificationActive", Context.MODE_PRIVATE)
            val editorRead = RemoveNotification.edit()
            editorRead.putString("ID", matchId)
            editorRead.apply()
            unread_count = dd2.toString()
        }
        ///////////////// loading..
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.progress_dialog, null)
        dialog = Dialog(this)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(view)
        dialog!!.setCancelable(false)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog!!.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        ///////////////// loading..
        val mySharedPreferences = getSharedPreferences("SentRead", Context.MODE_PRIVATE)
        val editor = mySharedPreferences.edit()
        editor.putInt("Read", Integer.valueOf(unread_count!!.toInt()))
        editor.apply()
        name_chat = intent!!.extras!!.getString("nameMatch")
        time_chk = intent!!.extras!!.getString("time_chk")
        mRecord_real = findViewById<View?>(R.id.record_real) as ImageView
        Open_menu = findViewById(R.id.menu_button)
        mSendEditText = findViewById(R.id.message)
        menu = findViewById(R.id.menu_app)
        mRecordStatus = findViewById<View?>(R.id.record_status) as TextView
        LinearRecord = findViewById<View?>(R.id.Linear_record) as LinearLayout
        menubar = findViewById(R.id.menubar)
        fileName = externalCacheDir!!.absolutePath
        fileName += "/recorded_audio.3gp"
        linearLayout_oval_send = findViewById<View?>(R.id.oval_send) as LinearLayout
        mName_chat = findViewById<View?>(R.id.name_chat) as TextView
        mSend_image = findViewById(R.id.send_image)
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")
        mDatabaseUser = if (intent.hasExtra("Hi")) {
            FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("chatna").child(matchId.toString())
        } else {
            FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches").child(matchId.toString()).child("ChatId")
        }
        mDatabaseImage = FirebaseDatabase.getInstance().reference.child("Users").child(matchId.toString()).child("ProfileImage").child("profileImageUrl0")
        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat")
        mRecord.setOnClickListener(View.OnClickListener {
            if (ActivityCompat.checkSelfPermission(this@ChatActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@ChatActivity, arrayOf<String?>(
                        Manifest.permission.RECORD_AUDIO), 72)
            } else {
                if (LinearRecord.visibility == View.GONE) {
                    proAudio!!.visibility = View.GONE
                    LinearRecord.visibility = View.VISIBLE
                    mRecord_real.visibility = View.VISIBLE
                    mRecordStatus.text = "Press to Record"
                } else {
                    LinearRecord.visibility = View.GONE
                }
                /*if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        startRecording();
                        Toast.makeText(ChatActivity.this,"Start",Toast.LENGTH_SHORT).show();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        stopRecording();
                        Toast.makeText(ChatActivity.this,"Stop",Toast.LENGTH_SHORT).show();
                    }*/
            }
        })
        mRecord_real.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                startRecording()
                //Toast.makeText(ChatActivity.this,"chk",Toast.LENGTH_SHORT).show();
                mRecordStatus.text  = ("00:00")
                T = Timer()
                T!!.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            ++time_count
                            val minute = time_count / 60
                            val second = time_count % 60
                            mRecordStatus.text = (String.format("%02d", minute) + ":" + String.format("%02d", second))
                        }
                    }
                }, 1000, 1000)
            } else if (event.action == MotionEvent.ACTION_UP) {
                T!!.cancel()
                stopRecording()
                mRecord_real.visibility = (View.GONE)
                proAudio!!.visibility = (View.VISIBLE)
                mRecordStatus.text = ("Uploading.....")
            }
            true
        })
        back.setOnClickListener(View.OnClickListener { onBackPressed() })
        menubar.setOnClickListener(View.OnClickListener { v ->
            val dd = PopupMenu(this@ChatActivity, v)
            dd.menuInflater.inflate(R.menu.menu_bar, dd.menu)
            dd.gravity = Gravity.END
            dd.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.menu_unmatch) {
                    Alerter.create(this@ChatActivity)
                            .setTitle(getString(R.string.cancel_match2))
                            .setText(getString(R.string.cancel_match_confirm))
                            .setIconColorFilter(Color.parseColor("#FFFFFF"))
                            .setBackgroundColorInt(Color.parseColor("#FF5050"))
                            .setIcon(ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_warning_black_24dp)!!)
                            .addButton(getString(R.string.cancle), R.style.AlertButton, View.OnClickListener { Alerter.hide() })
                            .addButton(getString(R.string.ok), R.style.AlertButton, View.OnClickListener {
                                Alerter.hide()
                                deletechild()
                            })
                            .show()
                } else if (item.itemId == R.id.menu_delete) {
                    val choice_text = resources.getStringArray(R.array.report_item)
                    val checked_item = BooleanArray(choice_text.size)
                    val builder = AlertDialog.Builder(this@ChatActivity)
                    builder.setTitle(R.string.dialog_reportUser)
                    builder.setMultiChoiceItems(R.array.report_item, checked_item) { dialog, which, isChecked ->
                        checked_item[which] = isChecked
                        val item = choice_text[which]
                    }
                    builder.setPositiveButton(getString(R.string.dialog_report)) { dialog, which ->
                        return_d = 0
                        i = 0
                        while (i < choice_text.size) {
                            val checked = checked_item[i]
                            if (checked) {
                                update(i.toString())
                            }
                            i++
                        }
                        UpdateDate()
                    }
                    builder.setNegativeButton(R.string.cancle) { dialog, which -> dialog.dismiss() }
                    val mDialog = builder.create()
                    mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@ChatActivity, R.drawable.myrect2))
                    mDialog.show()
                } else if (item.itemId == R.id.delete_chat) {
                    Alerter.create(this@ChatActivity)
                            .setTitle(getString(R.string.delete_message_all))
                            .setText(getString(R.string.delete_message_all_confirm))
                            .setIconColorFilter(Color.parseColor("#FFFFFF"))
                            .setBackgroundColorInt(Color.parseColor("#FF5050"))
                            .setIcon(ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_warning_black_24dp)!!)
                            .addButton(getString(R.string.cancle), R.style.AlertButton, View.OnClickListener { Alerter.hide() })
                            .addButton(getString(R.string.ok), R.style.AlertButton, View.OnClickListener {
                                Alerter.hide()
                                val GetStart = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches").child(matchId.toString()).child("Start")
                                GetStart.setValue(FetchId!!.get(FetchId!!.size - 1))
                                val prefs1 = getSharedPreferences(chatId, Context.MODE_PRIVATE)
                                val allPrefs = prefs1.all
                                val set = allPrefs.keys
                                for (s in set) {
                                    Log.d("Id1", s)
                                    getSharedPreferences(s, Context.MODE_PRIVATE).edit().clear().apply()
                                }
                                getSharedPreferences(chatId, Context.MODE_PRIVATE).edit().clear().apply()
                                FetchId.clear()
                                start = "null"
                                sizePre = 0
                                resultChat!!.clear()
                                mChatAdapter.notifyDataSetChanged()
                                val RemoveNotification = getSharedPreferences("DeleteChatActive", Context.MODE_PRIVATE)
                                val editorRead = RemoveNotification.edit()
                                editorRead.putString("ID", matchId)
                                editorRead.apply()
                            })
                            .show()
                } else {
                    Toast.makeText(this@ChatActivity, "" + item, Toast.LENGTH_SHORT).show()
                }
                true
            }
            dd.setOnDismissListener { }
            dd.show()
        })
        profile.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, ProfileUserOppositeActivity2::class.java)
            intent.putExtra("madoo", "1")
            intent.putExtra("User_opposite", matchId)
            startActivity(intent)
        })
        /* setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("555");
        getSupportActionBar().setIcon(R.drawable.maicar);
        int imageHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        Glide.with(this).asDrawable().load(R.drawable.maicar).apply(new RequestOptions().circleCrop()).override(imageHeight, imageHeight).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                getSupportActionBar().setIcon(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });*/
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Open_menu.setOnClickListener(View.OnClickListener {
            if (menu.visibility  == View.GONE) {
                Open_menu.visibility = View.GONE
                menu.visibility = View.VISIBLE
            }
        })
        getImageProfile()
        mName_chat.text = name_chat
        mRecyclerView = findViewById<View?>(R.id.recyclerView_2) as RecyclerView
        val mChatLayoutManager = LinearLayoutManager(this@ChatActivity)
        mRecyclerView.layoutManager = mChatLayoutManager
        mChatAdapter = ChatAdapter(getDataSetChat(), this@ChatActivity)
        mSendButton = findViewById(R.id.send)
        mSend_image.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(Intent.createChooser(intent, "เลือกรูปภาพ"), 23)
        })
        mSendEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 0) {
                    mSendButton.background = ContextCompat.getDrawable(this@ChatActivity, R.drawable.chat_after)
                    mSendButton.rotation = 0f
                } else {
                    mSendButton.background = ContextCompat.getDrawable(this@ChatActivity, R.drawable.chat_before)
                    mSendButton.rotation = 0f
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        mSendEditText!!.setOnEditTextImeBackListener(object : EditTextImeBackListener {
            override fun onImeBack(ctrl: CustomEdittext?, text: String?) {
                menu.visibility = View.VISIBLE
                mSendEditText!!.clearFocus()
                Open_menu.visibility = View.GONE
            }
        })
        mSendEditText!!.setOnFocusChangeListener{ view, b ->
            if (b) {
                LinearRecord.visibility = View.GONE
                menu.visibility = View.GONE
                Open_menu.visibility = View.VISIBLE
            } else {
                menu.visibility =  View.VISIBLE
                Open_menu.visibility = View.GONE
            }
        }
        mSendButton.setOnClickListener(View.OnClickListener { sendMessage() })
        linearLayout_oval_send.setOnClickListener(View.OnClickListener { sendMessage() })
        mCamera_open.setOnClickListener(View.OnClickListener {
            if (ActivityCompat.checkSelfPermission(this@ChatActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@ChatActivity, arrayOf<String?>(
                        Manifest.permission.CAMERA), 2)
            } else {
                val values = ContentValues()
                uri_camera = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_camera)
                startActivityForResult(intent, 33)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_MEDIA) {
            if (grantResults.size > 0 && grantResults!!.get(0) == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }


    private fun getImageProfile() {
        mDatabaseImage!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    UrlImage = dataSnapshot.value.toString()
                    Glide.with(applicationContext).load(UrlImage).apply(RequestOptions().override(100, 100)).into(profile)
                } else {
                    if (intent.getStringExtra("gender") == "Female") Glide.with(applicationContext).load(R.drawable.ic_woman).apply(RequestOptions().override(100, 100)).into(profile) else Glide.with(applicationContext).load(R.drawable.ic_man).apply(RequestOptions().override(100, 100)).into(profile)
                }
                getChatId()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendMessage() {
        val sendMessageText = mSendEditText!!.getText().toString()
        if (!sendMessageText.isEmpty()) {
            val calendar = Calendar.getInstance()
            val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
            val time_user = currentTime.format(calendar.time)
            val currentDate = SimpleDateFormat("dd/MM/yyyy")
            val date_user = currentDate.format(calendar.time)
            val newMessageDb = mDatabaseChat!!.push()
            val newMessage = hashMapOf(
                    "createByUser" to currentUserId,
                    "text" to sendMessageText,
                    "time" to time_user,
                    "date" to date_user,
                    "read" to "Unread")
            newMessageDb.setValue(newMessage)
        }
        mSendEditText!!.text = (null)
    }

    private fun getChatId() {
        mDatabaseUser!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.value.toString()
                    mDatabaseChat = mDatabaseChat!!.child(chatId.toString())
                    user_database = FirebaseDatabase.getInstance().reference.child("Chat").child(chatId.toString())
                    fetch_sharedPreference()
                    //getCount()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun Chat_check_read() {
        val dd = mDatabaseChat!!.orderByChild("read").equalTo("Unread")
        dd.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (loop in dataSnapshot.children) {
                    read_already(loop.key)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun read_already(key: String?) {
        mDatabaseChat!!.child(key.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("createByUser").value.toString() == matchId) {
                    mDatabaseChat!!.child(key.toString()).child("read").setValue("Read")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var count_node_d = 0
    private fun getCount() {
        val dd = FirebaseDatabase.getInstance().reference.child("Chat")
        dd.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild(chatId.toString())) {
                    pro!!.visibility = View.INVISIBLE
                }
                fetch_sharedPreference()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var c = 0
    private fun getcount(): Int {
        mDatabaseChat!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                c = dataSnapshot.childrenCount.toInt()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        return c
    }

    private var first_connect = true
    private var start: String? = "null"
    private fun getFirstNode() {
        val GetStart = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches").child(matchId.toString()).child("Start")
        GetStart.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (first_connect) {
                    first_connect = false
                    if (dataSnapshot.exists()) {
                        start = dataSnapshot.value.toString()
                        getChatMessages()
                    } else {
                        getChatMessages()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var sizePre = 0
    private val FetchId: MutableList<String?>? = ArrayList()
    private fun fetch_sharedPreference() {
        val prefs = getSharedPreferences(chatId, Context.MODE_PRIVATE)
        val allPrefs = prefs.all
        val set = allPrefs.keys
        for (s in set) {
            FetchId!!.add(s)
        }
        for (s in set) {
            Log.d("Id2", "" + prefs.getInt(s, 0))
            FetchId!!.set(prefs.getInt(s, 0) - 1, s)
        }
        sizePre = FetchId!!.size
        SetMessage()
    }

    private fun SetMessage() {
        for (i in (FetchId!!.indices)) {
            c++
            var message: String
            var createdByUser: String
            var time: String
            var url_send = "default"
            var audio: String
            var read : String
            var audio_length: String
            val MyInNode = getSharedPreferences(FetchId.elementAt(i), Context.MODE_PRIVATE)
            message = MyInNode.getString("text", "null")!!
            read = MyInNode.getString("read","null")!!
            createdByUser = MyInNode.getString("createByUser", "null")!!
            time = MyInNode.getString("time", "null")!!
            val Check = MyInNode.getString("image", "null")
            Log.d("text_chat", message)
            if (Check != "default" && Check != "null") {
                ++chk2
                url_send = MyInNode.getString("image", "null")!!
            }
            audio = MyInNode.getString("audio", "null")!!
            audio_length = MyInNode.getString("audio_length", "null")!!
            if (createdByUser != null && time != null) {
                var currentUserBoolean = false
                if (createdByUser == currentUserId) {
                    currentUserBoolean = true
                }else if(c == FetchId.size){
                    if(createdByUser != currentUserId)
                        Chat_check_read()
                }
                val newMessage = ChatObject(message, currentUserBoolean, UrlImage, time, chatId, url_send, chk2, matchId, audio, audio_length, currentUserId)
                resultChat!!.add(newMessage)
                ++chk
                if (FetchId.size == chk) {
                    mChatAdapter.notifyDataSetChanged()
                    mRecyclerView.adapter = mChatAdapter
                    mRecyclerView.scrollToPosition(resultChat.size - 1)
                    pro!!.visibility = View.INVISIBLE
                    count_node_d = FetchId.size
                }
            }
        }
        getFirstNode()
    }

    private fun getChatMessages() {
        var ChatDatabase: Query? = mDatabaseChat
        if (FetchId!!.size > 0) {
            Toast.makeText(this@ChatActivity, "Size > 1 :" + FetchId.elementAt(FetchId.size - 1), Toast.LENGTH_SHORT).show()
            ChatDatabase = mDatabaseChat!!.orderByKey().startAt(FetchId.elementAt(FetchId.size - 1))
        } else if (start != "null" && FetchId.size == 0) {
            Toast.makeText(this@ChatActivity, "Size == 0 :$start", Toast.LENGTH_SHORT).show()
            ChatDatabase = mDatabaseChat!!.orderByKey().startAt(start)
        }
        ChatDatabase!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.exists()) {
                    if (FetchId.size > 0) {
                        //Toast.makeText(this@ChatActivity,dataSnapshot.key + " , "+FetchId.elementAt(FetchId.size - 1),Toast.LENGTH_LONG).show()
                        if (dataSnapshot.key != FetchId.elementAt(FetchId.size - 1)) {
                            c++
                            var message: String? = null
                            var createdByUser: String? = null
                            var time: String? = null
                            var url_send = "default"
                            var audio = "null"
                            var audio_length = "null"
                            var read = "null"
                            val MyNode = getSharedPreferences(chatId, Context.MODE_PRIVATE)
                            val S1 = MyNode.getInt(dataSnapshot.key, 0)
                            Log.d("unsave", dataSnapshot.key)
                            //Toast.makeText(this@ChatActivity,read,Toast.LENGTH_LONG).show()
                            if (dataSnapshot.child("read").value != null) {
                                read = dataSnapshot.child("read").value.toString()
                            }
                            if (dataSnapshot.child("text").value != null) {
                                message = dataSnapshot.child("text").value.toString()
                            }
                            if (dataSnapshot.child("createByUser").value != null) {
                                createdByUser = dataSnapshot.child("createByUser").value.toString()
                            }
                            if (dataSnapshot.child("time").value != null) {
                                time = dataSnapshot.child("time").value.toString()
                            }
                            if (dataSnapshot.child("image").value != null) {
                                url_send = dataSnapshot.child("image").value.toString()
                                ++chk2
                            }
                            if (dataSnapshot.child("audio").value != null) {
                                audio = dataSnapshot.child("audio").value.toString()
                                audio_length = dataSnapshot.child("audio_length").value.toString()
                            }
                            val ChatMessageStored = getSharedPreferences(chatId, Context.MODE_PRIVATE)
                            val editorRead = ChatMessageStored.edit()
                            editorRead.putInt(dataSnapshot.key, ++sizePre)
                            //Toast.makeText(this@ChatActivity,"UnSave"+(sizePre),Toast.LENGTH_SHORT).show();
                            FetchId.add(dataSnapshot.key)
                            editorRead.apply()
                            val NodeChatMessageStored = getSharedPreferences(dataSnapshot.key, Context.MODE_PRIVATE)
                            val NodeEditorRead = NodeChatMessageStored.edit()
                            NodeEditorRead.putString("text", message)
                            NodeEditorRead.putString("time", time)
                            NodeEditorRead.putString("createByUser", createdByUser)
                            NodeEditorRead.putString("image", url_send)
                            NodeEditorRead.putString("audio", audio)
                            NodeEditorRead.putString("audio_length", audio_length)
                            NodeEditorRead.putString("read", read)
                            NodeEditorRead.apply()
                            if (createdByUser != null && time != null) {
                                var currentUserBoolean = false
                                if (createdByUser == currentUserId) {
                                    currentUserBoolean = true
                                } else {
                                    if (active) {
                                        if (dataSnapshot.child("read").value.toString() == "Unread") {
                                            Toast.makeText(this@ChatActivity,dataSnapshot.child("read").value.toString(),Toast.LENGTH_LONG).show()
                                            Chat_check_read()
                                        }
                                    }
                                }
                                val newMessage = ChatObject(message, currentUserBoolean, UrlImage, time, chatId, url_send, chk2, matchId, audio, audio_length, currentUserId)
                                resultChat!!.add(newMessage)
                                mChatAdapter.notifyDataSetChanged()
                                ++chk
                                if (FetchId.size == 1) {
                                    mChatAdapter.notifyDataSetChanged()
                                    mRecyclerView.adapter = mChatAdapter
                                    mRecyclerView.scrollToPosition(resultChat!!.size - 1)
                                    pro!!.visibility = View.INVISIBLE
                                } else if (count_node_d < chk) {
                                    mRecyclerView.smoothScrollToPosition(mRecyclerView.adapter!!.getItemCount() - 1)
                                }
                            }
                        }
                    } else if (dataSnapshot.key != start) {
                        Toast.makeText(this@ChatActivity,"First",Toast.LENGTH_LONG).show()

                        c++
                        var message: String? = null
                        var createdByUser: String? = null
                        var time: String? = null
                        var url_send = "default"
                        var audio = "null"
                        var audio_length = "null"
                        var read = "null"
                        Log.d("unsave", dataSnapshot.key)
                        //Toast.makeText(this@ChatActivity,read,Toast.LENGTH_LONG).show()
                        if (dataSnapshot.child("read").value != null) {
                            read = dataSnapshot.child("read").value.toString()
                        }
                        if (dataSnapshot.child("text").value != null) {
                            message = dataSnapshot.child("text").value.toString()
                        }
                        if (dataSnapshot.child("createByUser").value != null) {
                            createdByUser = dataSnapshot.child("createByUser").value.toString()
                        }
                        if (dataSnapshot.child("time").value != null) {
                            time = dataSnapshot.child("time").value.toString()
                        }
                        if (dataSnapshot.child("image").value != null) {
                            url_send = dataSnapshot.child("image").value.toString()
                            ++chk2
                        }
                        if (dataSnapshot.child("audio").value != null) {
                            audio = dataSnapshot.child("audio").value.toString()
                            audio_length = dataSnapshot.child("audio_length").value.toString()
                        }
                        val ChatMessageStored = getSharedPreferences(chatId, Context.MODE_PRIVATE)
                        val editorRead = ChatMessageStored.edit()
                        editorRead.putInt(dataSnapshot.key, ++sizePre)
                        //Toast.makeText(ChatActivity.this,"UnSave"+(sizePre),Toast.LENGTH_SHORT).show();
                        FetchId.add(dataSnapshot.key)
                        editorRead.apply()
                        val NodeChatMessageStored = getSharedPreferences(dataSnapshot.key, Context.MODE_PRIVATE)
                        val NodeEditorRead = NodeChatMessageStored.edit()
                        NodeEditorRead.putString("text", message)
                        NodeEditorRead.putString("time", time)
                        NodeEditorRead.putString("createByUser", createdByUser)
                        NodeEditorRead.putString("image", url_send)
                        NodeEditorRead.putString("audio", audio)
                        NodeEditorRead.putString("audio_length", audio_length)
                        NodeEditorRead.putString("read", read)
                        NodeEditorRead.apply()
                        if (createdByUser != null && time != null) {
                            var currentUserBoolean = false
                            if (createdByUser == currentUserId) {
                                currentUserBoolean = true
                            } else {
                                if (active) {
                                    if (dataSnapshot.child("read").value.toString() == "Unread") {
                                        Chat_check_read()
                                    }
                                }
                            }
                            val newMessage = ChatObject(message, currentUserBoolean, UrlImage, time, chatId, url_send, chk2, matchId, audio, audio_length, currentUserId)
                            resultChat!!.add(newMessage)
                            mChatAdapter.notifyDataSetChanged()
                            ++chk
                            if (FetchId.size == 1) {
                                mRecyclerView.adapter = mChatAdapter
                                mRecyclerView.scrollToPosition(resultChat.size - 1)
                                pro!!.visibility = View.INVISIBLE
                            } else if (count_node_d < chk) {
                                mRecyclerView.smoothScrollToPosition(mRecyclerView.adapter!!.getItemCount() - 1)
                            }
                        }
                    }
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun status(Status_User: String?, current: String?) {
        val jjj = MainActivity()
        jjj.status(Status_User!!, current!!)
    }

    override fun onResume() {
        super.onResume()
        status("online", currentUserId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 33 && resultCode == Activity.RESULT_OK) {
            dialog!!.show()
            val name = System.currentTimeMillis().toString()
            val filepath = FirebaseStorage.getInstance().reference.child("SendImage").child(currentUserId.toString()).child(matchId.toString()).child("image$name")
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, uri_camera)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val baos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val dataurl = baos.toByteArray()
            val uploadTask = filepath.putBytes(dataurl)
            uploadTask.addOnFailureListener {
                Toast.makeText(this@ChatActivity, "Fail Upload", Toast.LENGTH_LONG).show()
                finish()
            }
            uploadTask.addOnSuccessListener {
                val filepath = FirebaseStorage.getInstance().reference.child("SendImage").child(currentUserId.toString()).child(matchId.toString()).child("image$name")
                filepath.downloadUrl.addOnSuccessListener { uri ->
                    val newMessageDb = mDatabaseChat!!.push()
                    val calendar = Calendar.getInstance()
                    val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
                    val time_user = currentTime.format(calendar.time)
                    val currentDate = SimpleDateFormat("dd/MM/yyyy")
                    val date_user = currentDate.format(calendar.time)
                    val newMessage = hashMapOf(
                            "createByUser" to currentUserId,
                            "time" to time_user,
                            "date" to date_user,
                            "text" to "photo$currentUserId",
                            "read" to "Unread",
                            "image" to uri.toString())
                    newMessageDb.setValue(newMessage)
                    dialog!!.dismiss()
                }.addOnFailureListener { }
            }
        }
        if (requestCode == 23 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            dialog!!.show()
            val name = System.currentTimeMillis().toString()
            file_uri = data.data
            val filepath = FirebaseStorage.getInstance().reference.child("SendImage").child(currentUserId.toString()).child(matchId.toString()).child("image$name")
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, file_uri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val baos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val dataurl = baos.toByteArray()
            val uploadTask = filepath.putBytes(dataurl)
            uploadTask.addOnFailureListener {
                Toast.makeText(this@ChatActivity, "Fail Upload", Toast.LENGTH_LONG).show()
                finish()
            }
            uploadTask.addOnSuccessListener {
                val filepath = FirebaseStorage.getInstance().reference.child("SendImage").child(currentUserId.toString()).child(matchId.toString()).child("image$name")
                filepath.downloadUrl.addOnSuccessListener { uri ->
                    val newMessageDb = mDatabaseChat!!.push()
                    val calendar = Calendar.getInstance()
                    val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
                    val time_user = currentTime.format(calendar.time)
                    val currentDate = SimpleDateFormat("dd/MM/yyyy")
                    val date_user = currentDate.format(calendar.time)
                    val newMessage = hashMapOf(
                            "createByUser" to currentUserId,
                            "time" to time_user,
                            "date" to date_user,
                            "text" to "photo$currentUserId",
                            "read" to "Unread",
                            "image" to uri.toString())
                    newMessageDb.setValue(newMessage)
                    dialog!!.dismiss()
                }.addOnFailureListener { }
            }
        }
    }

    private fun startRecording() {
        recorder = MediaRecorder()
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder!!.setOutputFile(fileName)
        try {
            recorder!!.prepare()
        } catch (e: IOException) {
            Toast.makeText(this@ChatActivity, "Fail to recorded", Toast.LENGTH_SHORT).show()
        }
        recorder!!.start()
    }

    private fun stopRecording() {
        recorder!!.stop()
        recorder!!.release()
        recorder = null
        UpLoadAudio()
    }

    private fun UpLoadAudio() {
        val name = System.currentTimeMillis().toString()
        val ss2 = FirebaseStorage.getInstance().reference.child("Audio").child(currentUserId.toString()).child(matchId.toString()).child("audio$name.3gp")
        val uri = Uri.fromFile(File(fileName))
        ss2.putFile(uri).addOnSuccessListener {
            ss2.downloadUrl.addOnSuccessListener { uri ->
                Toast.makeText(this@ChatActivity, "Success", Toast.LENGTH_SHORT).show()
                LinearRecord.visibility = View.GONE
                val downloadUrl = uri
                val newMessageDb = mDatabaseChat!!.push()
                val calendar = Calendar.getInstance()
                val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
                val time_user = currentTime.format(calendar.time)
                val currentDate = SimpleDateFormat("dd/MM/yyyy")
                val date_user = currentDate.format(calendar.time)
                val newMessage = hashMapOf(
                        "createByUser" to currentUserId,
                        "time" to time_user,
                        "date" to date_user,
                        "audio_length" to time_count.toString(),
                        "audio" to downloadUrl.toString(),
                        "text" to "audio$currentUserId",
                        "read" to "Unread")
                newMessageDb.setValue(newMessage)
                time_count = 0
            }
        }
    }

    override fun onBackPressed() {
        mSendEditText!!.clearFocus()
        if (LinearRecord.visibility == View.VISIBLE) {
            LinearRecord.visibility = View.GONE
        } else {
            super.onBackPressed()
            if (intent.hasExtra("chat_na")) {
                Log.d("gghj", getcount().toString())
                if (c > 1) {
                    usersDb!!.child(matchId.toString()).child("connection").child("yep").child(currentUserId.toString()).setValue(true)
                    usersDb!!.child(currentUserId.toString()).child("connection").child("yep").child(matchId.toString()).setValue(true)
                    usersDb!!.child(currentUserId.toString()).child("connection").child("chatna").child(matchId.toString()).setValue(null)
                }
            }
            if (cHeck_back == 0) {
                finish()
                return
            }
            val intent = Intent(this@ChatActivity, Switch_pageActivity::class.java)
            intent.putExtra("first", 1)
            startActivity(intent)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        active = false
        status("offline", currentUserId)
    }

    private fun deletechild() {
        val datadelete = FirebaseDatabase.getInstance().reference.child("Users")
        val datachat = FirebaseDatabase.getInstance().reference
        datachat.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var ChatId = dataSnapshot.child("Users").child(currentUserId.toString()).child("connection").child("matches").child(matchId.toString()).child("ChatId").value.toString()
                if (dataSnapshot.child("Chat").hasChild(ChatId.toString())) {
                    datachat.child("Chat").child(ChatId.toString()).removeValue()
                }
                datadelete.child(currentUserId.toString()).child("connection").child("matches").child(matchId.toString()).removeValue()
                datadelete.child(currentUserId.toString()).child("connection").child("yep").child(matchId.toString()).removeValue()
                datadelete.child(matchId.toString()).child("connection").child("matches").child(currentUserId.toString()).removeValue()
                datadelete.child(matchId.toString()).child("connection").child("yep").child(currentUserId.toString()).removeValue()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun update(Child: String?) {
        usersDb!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val date_user: String
                val date_user_before: String
                var date_before = 0
                var date_after = 0
                val currentDate = SimpleDateFormat("dd/MM/yyyy")
                val calendar = Calendar.getInstance()
                date_user = currentDate.format(calendar.time)
                val after = date_user.substring(0, 2)
                date_after = Integer.valueOf(after)
                if (dataSnapshot.child(currentUserId.toString()).child("PutReportId").hasChild(matchId.toString())) {
                    date_user_before = dataSnapshot.child(currentUserId.toString()).child("PutReportId").child(matchId.toString()).child("date").value.toString()
                    val before = date_user_before.substring(0, 2)
                    date_before = Integer.valueOf(before)
                } else {
                    date_before = -1
                }
                if (date_before != date_after || date_before == -1) {
                    if (return_d == 0) {
                        return_d = -1
                        Alerter.create(this@ChatActivity)
                                .setTitle(getString(R.string.report_suc))
                                .setText(getString(R.string.report_suc2))
                                .setBackgroundColorInt(Color.parseColor("#7AFFCF"))
                                .setIcon(ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_check)!!)
                                .show()
                    }
                    if (!dataSnapshot.child(matchId.toString()).hasChild("Report")) {
                        val jj = hashMapOf(
                                Child to "1")
                        usersDb!!.child(matchId.toString()).child("Report").updateChildren(jj as Map<String, Any>)
                    } else if (dataSnapshot.child(matchId.toString()).hasChild("Report")) {
                        if (dataSnapshot.child(matchId.toString()).child("Report").hasChild(Child.toString())) {
                            val count_rep = Integer.valueOf(dataSnapshot.child(matchId.toString()).child("Report").child(Child.toString()).value.toString()) + 1
                            val input_count = count_rep.toString()
                            val jj = hashMapOf(
                                    Child to input_count)
                            usersDb!!.child(matchId.toString()).child("Report").updateChildren(jj as Map<String, Any>)
                        } else {
                            val jj = hashMapOf(
                                    Child to "1")
                            usersDb!!.child(matchId.toString()).child("Report").updateChildren(jj as Map<String, Any>)
                        }
                    }
                } else {
                    if (return_d == 0) {
                        return_d = -1
                        Alerter.create(this@ChatActivity)
                                .setTitle(getString(R.string.report_failed))
                                .setText(getString(R.string.report_fail))
                                .setBackgroundColorInt(Color.parseColor("#FF5050"))
                                .setIcon(ContextCompat.getDrawable(applicationContext, R.drawable.ic_do_not_disturb_black_24dp)!!)
                                .show()
                        val builder = AlertDialog.Builder(this@ChatActivity)
                        val view = LayoutInflater.from(this@ChatActivity).inflate(R.layout.alert_dialog, null)
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
                        icon.background = ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_warning_black_24dp)
                        builder.setView(view)
                        val mDialog = builder.show()
                        yes.setOnClickListener { mDialog.dismiss() }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun UpdateDate() {
        val date_user: String
        val currentDate = SimpleDateFormat("dd/MM/yyyy")
        val calendar = Calendar.getInstance()
        date_user = currentDate.format(calendar.time)
        val ff = hashMapOf(
                "date" to date_user)
        usersDb!!.child(currentUserId.toString()).child("PutReportId").child(matchId.toString()).updateChildren(ff as Map<String, Any>)
    }

    private val resultChat: ArrayList<ChatObject?>? = ArrayList()
    private fun getDataSetChat(): MutableList<ChatObject?>? {
        return resultChat
    }
}