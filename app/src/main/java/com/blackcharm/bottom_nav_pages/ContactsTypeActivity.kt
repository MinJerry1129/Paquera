package com.blackcharm.bottom_nav_pages

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.blackcharm.R
import com.blackcharm.models.User
import com.blackcharm.views.ContactTypeResultRow
import com.blackcharm.views.ContactTypeResultRowFriendRequest
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class ContactsTypeActivity: AppCompatActivity() {

    companion object {
        var currentUser: User? = null
        var messagelast: String? = null
        var counter: Int? = 1;
        var final: Int? = 0;
        //var contactlist: MutableList<String?> = ArrayList()

    }


    var contacttypeactivitytitle: String? = null
    lateinit var contactstypetitle: TextView
    var datatable: String? = null
    val adapter = GroupAdapter<ViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_type)

        //**********Show back button on top toolbar**********

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.contacts_type_toolbar);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        //**********End show back button on top toolbar**********

        contacttypeactivitytitle = intent.extras.getString("contacttypetitle")
        datatable = intent.extras.getString("datatable")


        contactstypetitle = findViewById(R.id.contacts_type_title)
        contactstypetitle.text = contacttypeactivitytitle



        var recyclerview_contacts_type = findViewById<RecyclerView>(R.id.recyclerview_contacts_type)


        recyclerview_contacts_type.adapter = adapter
        recyclerview_contacts_type.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))



        if (contacttypeactivitytitle == "My friends") {
            getContactList(this)
        }
        if(contacttypeactivitytitle == "All contacts"){
            getContactAllList(this)
            Log.d("Contactpage2", "executing all contacts")
        }
        if(contacttypeactivitytitle == "Friend requests"){
            getFriendRequests(this)
            Log.d("Contactpage2", "executing all contacts")
        }
        if(contacttypeactivitytitle == "Requests sent"){
            getRequestsSent(this)
            Log.d("Contactpage2", "executing all contacts")
        }


    }

    private fun getContactList(context: Context) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/$datatable/$fromId")

//        Log.d("Receivenotification", "listenForNotifications $fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Receive notification", "error")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                adapter.add(ContactTypeResultRow(context, p0.key!!, adapter))

            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }



        })
    }

//    private fun getContactAllList(context: Context) {
//        val fromId = FirebaseAuth.getInstance().uid
//        val ref = FirebaseDatabase.getInstance().getReference("/$datatable/$fromId")
//        contactlist.clear()
//
//        //Log.d("Contactpage2", "All contact list function")
//
////        Log.d("Receivenotification", "listenForNotifications $fromId")
//
//        ref.addChildEventListener(object: ChildEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                Log.d("Receive notification", "error")
//            }
//
//            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//
//                contactlist.add(p0.key)
//
//                adapter.add(ContactTypeResultRow(context, p0, adapter))
//
//                //Log.d("Contactpage2", "Added to the adapter")
//
//                val ref1 = FirebaseDatabase.getInstance().getReference("/users-friends/"+p0.key)
//
//
//                //Log.d("Contactpage2", "Trying to fetch friends of friends")
//
//                ref1.addChildEventListener(object: ChildEventListener {
//                    override fun onCancelled(p0: DatabaseError) {
//                        Log.d("Receive notification", "error")
//                    }
//
//                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                        //Log.d("Contactpage2", "Fetching")
//
//                        if(p0.key != fromId) {
//                            if(p0.key !in contactlist){
//                                contactlist.add(p0.key)
//
//                                adapter.add(ContactTypeResultRow(context, p0, adapter))
//                        }
//                        }
//                    }
//                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//
//                    }
//
//                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//
//                    }
//
//                    override fun onChildRemoved(p0: DataSnapshot) {
//
//                    }
//
//                })
//
//            }
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//
//            }
//
//            override fun onChildRemoved(p0: DataSnapshot) {
//
//            }
//
//
//
//        })
//    }

    private fun getContactAllList(context: Context) {
        for(userFromFofArray in ContactsActivity.contactlist) {
            adapter.add(ContactTypeResultRow(context, userFromFofArray!!, adapter))
        }

    }

    private fun getFriendRequests(context: Context) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/$datatable/$fromId/received")


        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Receive notification", "error")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                adapter.add(ContactTypeResultRowFriendRequest(context, p0, adapter))

            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }



        })
    }

    private fun getRequestsSent(context: Context) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/$datatable/$fromId/sent-to")


        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Receive notification", "error")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                adapter.add(ContactTypeResultRow(context, p0.key!!, adapter))

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
