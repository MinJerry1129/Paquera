package com.blackcharm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.blackcharm.messages.LatestMessagesActivity
import com.blackcharm.registerlogin.TermsScreen
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*


class SplashActivity : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            veryfyUserLoggedIn()
        }
    }

    private fun veryfyUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent =  Intent(this, InfoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        else {
            var mReference = FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().uid);
            mReference.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var sdf = SimpleDateFormat("yyyy/MM/dd")
                    val currentDate = sdf.format(Date());
                    println("currnet date " + currentDate)

                    var membershipdate : String = p0.child("membershipdate").getValue().toString();
                    println("membership date " + membershipdate)
                    if(currentDate > membershipdate){
                        Common.getInstance().membership_status = "no"
                    }else{
                        Common.getInstance().membership_status = "yes"
                    }
                    val intent = Intent(applicationContext, LatestMessagesActivity::class.java)
                    intent.putExtra("isFirstVisit", false)
                    startActivity(intent)
                    finish()
                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        System.out.println("----------------------------");
        printKeyHash()

        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }

    private fun printKeyHash() {
        // Add code to print out the key hash
        try {
            var stringVal: String = ""
            val info = packageManager.getPackageInfo(
                "com.myfriendsroomlimited",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))

                stringVal += "KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT) + " ### "
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("KeyHash:", e.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.d("KeyHash:", e.toString())
        }

    }

}