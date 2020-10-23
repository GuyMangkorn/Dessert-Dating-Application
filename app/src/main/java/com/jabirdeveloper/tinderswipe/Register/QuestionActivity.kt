package com.jabirdeveloper.tinderswipe.Register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.jabirdeveloper.tinderswipe.Functions.ChangLanguage
import com.jabirdeveloper.tinderswipe.QAStore.QAActivityAdapter
import com.jabirdeveloper.tinderswipe.QAStore.QAObject
import com.jabirdeveloper.tinderswipe.R

class QuestionActivity : AppCompatActivity() {
    private var x: Double = 0.0
    private var y: Double = 0.0
    private lateinit var type: String
    private lateinit var pager: ViewPager2
    private var email: String? = null
    private var pass: String? = null
    private var name: String? = null
    private lateinit var progressBar: ProgressBar
    private var sex: String? = null
    private var age: Int = 18
    private lateinit var toolbar: Toolbar
    private lateinit var intent1: Intent
    private var functions = Firebase.functions
    private var language:ChangLanguage = ChangLanguage(this)
    private val localizationDelegate = LocalizationActivityDelegate(this)
    private var arrSetQuestion:ArrayList<QAObject> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        toolbar = findViewById(R.id.my_tools)
        progressBar = findViewById(R.id.progressQuestion)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.registered)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        loadQuestion()
        pager = findViewById(R.id.viewPagerQuestion)
        language.setLanguage()
        intent.apply {
            x = getDoubleExtra("X", x)
            y = getDoubleExtra("Y", y)
            type = getStringExtra("Type")
            email = getStringExtra("email")
            pass = getStringExtra("password")
            name = getStringExtra("Name")
            sex = getStringExtra("Sex")
            age = getIntExtra("Age", age)
        }
    }
    private fun loadQuestion(): Task<HttpsCallableResult> {
        val data = hashMapOf(
                "type" to "RegisterQuestion",
                "language" to localizationDelegate.getLanguage(this).toLanguageTag()
        )
        return functions
                .getHttpsCallable("addQuestions")
                .call(data)
                .addOnSuccessListener {
                    val data:Map<*,*> = it.data as Map<*, *>
                    val questions:Map<*,*> = data["questions"] as Map<*, *>
                    for (entry in questions.keys){
                        val questionId = entry.toString()
                        val questionSet = questions[questionId] as Map<*,*>
                        val arr:ArrayList<String> = ArrayList()
                        arr.add(questionSet["0"].toString())
                        arr.add(questionSet["1"].toString())
                        val ob = QAObject(questionId,questionSet["question"].toString(),arr)
                        arrSetQuestion.add(ob)
                        Log.d("tagRegisterQuestion", questionSet["question"].toString())
                    }
                    intent1 = Intent(this@QuestionActivity, Regis_target_Acivity::class.java)
                    intent1.apply {
                        putExtra("Sex", intent.getStringExtra("Sex"))
                        putExtra("Type", intent.getStringExtra("Type"))
                        putExtra("X", intent.getDoubleExtra("X", 0.0))
                        putExtra("Y", intent.getDoubleExtra("Y", 0.0))
                        putExtra("Name", intent.getStringExtra("Name"))
                        putExtra("Age", intent.getStringExtra("Age"))
                        putExtra("email", intent.getStringExtra("email"))
                        putExtra("password", intent.getStringExtra("password"))
                        putExtra("Birth", intent.getLongExtra("Birth", 0))
                    }
                    val pagerAdapter = QAActivityAdapter(this,arrSetQuestion,pager,intent1)
                    pager.offscreenPageLimit = arrSetQuestion.size
                    pager.isUserInputEnabled = false
                    pager.adapter = pagerAdapter
                    progressBar.visibility = View.GONE
                }
    }
}