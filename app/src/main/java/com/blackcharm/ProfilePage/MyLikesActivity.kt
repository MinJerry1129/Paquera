package com.blackcharm.ProfilePage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.blackcharm.R
import com.blackcharm.models.User
import com.blackcharm.views.MyLikesResultRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_my_likes.*

class MyLikesActivity: AppCompatActivity() {

    companion object {
        var currentUser: User? = null
        var messagelast: String? = null
        var counter: Int? = 1;
        var final: Int? = 0;

    }

    val adapter = GroupAdapter<ViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_likes)

        //**********Show back button on top toolbar**********

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.mylikes_toolbar);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        //**********End show back button on top toolbar**********



        var recyclerview_contacts_type = findViewById<RecyclerView>(R.id.recyclerview_mylikes)

        peoplelikes_likes_me.setOnClickListener {
            val intent =  Intent(this, PeopleLikeActivity::class.java)
            startActivity(intent)
        }


        recyclerview_contacts_type.adapter = adapter
        recyclerview_contacts_type.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        getMyLikes(this)


    }

    private fun getMyLikes(context: Context) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-wishlists/$fromId")

//        Log.d("Receivenotification", "listenForNotifications $fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Receive notification", "error")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                adapter.add(MyLikesResultRow(context, p0, adapter))

            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }



        })
    }



}