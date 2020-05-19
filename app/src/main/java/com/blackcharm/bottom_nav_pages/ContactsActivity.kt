package com.blackcharm.bottom_nav_pages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.blackcharm.R
import com.blackcharm.SearchListener.IFirebaseLoadDone
import com.blackcharm.SearchModel.SearchModel

class ContactsActivity: AppCompatActivity(), IFirebaseLoadDone {
    override fun onSearchResaultSuccess(profileList: List<SearchModel>, dataid: List<String>) {

    }

    override fun onSearchResaultFailed(message: String) {

    }

    override fun OnSearchPermittedUserSuccess(profileList:List<String>, flag :Int) {
        //Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show()
    }

    lateinit var friendsonly: Button
    lateinit var allcontacts: Button
    lateinit var friendsrequests: Button
    lateinit var requestssent: Button
    lateinit var blacklist: Button
    lateinit var whatsappbtn: Button
    lateinit var facebookbtn: Button
    lateinit var phonebtn: Button

    lateinit var friendsonlycounter: TextView
    lateinit var allcontactscounter: TextView
    lateinit var friendsrequestscounter: TextView
    lateinit var requestssentcounter: TextView
    lateinit var blacklistcounter: TextView
    var countall: Long = 0
    var countpreall: Long = 0
    var countfinalall: Long = 0

    var GOOGLE_PLAY_STORE_URI: String = "http://play.google.com/store/apps/details?id=com.myfriendsroomlimited";
    var FACEBOOK_MESSENGER_PACKAGE: String = "com.facebook.orca";



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        //**********Show back button on top toolbar**********

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.contacts_toolbar);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        //**********End show back button on top toolbar**********


        friendsonly = findViewById(R.id.contacts_friendsonly_button)
        allcontacts = findViewById(R.id.contacts_allcontacts_button)
        friendsrequests = findViewById(R.id.contacts_friendsrequest_button)
        requestssent = findViewById(R.id.contacts_requestsent_button)
        blacklist = findViewById(R.id.contacts_blacklist_button)

        whatsappbtn = findViewById(R.id.contacts_whatsapp_button)
        facebookbtn = findViewById(R.id.contacts_facebook_button)
        phonebtn = findViewById(R.id.contacts_phone_button)

        friendsonlycounter = findViewById(R.id.contacts_friendsonly_counter)
        allcontactscounter = findViewById(R.id.contacts_allcontacts_counter)
        friendsrequestscounter = findViewById(R.id.contacts_friendsrequest_counter)
        requestssentcounter = findViewById(R.id.contacts_requestsent_counter)
        blacklistcounter = findViewById(R.id.contacts_blacklist_counter)

        friendsonly.setOnClickListener { view ->
            val intent = Intent(view.context, ContactsTypeActivity::class.java)
            //val row = item as LatestMessageRow
            intent.putExtra("contacttypetitle", "My friends")
            intent.putExtra("datatable", "users-friends")

            startActivity(intent)

        }
        allcontacts.setOnClickListener { view ->
            val intent = Intent(view.context, ContactsTypeActivity::class.java)
            //val row = item as LatestMessageRow
            intent.putExtra("contacttypetitle", "All contacts")
            intent.putExtra("datatable", "users-friends")

            startActivity(intent)

        }
        friendsrequests.setOnClickListener { view ->
            val intent = Intent(view.context, ContactsTypeActivity::class.java)
            //val row = item as LatestMessageRow
            intent.putExtra("contacttypetitle", "Friend requests")
            intent.putExtra("datatable", "users-friends-proposals")

            startActivity(intent)

        }
        requestssent.setOnClickListener { view ->
            val intent = Intent(view.context, ContactsTypeActivity::class.java)
            //val row = item as LatestMessageRow
            intent.putExtra("contacttypetitle", "Requests sent")
            intent.putExtra("datatable", "users-friends-proposals")

            startActivity(intent)

        }
        blacklist.setOnClickListener { view ->
            val intent = Intent(view.context, ContactsTypeActivity::class.java)
            //val row = item as LatestMessageRow
            intent.putExtra("contacttypetitle", "Blacklist")

            startActivity(intent)

        }

        whatsappbtn.setOnClickListener {
            sendWhatsapp("Hey! Be my friend on this great new travel app.\n" +
                    "IOS: https://itunes.apple.com/app/id1439459277\n" +
                    "Android: http://play.google.com/store/apps/details?id=com.myfriendsroomlimited")
        }
        facebookbtn.setOnClickListener {
            shareOnFacebookMessenger("Hey! Be my friend on this great new travel app. " +
                    "https://itunes.apple.com/app/id1439459277\n" +
                    "Android: http://play.google.com/store/apps/details?id=com.myfriendsroomlimited")
        }
        phonebtn.setOnClickListener {
            shareOnSms("Hey! Be my friend on this great new travel app.\n" +
                    "IOS: https://itunes.apple.com/app/id1439459277\n" +
                    "Android: http://play.google.com/store/apps/details?id=com.myfriendsroomlimited")
        }



        fetchfriendsonly()
        fetchallcontacts()
        fetchFriendRequests()
        fetchRequestsSent()



    }
    private fun sendWhatsapp(message: String){
        var sendIntent: Intent = Intent()

        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.setType("text/plain")
        sendIntent.setPackage("com.whatsapp")
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent)
        }
        else {
            Toast.makeText(this, "Please install whatsapp app", Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun shareOnFacebookMessenger(message: String){

        var sendIntent: Intent = Intent()

        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.setType("text/plain")
        sendIntent.setPackage("com.facebook.orca");
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent)
        }
        else {
            Toast.makeText(this, "Please install facebook messenger.", Toast.LENGTH_SHORT)
                .show()
        }
//        FacebookSdk.sdkInitialize(this)
//         var content: ShareLinkContent = ShareLinkContent.Builder()
//            .setContentUrl(Uri.parse("https://itunes.apple.com/app/id1439459277"))
//            .build()
//
//            MessageDialog.show(this, content);


    }
    private fun shareOnSms(message: String){

//        var sendIntent: Intent = Intent()
//
//        sendIntent.setAction(Intent.ACTION_SEND)
//        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
//        sendIntent.setType("text/plain")
//        sendIntent.setPackage("com.facebook.orca");
//        if (sendIntent.resolveActivity(getPackageManager()) != null) {
//            startActivity(sendIntent)
//        }
//        else {
//            Toast.makeText(this, "Please install facebook messenger.", Toast.LENGTH_SHORT)
//                .show()
//        }


        var sendIntent: Intent = Intent(Intent.ACTION_VIEW)

        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body", message)

        startActivity(sendIntent);


    }


    private fun fetchfriendsonly() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-friends/$fromId")
        var count = 0
        val children: MutableList<String?> = ArrayList()

