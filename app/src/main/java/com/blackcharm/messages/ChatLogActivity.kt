package com.blackcharm.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.blackcharm.NotificationHandler.CustomJsonObjectRequestBasicAuth
import com.blackcharm.NotificationHandler.VolleySingleton
import com.blackcharm.ProfilePage.AlienProfilePageActivity
import com.blackcharm.ProfilePage.MembershipActivity
import com.blackcharm.R
import com.blackcharm.bottom_nav_pages.LatestMessagesFragment
import com.blackcharm.models.ChatMessage
import com.blackcharm.models.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.comparisons.compareValues as compareValues1


class ChatLogActivity : AppCompatActivity() {
    val TAG = "MyMessage"

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null
    var toUserUid: String? = null
    var fromName: String? = null
    var UserDeviceId: String? = null

    var chatPartnerUser: User? = null
    val chatPartnerId: String? = null
    var membershipStatus: String? = null
    var chatCount : Int = 0
    private var eventlistenForMessages: ChildEventListener? = null
    lateinit var listenForMessagesReference: DatabaseReference

    private var FCM_API: String = "https://fcm.googleapis.com/fcm/send";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

//        recyclerview_chat_log.adapter = adapter
//
//        //**********Show back button on top toolbar**********
//
//        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.chatlog_toolbar);
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.title = ""
//
//        var mReference = FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().uid);
//
//        mReference.addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                var sdf = SimpleDateFormat("yyyy/MM/dd")
//                val currentDate = sdf.format(Date());
//                println("currnet date " + currentDate)
//
//                var membershipdate : String = p0.child("membershipdate").getValue().toString();
//                println("membership date " + membershipdate)
//                if(currentDate < membershipdate){
//                    membershipStatus = "no"
//                }else{
//                    membershipStatus = "yes"
//                }
//
//            }
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//
//
//        //**********End show back button on top toolbar**********
//
//
//
//
////        var bundle: Bundle? = getIntent().getExtras();
////        if (bundle != null) {
////
////            toUserUid = intent.getExtras().getString("uid")
////            Log.d("jhkjh", intent.getExtras().getString("uid"))
////
////
////
////            val ref = FirebaseDatabase.getInstance().getReference("/users/$toUserUid")
////            ref.addListenerForSingleValueEvent(object : ValueEventListener {
////                override fun onDataChange(p0: DataSnapshot) {
////                    chatPartnerUser = p0.getValue(User::class.java)
////                    toUser = chatPartnerUser
////                    UserDeviceId = chatPartnerUser?.fromDevice
////                    Log.d("jhkjh", ""+toUser?.name)
////
////                    chatlog_toolbar_title.text = toUser?.name
////
////                    listenForMessages()
////
////                    send_button_chat_log.setOnClickListener {
////                        performSendMessage()
////                    }
////                    fetchCurrentUser()
////
////                }
////
////                override fun onCancelled(p0: DatabaseError) {
////
////                }
////            })
////        }
////        else {
//            toUser = intent.getParcelableExtra<User>("USER_KEY")
//            toUserUid = intent.extras.getString("USER_UID")
//
//            Log.d(TAG,"Saved message: ${toUserUid}")
//
//
//
//        if(toUser == null) {
//            Log.d("checknull", "this is null")
//            val ref = FirebaseDatabase.getInstance().getReference("/users/$toUserUid")
//            ref.addListenerForSingleValueEvent(object: ValueEventListener {
//                override fun onDataChange(p0: DataSnapshot) {
//                    chatPartnerUser = p0.getValue(User::class.java)
//                    toUser = chatPartnerUser
//                    chatlog_toolbar_title.text = toUser?.name
//
//                }
//                override fun onCancelled(p0: DatabaseError) {
//
//                }
//            })
//
//            UserDeviceId = chatPartnerUser?.fromDevice
//
//        }
//        else {
//            chatlog_toolbar_title.text = toUser?.name
//            UserDeviceId = intent.getParcelableExtra<User>("USER_KEY").fromDevice
//        }
//
//            listenForMessages()
//            //updatecounter()
//
//            send_button_chat_log.setOnClickListener {
//                if(edittext_chat_log.getText().toString().equals("")){
//
//
//                }
//                else {
//                    if(membershipStatus == "yes"){
//                        Toast.makeText(this, "Atualize o plano de associação", Toast.LENGTH_LONG).show()
//                        performSendMessage()
//                    }else{
//                        Toast.makeText(this, "Atualize o plano de associação", Toast.LENGTH_LONG).show()
//                        val intent =  Intent(getBaseContext(), MembershipActivity::class.java)
//                        startActivity(intent)
//                    }
//                }
//            }
//
//
//            fetchCurrentUser()
//        }






