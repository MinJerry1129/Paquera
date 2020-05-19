package com.blackcharm.NotificationHandler

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.blackcharm.ProfilePage.AlienProfilePageActivity
import com.blackcharm.messages.ChatLogActivity
import com.blackcharm.messages.LatestMessagesActivity
import com.blackcharm.models.User

class NotificationMiddleActivity: AppCompatActivity() {
    var chatPartnerUser: User? = null
    var chatPartnerId: String? = null
    var method: String? = null
    var UserDeviceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("hello", "hello world")

        var bundle: Bundle? = getIntent().getExtras();

        if (bundle != null) {

            chatPartnerId = intent.getExtras().getString("uid")
            method = intent.getExtras().getString("method")

            Log.d("jhkjh", method)


            val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    chatPartnerUser = p0.getValue(User::class.java)
                    val intent1 = Intent(getApplicationContext(), LatestMessagesActivity::class.java)

                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    getApplicationContext().startActivity(intent1)

                    if(method == "chat") {
                        val intent = Intent(getApplicationContext(), ChatLogActivity::class.java)

                        intent.putExtra("USER_KEY", chatPartnerUser)
                        intent.putExtra("USER_UID", chatPartnerId)

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        getApplicationContext().startActivity(intent)
                    }
                    if(method == "profile") {

                        val intent = Intent(getApplicationContext(), AlienProfilePageActivity::class.java)
                        //intent.putExtra("USER_KEY", chatPartnerUser)
                        intent.putExtra("profileuid", chatPartnerId)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        getApplicationContext().startActivity(intent)
                    }
                    finish()

                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })





        }


    }
    override fun onResume() {
        super.onResume()
    }

}