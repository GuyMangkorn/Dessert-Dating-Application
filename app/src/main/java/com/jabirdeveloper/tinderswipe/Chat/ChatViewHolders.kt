package com.jabirdeveloper.tinderswipe.Chat

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jabirdeveloper.tinderswipe.ImageChat.ItemImageActivity
import com.jabirdeveloper.tinderswipe.R
import com.ldoublem.loadingviewlib.LVCircularCD
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ChatViewHolders(itemView: View, private val context: Context?) : RecyclerView.ViewHolder(itemView) {
    var mMessage: TextView
    var timeSend: TextView
    var mMatchId: TextView
    var mMatchIdReal: TextView
    var audioUrl: TextView
    var beginAudio: TextView
    var mContainer: LinearLayout
    var buttonAudio: Button
    var mchk: LinearLayout
    var mchk2: LinearLayout
    var mchk3: LinearLayout
    var imageOpposite: ImageView
    var mImage_sent: ImageView
    //var stop_Animate: ImageView? = null
    private var progressBarAudio: ProgressBar
    private var play:Boolean = false
    //var mRecycler: RecyclerView?
    var mChk: TextView
    var mChk_2: TextView
    //private val loading: AVLoadingIndicatorView? = null
    private val myClipboard: ClipboardManager?
    private var myClip: ClipData? = null
    private var mediaPlayer: MediaPlayer? = null
    var cd: LVCircularCD = itemView.findViewById(R.id.play_pause_animate)
    private var length = 0
    private var totalLength = 0
    private var countDownTimer: CountDownTimer? = null
    //private var card: CardView = itemView.findViewById(R.id.card)
    private var check = true
    private lateinit var alertDialog: AlertDialog


    init {
        cd.setViewColor(Color.parseColor("#FFF064"))
        progressBarAudio = itemView.findViewById(R.id.progressBar_playAudio)
        buttonAudio = itemView.findViewById(R.id.play_audio)
        audioUrl = itemView.findViewById(R.id.audio_url)
        mChk = itemView.findViewById(R.id.chk_image)
        mMatchId = itemView.findViewById(R.id.match_id)
        mImage_sent = itemView.findViewById(R.id.img_sent)
        //mRecycler = itemView.findViewById<View?>(R.id.recyclerView_2) as RecyclerView
        mchk2 = itemView.findViewById(R.id.lili)
        beginAudio = itemView.findViewById(R.id.begin_audio)
        mchk3 = itemView.findViewById(R.id.li)
        mchk = itemView.findViewById(R.id.lilili)
        timeSend = itemView.findViewById(R.id.time_chat_user)
        mMessage = itemView.findViewById(R.id.chatmessage)
        mContainer = itemView.findViewById(R.id.container)
        mMatchIdReal = itemView.findViewById(R.id.match_id_real_image)
        imageOpposite = itemView.findViewById(R.id.image_holder)
        mChk_2 = itemView.findViewById(R.id.chk_image_2)
        val item = context!!.resources.getStringArray(R.array.chat_item)
        val itemImage = context.resources.getStringArray(R.array.chat_item_image)
        val builder = AlertDialog.Builder(context)
        myClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        buttonAudio.setOnClickListener{
            if(!play){
                play =true
                cd.startAnim()
                if (check) {
                    val minute = Integer.valueOf(mMessage.text.toString().substring(0, 2))
                    val second = Integer.valueOf(mMessage.text.toString().substring(3, 5))
                    totalLength = second + minute * 60
                    check = false
                }
                buttonAudio.visibility = View.GONE
                progressBarAudio.visibility = View.VISIBLE
                val minute = Integer.valueOf(mMessage.text.toString().substring(0, 2))
                val second = Integer.valueOf(mMessage.text.toString().substring(3, 5))
                val minuteSub = Integer.valueOf(beginAudio.text.toString().substring(0, 2))
                val secondSub = Integer.valueOf(beginAudio.text.toString().substring(3, 5))
                val counterSub = secondSub + minuteSub * 60
                val counter = second + minute * 60 - counterSub
                mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer!!.setDataSource(audioUrl.text.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    mediaPlayer!!.prepare()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (length == 0) {
                    mediaPlayer!!.start()
                } else {
                    mediaPlayer!!.seekTo(length)
                    mediaPlayer!!.start()
                }
                if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1)!!.constantState) {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1_selected)
                } else {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2_selected)
                }
                progressBarAudio.visibility = View.GONE
                buttonAudio.visibility = View.VISIBLE
                buttonAudio.background = ContextCompat.getDrawable(context, R.drawable.ic_pause_circle_outline_black_24dp)
                countDownTimer = object : CountDownTimer(((counter + 1) * 1000).toLong(), 1000) {
                    var total = counter
                    override fun onTick(millisUntilFinished: Long) {
                        val aaa = millisUntilFinished.toInt()
                        val allSecond = total - aaa / 1000
                        if (allSecond < 60) {
                            val second = String.format("%02d", allSecond + secondSub)
                            beginAudio.text = ("00:$second")
                        } else {
                            val checkMinute = allSecond / 60
                            val checkSecond = allSecond % 60
                            val secondS = String.format("%02d", checkSecond + minuteSub)
                            val minute = String.format("%02d", checkMinute + secondSub)
                            beginAudio.text = ("$minute:$secondS")
                        }
                    }

                    override fun onFinish() {
                        length = 0
                        cd.stopAnim()
                        buttonAudio.background = ContextCompat.getDrawable(context, R.drawable.ic_play_circle_outline_black_24dp)
                        beginAudio.text = ("00:00")
                        if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1_selected)!!.constantState) {
                            mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1)
                        } else {
                            mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
                        }
                    }
                }.start()

            }else{
                play =false
                Log.d("audioBackground","$play")
                cd.stopAnim()
                countDownTimer!!.cancel()
                buttonAudio.background = ContextCompat.getDrawable(context, R.drawable.ic_play_circle_outline_black_24dp)
                mediaPlayer!!.stop()
                length = mediaPlayer!!.currentPosition
                Log.d("audioBackground","$length")
                if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1_selected)!!.constantState) {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1)
                } else {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
                }
            }
            /*if (buttonAudio.background.constantState === ContextCompat.getDrawable(context, R.drawable.ic_play_circle_outline_black_24dp)!!.constantState) {
                cd!!.startAnim()
                if (check) {
                    val minute = Integer.valueOf(mMessage.text.toString().substring(0, 2))
                    val second = Integer.valueOf(mMessage.text.toString().substring(3, 5))
                    totalLength = second + minute * 60
                    check = false
                }
                buttonAudio.visibility = View.GONE
                progressBarAudio.visibility = View.VISIBLE
                val minute = Integer.valueOf(mMessage.text.toString().substring(0, 2))
                val second = Integer.valueOf(mMessage.text.toString().substring(3, 5))
                val minute_sub = Integer.valueOf(beginAudio.text.toString().substring(0, 2))
                val second_sub = Integer.valueOf(beginAudio.text.toString().substring(3, 5))
                val counter_sub = second_sub + minute_sub * 60
                val counter = second + minute * 60 - counter_sub
                mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer!!.setDataSource(audioUrl.text.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    mediaPlayer!!.prepare()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (length == 0) {
                    mediaPlayer!!.start()
                } else {
                    mediaPlayer!!.seekTo(length)
                    mediaPlayer!!.start()
                }
                if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1)!!.constantState) {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1_selected)
                } else {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2_selected)
                }
                progressBarAudio.visibility = View.GONE
                buttonAudio.visibility = View.VISIBLE
                buttonAudio.background = ContextCompat.getDrawable(context, R.drawable.ic_pause_circle_outline_black_24dp)
                countDownTimer = object : CountDownTimer(((counter + 1) * 1000).toLong(), 1000) {
                    var total = counter
                    override fun onTick(millisUntilFinished: Long) {
                        val aaa = millisUntilFinished.toInt()
                        val all_second = total - aaa / 1000
                        if (all_second < 60) {
                            val second = String.format("%02d", all_second + second_sub)
                            beginAudio.text = ("00:$second")
                        } else {
                            val check_minute = all_second / 60
                            val check_second = all_second % 60
                            val second_s = String.format("%02d", check_second + minute_sub)
                            val minute = String.format("%02d", check_minute + second_sub)
                            beginAudio.text = ("$minute:$second_s")
                        }
                    }

                    override fun onFinish() {
                        length = 0
                        cd!!.stopAnim()
                        buttonAudio.background = ContextCompat.getDrawable(context, R.drawable.ic_play_circle_outline_black_24dp)
                        beginAudio.text = ("00:00")
                        if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1_selected)!!.constantState) {
                            mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1)
                        } else {
                            mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
                        }
                    }
                }.start()
            } else {
                cd!!.stopAnim()
                countDownTimer!!.cancel()
                buttonAudio.background = ContextCompat.getDrawable(context, R.drawable.ic_play_circle_outline_black_24dp)
                mediaPlayer!!.stop()
                length = mediaPlayer!!.currentPosition
                if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1_selected)!!.constantState) {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1)
                } else {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
                }
            }*/
        }
        mchk2.setOnLongClickListener(OnLongClickListener {
            if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_2)!!.constantState) {
                mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2_selected)
                builder.setItems(item) { _, which ->
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
                    if (item[which] == "คัดลอก") {
                        val text: String = mMessage.text.toString()
                        myClip = ClipData.newPlainText("text", text)
                        myClipboard.setPrimaryClip(myClip!!)
                    }
                }
                builder.setOnDismissListener { mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2) }
                alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window!!.setLayout(800, 400)
            } else {
                mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1_selected)
                builder.setItems(item) { _, which ->
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1)
                    if (item[which] == "คัดลอก") {
                        val text: String = mMessage.text.toString()
                        myClip = ClipData.newPlainText("text", text)
                        myClipboard.setPrimaryClip(myClip!!)
                    }
                }
                builder.setOnDismissListener { mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1) }
                alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window!!.setLayout(800, 390)
            }
            true
        })
        mImage_sent.setOnClickListener{
            val intent = Intent(context, ItemImageActivity::class.java)
            intent.putExtra("matchIdReal", mMatchIdReal.text.toString())
            intent.putExtra("matchId", mMatchId.text.toString())
            intent.putExtra("ChkImage", mChk.text.toString())
            intent.putExtra("ChkImage2", mChk_2.text.toString())
            context.startActivity(intent)
        }
        mImage_sent.setOnLongClickListener(OnLongClickListener {
            if (mchk3.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1_photo)!!.constantState) {
                mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_1_photo_selected)
                builder.setItems(itemImage) { _, which ->
                    if (itemImage[which] == "ดาวน์โหลดภาพ") {
                        ActivityCompat.requestPermissions((context as Activity?)!!, arrayOf<String?>(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                        mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_1_photo)
                        val bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.maicar)
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "test.png")
                        val out: FileOutputStream
                        try {
                            out = FileOutputStream(file)
                            Toast.makeText(context, "chk", Toast.LENGTH_SHORT).show()
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                            out.flush()
                            out.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                builder.setOnDismissListener { mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_1_photo) }
                alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window!!.setLayout(800, 245)
            } else {
                mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_2_photo_selected)
                builder.setItems(itemImage) { _, _ -> mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_2_photo) }
                builder.setOnDismissListener { mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_2_photo) }
                alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window!!.setLayout(800, 245)
            }
            true
        })
    }
}