//        Log.d("Receivenotification", "listenForNotifications $fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Receive notification", "error")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                count += 1

                friendsonlycounter.text = count.toString()



            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }



        })

    }


    companion object {
        var contactlist: MutableList<String?> = ArrayList()

    }
    private fun fetchallcontacts() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-friends/$fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Receive notification", "error")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                contactlist.add(p0.key)
                countall +=1


                val ref1 = FirebaseDatabase.getInstance().getReference("/users-friends/"+p0.key)




                ref1.addChildEventListener(object: ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("Receive notification", "error")
                    }

                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                        if(p0.key != fromId) {


                            if(p0.key !in contactlist){
                                contactlist.add(p0.key)
                                countall +=1
                                allcontactscounter.text = countall.toString()

                            }


                        }


                    }
                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                    }

                    override fun onChildRemoved(p0: DataSnapshot) {

                    }
                })
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })

    }


    private fun fetchFriendRequests() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-friends-proposals/$fromId/received")
        var countrequests = 0

//        Log.d("Receivenotification", "listenForNotifications $fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Receive notification", "error")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                countrequests += 1

                friendsrequestscounter.text = countrequests.toString()



            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }



        })

    }
    private fun fetchRequestsSent() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-friends-proposals/$fromId/sent-to")
        var countsent = 0

//        Log.d("Receivenotification", "listenForNotifications $fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Receive notification", "error")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                countsent += 1

                requestssentcounter.text = countsent.toString()



            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }



        })

    }

    private fun finalfetchallcontacts(countpreall: Long, contactuid: String?) {

        countall = countpreall




    }

    override fun onResume() {
        super.onResume()
    }

}