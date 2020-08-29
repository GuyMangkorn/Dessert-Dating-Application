package com.jabirdeveloper.tinderswipe

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.hanks.htextview.fade.FadeTextView
import com.jabirdeveloper.tinderswipe.Register.PhoneActivity
import com.jabirdeveloper.tinderswipe.Register.Regis_name_Activity
import com.jabirdeveloper.tinderswipe.Register.RegistrationActivity
import java.util.*

class ChooseLoginRegistrationActivity : AppCompatActivity() {
    private lateinit var mLogin: Button
    private lateinit var mRegister: Button
    private lateinit var test: Button
    private lateinit var nameFace: TextView
    private lateinit var mCallbackManager: CallbackManager
    private lateinit var firebaseAuthStateListener: AuthStateListener
    private lateinit var mAuth: FirebaseAuth
    private lateinit var usersDb: DatabaseReference
    private lateinit var textView: FadeTextView
    private lateinit var loginButton: LoginButton
    private lateinit var googleSignInClientg: GoogleSignInClient
    private val RC_SIGN_IN = 0
    private lateinit var thai: TextView
    private lateinit var eng: TextView
    private lateinit var face: LinearLayout
    private lateinit var google: LinearLayout
    private lateinit var mPhone: LinearLayout
    private lateinit var dialog: Dialog
    private lateinit var dialog2: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocal()
        setContentView(R.layout.activity_choose_login_registration)

        val constraintLayout: ConstraintLayout = findViewById(R.id.gra)
        val animationDrawable: AnimationDrawable = constraintLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(10)
        animationDrawable.setExitFadeDuration(3000)
        animationDrawable.start()
        mAuth = FirebaseAuth.getInstance()
        thai = findViewById(R.id.thai_lang)
        eng = findViewById(R.id.eng_lang)
        mLogin = findViewById(R.id.button3)
        mRegister = findViewById(R.id.button4)
        mPhone = findViewById(R.id.button7)
        face = findViewById(R.id.face)
        google = findViewById(R.id.google)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.progress_dialog, null)
        dialog = Dialog(this@ChooseLoginRegistrationActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        dialog.setCancelable(false)
        val width = (resources.displayMetrics.widthPixels * 0.80).toInt()
        dialog.window?.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val langure = preferences.getString("My_Lang", "")
        if (langure == "th") {
            thai.setTextColor(ContextCompat.getColor(applicationContext, R.color.cpb_white))
            eng.setTextColor(ContextCompat.getColor(applicationContext, R.color.white_tran))
        } else {
            thai.setTextColor(ContextCompat.getColor(applicationContext, R.color.white_tran))
            eng.setTextColor(ContextCompat.getColor(applicationContext, R.color.cpb_white))
        }
        firebaseAuthStateListener = AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userdb = FirebaseDatabase.getInstance().reference
                userdb.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        when {
                            dataSnapshot.child("BlackList").hasChild(user.uid) -> {
                                dialog.dismiss()

                                mAuth.signOut()
                                val intent = Intent(this@ChooseLoginRegistrationActivity, BandUser::class.java)
                                startActivity(intent)
                            }
                            dataSnapshot.child("Users").child(user.uid).hasChild("sex") -> {
                                dialog.dismiss()
                                val intent = Intent(this@ChooseLoginRegistrationActivity, Switch_pageActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("first", "0")
                                startActivity(intent)
                                finish()
                                return
                            }
                            else -> {
                                dialog.dismiss()
                                val intent = Intent(this@ChooseLoginRegistrationActivity, Regis_name_Activity::class.java)
                                intent.putExtra("Type", "face")
                                startActivity(intent)
                                return
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
        mCallbackManager = CallbackManager.Factory.create()
        face.setOnClickListener(View.OnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this@ChooseLoginRegistrationActivity, Arrays.asList("email"))
            LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    handleFacebookToken(loginResult?.accessToken)
                    //Toast.makeText(getApplicationContext(),"ไปต่อ", Toast.LENGTH_SHORT).show();
                }

                override fun onCancel() {}
                override fun onError(exception: FacebookException?) {
                    Log.d("TAG", "facebook:onError", exception)
                }
            })
        })
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClientg = GoogleSignIn.getClient(this, gso)
        google.setOnClickListener(View.OnClickListener { signIn() })
        mLogin.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ChooseLoginRegistrationActivity, LoginActivity::class.java)
            startActivity(intent)
            return@OnClickListener
        })
        mRegister.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ChooseLoginRegistrationActivity, RegistrationActivity::class.java)
            startActivity(intent)
            return@OnClickListener
        })
        mPhone.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ChooseLoginRegistrationActivity, PhoneActivity::class.java)
            startActivity(intent)
            return@OnClickListener
        })
        thai.setOnClickListener(View.OnClickListener {
            setLocal("th")
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })
        eng.setOnClickListener(View.OnClickListener {
            setLocal("en")
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })
    }

    private fun signIn() {
        val signInIntent = googleSignInClientg.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleFacebookToken(token: AccessToken?) {
        dialog.show()
        val credential = FacebookAuthProvider.getCredential(token!!.token)
        mAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Log.d("suc","สำเร็จซะที");
                //Toast.makeText(getApplicationContext(),"สวยยย", Toast.LENGTH_SHORT).show();
            } else { //Log.d("suc","vbspy';t"); Toast.makeText(getApplicationContext(),"ชิบหายยยยยยยย", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    dialog.show()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success")
                        //FirebaseUser user = mAuth.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d("TAG", "signInWithCredential:failure", task.exception)
                    }

                    // ...
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("TAG", "firebaseAuthWithGoogle:" + account?.id)
                firebaseAuthWithGoogle(account?.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d("TAG", "Google sign in failed", e)
                // ...
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
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
        Log.d("My2", langure)
        setLocal(langure)
    }
}