        //supportActionBar?.title = toUser?.username

        //setupDummyData()


    }

    override fun onStart() {
        super.onStart()
        recyclerview_chat_log.adapter = adapter

        //**********Show back button on top toolbar**********

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.chatlog_toolbar);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        var mReference = FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().uid);

        mReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var sdf = SimpleDateFormat("yyyy/MM/dd")
                val currentDate = sdf.format(Date());
                println("currnet date " + currentDate)

                var membershipdate : String = p0.child("membershipdate").getValue().toString();
                println("membership date " + membershipdate)
                if(currentDate > membershipdate){
                    membershipStatus = "no"
                }else{
                    membershipStatus = "yes"
                }

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })


        //**********End show back button on top toolbar**********




//        var bundle: Bundle? = getIntent().getExtras();
//        if (bundle != null) {
//
//            toUserUid = intent.getExtras().getString("uid")
//            Log.d("jhkjh", intent.getExtras().getString("uid"))
//
//
//
//            val ref = FirebaseDatabase.getInstance().getReference("/users/$toUserUid")
//            ref.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(p0: DataSnapshot) {
//                    chatPartnerUser = p0.getValue(User::class.java)
//                    toUser = chatPartnerUser
//                    UserDeviceId = chatPartnerUser?.fromDevice
//                    Log.d("jhkjh", ""+toUser?.name)
//
//                    chatlog_toolbar_title.text = toUser?.name
//
//                    listenForMessages()
//
//                    send_button_chat_log.setOnClickListener {
//                        performSendMessage()
//                    }
//                    fetchCurrentUser()
//
//                }
//
//                override fun onCancelled(p0: DatabaseError) {
//
//                }
//            })
//        }
//        else {
        toUser = intent.getParcelableExtra<User>("USER_KEY")
        toUserUid = intent.extras.getString("USER_UID")

        Log.d(TAG,"Saved message: ${toUserUid}")



        if(toUser == null) {
            Log.d("checknull", "this is null")
            val ref = FirebaseDatabase.getInstance().getReference("/users/$toUserUid")
            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    chatPartnerUser = p0.getValue(User::class.java)
                    toUser = chatPartnerUser
                    chatlog_toolbar_title.text = toUser?.name

                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })

            UserDeviceId = chatPartnerUser?.fromDevice

        }
        else {
            chatlog_toolbar_title.text = toUser?.name
            UserDeviceId = intent.getParcelableExtra<User>("USER_KEY").fromDevice
        }

        listenForMessages()
        chatChildCount()
        //updatecounter()

        send_button_chat_log.setOnClickListener {
            if(edittext_chat_log.getText().toString().equals("")){

            }
            else {
                if(membershipStatus == "yes"){
                    performSendMessage()
                }else{
                    if(chatCount >1){
                        Toast.makeText(this, "Atualize o plano de associação", Toast.LENGTH_LONG).show()
                        val intent =  Intent(getBaseContext(), MembershipActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        performSendMessage()
                    }
                }
            }
        }


        fetchCurrentUser()
//        }

    }
    override fun onPause() {
        //this should be before super
        listenForMessagesReference.removeEventListener(eventlistenForMessages!!);
        super.onPause();


    }

    private fun chatChildCount(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUserUid

        var mchatref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId");
        mchatref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                chatCount = p0.childrenCount.toInt()
            }
        })
    }


    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUserUid
        listenForMessagesReference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        eventlistenForMessages = listenForMessagesReference.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {


                val messageId = p0.key
                val map: HashMap<String, Any> = hashMapOf(
                    messageId!! to 2
                )
                listenForMessagesReference.updateChildren(map)

                val ref1 = FirebaseDatabase.getInstance().getReference("/messages/$messageId")
             

                ref1.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val chatMessage = p0.getValue(ChatMessage::class.java)
                        val userchatname: String = p0.child("name").value.toString()

                        Log.d(TAG, "$chatMessage")

                        if (chatMessage != null) {

                             if(chatMessage?.fromId == FirebaseAuth.getInstance().uid){
                                val currentUser = LatestMessagesFragment.currentUser ?: return

                                 if(p0.child("name").value !=null){
                                     var travelChecked = p0.child("travelChecked").value as? Boolean
                                     var dateChecked = p0.child("dateChecked").value as? Boolean
                                     var meetChecked = p0.child("meetChecked").value as? Boolean
                                     var currentLoc = p0.child("currentLoc").value.toString()
                                     var homeLoc = p0.child("homeLoc").value.toString()
                                     var travelDateTo = p0.child("travelDateTo").value.toString()
                                     var travelDateFrom = p0.child("travelDateFrom").value.toString()
                                     var occupation = p0.child("occupation").value.toString()
                                     var name = p0.child("name").value.toString()
                                     var age = p0.child("age").value.toString()
                                     var messagePropsToText = ""
                                     var nameAndAge = name + ", " + age
                                     messagePropsToText = nameAndAge + "\n"

                                     if (currentLoc != null) {
                                         if (currentLoc != "") {
                                             messagePropsToText = messagePropsToText + "Current location:\n " + currentLoc!! + "\n"
                                         }
                                     }
                                     if (homeLoc != "") {
                                         messagePropsToText = messagePropsToText + "Home location:\n " + homeLoc + "\n"
                                     }
                                     if (occupation != null) {
                                         if (occupation != "") {
                                             messagePropsToText = messagePropsToText + "Occupation:\n " + occupation!! + "\n"
                                         }
                                     }
                                     if (travelChecked == true || dateChecked == true || meetChecked == true) {
                                         messagePropsToText = messagePropsToText + "Interested in:\n"
                                         if (travelChecked == true) {
                                             messagePropsToText = messagePropsToText + "- travel accommodation\n"
                                         }
                                         if (meetChecked == true) {
                                             messagePropsToText = messagePropsToText + "- to meet up\n"
                                         }
                                         if (dateChecked == true) {
                                             messagePropsToText = messagePropsToText + "- dating\n"
                                         }
                                     }
                                     if (travelDateFrom != "-" || travelDateTo != "-") {
                                         messagePropsToText = messagePropsToText + "Travel dates:\n"
                                         if (travelDateFrom != "-") {
                                             messagePropsToText = messagePropsToText + "From: " + travelDateFrom!! + "\n"
                                         }
                                         if (travelDateTo != "-") {
                                             messagePropsToText = messagePropsToText + "To: " + travelDateTo!! + "\n"
                                         }

                                     }
                                     adapter.add(ChatFromItem(messagePropsToText, currentUser))

                                 }



                                    adapter.add(ChatFromItem(chatMessage.text, currentUser))




                            } else {
                                if(p0.child("name").value !=null){
                                    var travelChecked = p0.child("travelChecked").value as? Boolean
                                    var dateChecked = p0.child("dateChecked").value as? Boolean
                                    var meetChecked = p0.child("meetChecked").value as? Boolean
                                    var currentLoc = p0.child("currentLoc").value.toString()
                                    var homeLoc = p0.child("homeLoc").value.toString()
                                    var travelDateTo = p0.child("travelDateTo").value.toString()
                                    var travelDateFrom = p0.child("travelDateFrom").value.toString()
                                    var occupation = p0.child("occupation").value.toString()
                                    var name = p0.child("name").value.toString()
                                    var age = p0.child("age").value.toString()
                                    var messagePropsToText = ""
                                    var nameAndAge = name + ", " + age
                                    messagePropsToText = nameAndAge + "\n"

                                    if (currentLoc != null) {
                                        if (currentLoc != "") {
                                            messagePropsToText = messagePropsToText + "Current location:\n " + currentLoc!! + "\n"
                                        }
                                    }
                                    if (homeLoc != "") {
                                        messagePropsToText = messagePropsToText + "Home location:\n " + homeLoc + "\n"
                                    }
                                    if (occupation != null) {
                                        if (occupation != "") {
                                            messagePropsToText = messagePropsToText + "Occupation:\n " + occupation!! + "\n"
                                        }
                                    }
                                    if (travelChecked == true || dateChecked == true || meetChecked == true) {
                                        messagePropsToText = messagePropsToText + "Interested in:\n"
                                        if (travelChecked == true) {
                                            messagePropsToText = messagePropsToText + "- travel accommodation\n"
                                        }
                                        if (meetChecked == true) {
                                            messagePropsToText = messagePropsToText + "- to meet up\n"
                                        }
                                        if (dateChecked == true) {
                                            messagePropsToText = messagePropsToText + "- dating\n"
                                        }
                                    }
                                    if (travelDateFrom != "-" || travelDateTo != "-") {
                                        messagePropsToText = messagePropsToText + "Travel dates:\n"
                                        if (travelDateFrom != "-") {
                                            messagePropsToText = messagePropsToText + "From: " + travelDateFrom!! + "\n"
                                        }
                                        if (travelDateTo != "-") {
                                            messagePropsToText = messagePropsToText + "To: " + travelDateTo!! + "\n"
                                        }

                                    }
                                    adapter.add(ChatToItem(applicationContext, chatMessage?.fromId, messagePropsToText, toUser!!))

                                }
                                else{
                                    adapter.add(ChatToItem(applicationContext, chatMessage?.fromId, chatMessage.text, toUser!!))
                                }



                            }
                        }
                        recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)

                    }
                })



            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {


            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                fromName = p0.child("name").value.toString()

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun performSendMessage() {

        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        val toId = toUserUid



        if (fromId == null) return


        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val messageId = reference.key
        Log.d(TAG,"Saved message: ${messageId}")
        val databaseRef = FirebaseDatabase.getInstance().getReference()



        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId!!, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount -1)
            }

        val userMessagesRef = HashMap<String, Any>()
        val recipientUserMessagesRef = HashMap<String, Any>()

        userMessagesRef.put("/user-messages/$fromId/$toId/$messageId", 1)
        recipientUserMessagesRef.put("/user-messages/$toId/$fromId/$messageId", 1)
        databaseRef!!.updateChildren(userMessagesRef)
        databaseRef!!.updateChildren(recipientUserMessagesRef)
        sendNotificationToUser(text)

    }

    private fun sendNotificationToUser(text: String) {




        var notification: JSONObject = JSONObject()
        var notifcationBody: JSONObject = JSONObject()
        var dataBody: JSONObject = JSONObject()

        try {
//            notifcationBody.put("title", fromName);
//            notifcationBody.put("body", text);
//            notifcationBody.put("sound", "default");
//            notifcationBody.put("click_action", "chat");


            dataBody.put("uid", FirebaseAuth.getInstance().uid);
            dataBody.put("method", "chat");
            dataBody.put("fromName", fromName);
            dataBody.put("text", text);
            dataBody.put("sound", "default");

//            notification.put("to", "dhfW5Qx8c8Q:APA91bHgulT--XznQMebTJL_U4HVR7DwBGPkvNtB5spENf5wdRmy0uTcX40e-0PE8t3ISONZV16O-B4WIRdQhx7R6c2LK3hsLU_rXK0rOZ2vhBTOsu6KJjw3gP0NoiEL8cVLBZz5X5lg");
            notification.put("to", UserDeviceId);
        //    notification.put("notification", notifcationBody);
            notification.put("data", dataBody);
        } catch (e: JSONException) {
//            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(notification);


    }

    private fun sendNotification(notification: JSONObject) {
        var jsonObjectRequest: JsonObjectRequest = CustomJsonObjectRequestBasicAuth(
            Request.Method.POST, FCM_API, notification,
            Response.Listener { response ->
    System.err.println(response);
                //                override fun onResponse(response: JSONObject) {
////                    Log.i(TAG, "onResponse: " + response.toString());
//                    edtTitle.setText("");
//                    edtMessage.setText("");
//                }
                try {
//                    Log.i(LatestMessagesActivity.TAG, "onResponse: " + response.toString());
                }catch (e:Exception){
//                    Log.i(LatestMessagesActivity.TAG, "onError: " + e);
                }
            },
            Response.ErrorListener {
                //                override fun onErrorResponse(error: VolleyError) {
////                    Toast.makeText(this, "Request error", Toast.LENGTH_LONG).show();
////                    Log.i(TAG, "onErrorResponse: Didn't work");
//                }
                Log.i(TAG, "onErrorResponse: $it");
                System.err.println(it)
//                Log.i(LatestMessagesActivity.TAG, "onError: " + "Volley error: $it");
            })

//        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)


    }

}


class ChatFromItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        Glide.with(targetImageView).load(uri)
            .placeholder(R.drawable.progress_animation)

            .error(R.drawable.branco).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(var context: Context, val profileuid: String, val text: String, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Glide.with(targetImageView).load(uri)
            .placeholder(R.drawable.progress_animation)

            .error(R.drawable.branco).into(targetImageView)

        viewHolder.itemView.imageview_chat_to_row.setOnClickListener {
            val intent = Intent(context, AlienProfilePageActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("profileuid", profileuid)

            context.startActivity(intent)
        }

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}