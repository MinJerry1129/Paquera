package com.blackcharm.bottom_nav_pages

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.blackcharm.NotificationHandler.CustomJsonObjectRequestBasicAuth
import com.blackcharm.NotificationHandler.VolleySingleton
import com.blackcharm.R
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ContactPersonActivity: AppCompatActivity() {

    var alienProfileUser: String? = null
    lateinit var contactpersonpickfrom: TextView
    lateinit var contactpersonpickto: TextView
    lateinit var contactpersonname: TextView
    lateinit var contactpersonedittext: TextView

    lateinit var travelcheck: CheckBox
    lateinit var meetcheck: CheckBox
    lateinit var datecheck: CheckBox

    var travelcheckbool: Boolean = false
    var meetcheckbool: Boolean = false
    var datecheckbool: Boolean = false
    private var FCM_API: String = "https://fcm.googleapis.com/fcm/send";

    lateinit var contactpersonsend: Button
    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener

    var sentNotif: Boolean = false
    var inBlacklist: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_person)

        var cal: Calendar = Calendar.getInstance()

        contactpersonpickfrom = findViewById(R.id.contact_person_pick_from)
        contactpersonpickto = findViewById(R.id.contact_person_pick_to)
        contactpersonname = findViewById(R.id.contact_person_profile_name)

        travelcheck = findViewById(R.id.contact_person_register_travel_checkbox)
        meetcheck = findViewById(R.id.contact_person_register_meet_checkbox)
        datecheck = findViewById(R.id.contact_person_register_date_checkbox)
        contactpersonsend = findViewById(R.id.contact_person_go_button)


        contactpersonedittext = findViewById(R.id.contact_person_edittext)

        alienProfileUser = intent.extras.getString("profileuid")
        contactpersonname.text = intent.extras.getString("profilename")

        contactpersonsend.setOnClickListener {

            if(contactpersonedittext.getText().toString().equals("")){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Digite sua mensagem")
                builder.setNeutralButton("Sim"){dialog,which ->
                }

                val dialog: AlertDialog = builder.create()
                //dialog.getWindow().setLayout(600, 400);
                dialog.show()
            }
            else {
                contactToMember()
            }
        }


        contactpersonpickfrom.setOnClickListener {

            mDateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "MM.dd.yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                contactpersonpickfrom.text = sdf.format(cal.time)


            }

            val year: Int = cal.get(Calendar.YEAR)
            val month: Int = cal.get(Calendar.MONTH)
            val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)

            var dialog = DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, dayOfMonth)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
        contactpersonpickto.setOnClickListener {

            mDateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "MM.dd.yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                contactpersonpickto.text = sdf.format(cal.time)


            }

            val year: Int = cal.get(Calendar.YEAR)
            val month: Int = cal.get(Calendar.MONTH)
            val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)

            var dialog = DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, dayOfMonth)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }



    }
    override fun onResume() {
        super.onResume()


    }

    private fun contactToMember() {
        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        var ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                var homeLocation = p0.child("loc").value.toString()
                var name = p0.child("name").value.toString()
                var age = p0.child("age").value.toString()
                var occupation = p0.child("occupation").value.toString()
                var fromDevice = p0.child("fromDevice").toString()
                var startingInterested = "\nInterested in: "
                var messageTravelDate = ""
                var message_currentLocation = ""
                var messageTravelDateFrom = "-"
                var messageTravelDateTo = "-"
                var currentLocationVAlue = p0.child("currentLoc").value.toString()

                if (travelcheck.isChecked) {
                    startingInterested = startingInterested + "\n - travel accommodation"
                    travelcheckbool = true
                }
                if (meetcheck.isChecked) {
                    startingInterested = startingInterested + "\n - to meet up"
                    meetcheckbool = true
                }
                if (datecheck.isChecked) {
                    startingInterested = startingInterested + "\n - dating"
                    datecheckbool = true
                }

                if (contactpersonpickfrom.text != "pick date" || contactpersonpickto.text != "pick date") {
                    messageTravelDate = "\nTravel dates"
                }
                if (contactpersonpickfrom.text != "pick date") {
                    var fromText = contactpersonpickfrom.text
                    messageTravelDateFrom = fromText as String
                }
                if (contactpersonpickto.text != "pick date") {
                    var toText = contactpersonpickto.text
                    messageTravelDateTo = toText as String
                }
                if (currentLocationVAlue.toString() != null && currentLocationVAlue.toString() != "") {
                    message_currentLocation = "\nCurrent location: " + currentLocationVAlue
                }
                var message_homeLocation = "\nHome: " + homeLocation
                var nameAndAge = name + " " + age + "\n"
                var messageText = contactpersonedittext.text.toString()
                var messageTextFullPartOne = nameAndAge + occupation
                messageTextFullPartOne =
                    messageTextFullPartOne + message_currentLocation + message_homeLocation + messageTravelDate
                var messageTextFull: String
                if (startingInterested != "\nInterested in: ") {
                    messageTextFull = messageTextFullPartOne + startingInterested
                } else {
                    messageTextFull = messageTextFullPartOne
                }

                val properties: HashMap<String, Any> = hashMapOf(
                    "homeLoc" to homeLocation,
                    "name" to name,
                    "age" to age,
                    "occupation" to occupation,
                    "travelDateFrom" to messageTravelDateFrom,
                    "travelDateTo" to messageTravelDateTo,
                    "travelChecked" to travelcheckbool,
                    "meetChecked" to meetcheckbool,
                    "dateChecked" to datecheckbool,
                    "currentLoc" to currentLocationVAlue.toString()
                )

                sentNotif = false

                sendMessagesWithProperties(properties, name, sentNotif)
                sendSecondMessage(messageText, name)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun sendMessagesWithProperties(properties: HashMap<String, Any>, name: String, sentNotif: Boolean) {

        var ref = FirebaseDatabase.getInstance().getReference().child("messages").push()
        var toId = alienProfileUser
        var fromId = FirebaseAuth.getInstance().currentUser!!.uid
        var blacklistRef = FirebaseDatabase.getInstance().getReference().child("users-blacklists").child(toId!!).child(fromId)
        val builder = AlertDialog.Builder(this)

        blacklistRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var snapValueType = p0.value
                if (snapValueType != null) {
                    print("You are in blacklist of this user")
                    inBlacklist = true
                    //contactTEXTSContainerView.isHidden = true
                    builder.setTitle("Este usuário colocou você na lista negra")
                    builder.setNeutralButton("OK"){dialog,which ->
                    }
                    val dialog: AlertDialog = builder.create()
                    //dialog.getWindow().setLayout(600, 400);
                    dialog.show()
                }
                else{
                    val timestamp = System.currentTimeMillis()/1000
                    val values: HashMap<String, Any> = hashMapOf(
                        "toId" to toId.toString(),
                        "fromId" to fromId.toString(),
                        "timestamp" to timestamp.toLong()
                    )
                    val allvalues = HashMap<String, Any>()
                    allvalues.putAll(properties)
                    allvalues.putAll(values)
//                    properties.forEach {values[$0] = $1}
                    ref.updateChildren(allvalues).addOnCompleteListener {

                        contactpersonedittext.text = null
                        var userMessagesRef = FirebaseDatabase.getInstance().getReference().child("user-messages").child(fromId).child(toId!!)
                        var messageId = ref.key.toString()
                        val messageIdupdate: HashMap<String, Any> = hashMapOf(
                            messageId to 1
                        )
                        userMessagesRef.updateChildren(messageIdupdate)
                        var recipientUserMessagesRef = FirebaseDatabase.getInstance().getReference().child("user-messages").child(toId!!).child(fromId)
                        recipientUserMessagesRef.updateChildren(messageIdupdate)

                    }

                    if (sentNotif == true) {
                        fetchmessages(toId, properties, name)
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }


    private fun sendSecondMessage(messageText: String, name: String){
        val properties: HashMap<String, Any> = hashMapOf(
            "text" to messageText,
            "likeStatus" to false
        )
        sentNotif = true
        sendMessagesWithProperties(properties, name, sentNotif)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sent")
        builder.setNeutralButton("OK"){dialog,which ->
        }
        val dialog: AlertDialog = builder.create()
        //dialog.getWindow().setLayout(600, 400);
        dialog.show()

    }


    private fun fetchmessages(toId: String, properties: HashMap<String, Any>, fromName: String) {
        val ref = FirebaseDatabase.getInstance().getReference().child("users").child(toId)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var value = p0.value
                var UserDeviceId = p0.child("fromDevice").value.toString()
                sendNotificationToUser(UserDeviceId, properties, fromName)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun sendNotificationToUser(UserDeviceId: String, properties: HashMap<String, Any>, fromName: String) {




        var notification: JSONObject = JSONObject()
        var notifcationBody: JSONObject = JSONObject()
        var dataBody: JSONObject = JSONObject()
        var text = properties["text"]

        try {
            notifcationBody.put("title", fromName);
            notifcationBody.put("body", text);
            notifcationBody.put("click_action", "chat");


            dataBody.put("uid", FirebaseAuth.getInstance().uid);
            dataBody.put("method", "chat");

//            notification.put("to", "dhfW5Qx8c8Q:APA91bHgulT--XznQMebTJL_U4HVR7DwBGPkvNtB5spENf5wdRmy0uTcX40e-0PE8t3ISONZV16O-B4WIRdQhx7R6c2LK3hsLU_rXK0rOZ2vhBTOsu6KJjw3gP0NoiEL8cVLBZz5X5lg");
            notification.put("to", UserDeviceId);
            notification.put("notification", notifcationBody);
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
//                Log.i(LatestMessagesActivity.TAG, "onError: " + "Volley error: $it");
            })

//        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)


    }


}