package com.blackcharm.events

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.CalendarContract
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.format.DateFormat
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.blackcharm.NotificationHandler.CustomJsonObjectRequestBasicAuth
import com.blackcharm.NotificationHandler.VolleySingleton
import com.blackcharm.R
import com.blackcharm.messages.ChatLogActivity
import com.blackcharm.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_events_detail.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*

class EventsDetailActivity : AppCompatActivity() {

    var events_data : Events = Events();
    private var recyclerView: RecyclerView? = null
    private var mImages: EventsImage? = null
    var chatPartnerUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_detail)

        //**********Show back button on top toolbar**********

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.alien_profile_toolbar);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        //**********End show back button on top toolbar**********

        events_data = intent.getSerializableExtra("events_data") as Events

        Glide.with(this)
            .load(events_data.getImage().getUri())
            .into(events_view_image)
        Glide.with(this)
            .load(events_data.getImage().getUri())
            .into(events_view_image_box)
        events_view_toolbar_title.text = events_data.organise
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = events_data.getDatetime().toDouble().toLong()
        val date = DateFormat.format("EEEE dd LLLL yyyy, hh:mm", cal).toString()
        events_view_datetime.text = date
        events_view_title.text = events_data.title
        setTextWithSpan(events_view_organisation,"Organizados por",events_data.organise)
        setTextWithSpan(events_view_location,"Localização",events_data.location)
        events_view_time.text = events_data.time
        setTextWithSpan(events_view_howtofindus,"Como nos encontrar",events_data.howtofindus)
        events_view_link1.text = events_data.link1
        events_view_link2.text = events_data.link2
        events_view_description.text = events_data.description


        events_view_link1.setOnClickListener{
            try {
                val browserIntent: Intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("http://"+events_data.link1.replace(" ","")));
                startActivity(browserIntent);
            }
            catch (e:Exception){

            }
        }

        events_view_link2.setOnClickListener{
            try {
                val browserIntent: Intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("http://"+events_data.link2.replace(" ","")));
                startActivity(browserIntent);
            }
            catch (e:Exception){

            }
        }

        changeMoreInfoBtn.setOnClickListener{
            if(events_view_description.maxLines==2)
            {
                events_view_description.maxLines=100
                events_view_description.ellipsize = TextUtils.TruncateAt.MARQUEE
                changeMoreInfoBtn.text="Menos"
            }
            else
            {
                events_view_description.maxLines=2
                events_view_description.ellipsize = TextUtils.TruncateAt.END
                changeMoreInfoBtn.text="Mais"
            }
        }

        addtocalendar_btn.setOnClickListener {
            val beginCal :Calendar = Calendar.getInstance();

            beginCal.setTimeInMillis(events_data.getDatetime().toDouble().toLong());

            val intent : Intent = Intent(Intent.ACTION_INSERT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra(CalendarContract.Events.TITLE, events_data.organise);
            intent.putExtra(CalendarContract.Events.DESCRIPTION, events_data.title);
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "");
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginCal.getTimeInMillis());
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, beginCal.getTimeInMillis());
            intent.putExtra(CalendarContract.Events.ALL_DAY, 1);
            intent.putExtra(CalendarContract.Events.STATUS, 1);
            intent.putExtra(CalendarContract.Events.VISIBLE, 0);
            intent.putExtra(CalendarContract.Events.HAS_ALARM, 1);
            startActivity(intent);
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/"+events_data.userid)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)

            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
        bt_events_message.setOnClickListener {
                 val intent = Intent(this, ChatLogActivity::class.java)

            intent.putExtra("USER_KEY", chatPartnerUser)
            intent.putExtra("USER_UID", events_data.userid)

            startActivity(intent)
        }
        bt_events_like.setOnClickListener {
            addToWishlist()
        }

        bt_discover_share.setOnClickListener {
            var imageToShare: String = events_data.image.uri; //Image You wants to share


            Picasso.get().load(imageToShare).into(object : com.squareup.picasso.Target {
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                    var title: String = events_data.title //Title you wants to share

                    var shareIntent: Intent = Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                    shareIntent.setType("*/*");
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, events_data.description);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                    startActivity(
                        Intent.createChooser(
                            shareIntent,
                            "Select App to Share Text and Image"
                        )
                    );
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

            })
        }
        isInYourWishlist()
    }

    fun getLocalBitmapUri(bmp:Bitmap?) : Uri? {
        var builder : StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        var bmpUri : Uri? = null;
        try {
            var file : File =  File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            var out : FileOutputStream = FileOutputStream(file);
            bmp?.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch ( e : IOException) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    internal fun setTextWithSpan(textView: TextView, spanText: String, text: String) {
        val sb = SpannableStringBuilder("$spanText: $text")
        val start = 0
        val end = spanText.length+1
        val boldStyle = StyleSpan(Typeface.BOLD)
        sb.setSpan(boldStyle, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        textView.text = sb
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.alien_profile_menu, menu);
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        var id: Int = item!!.getItemId()


        return super.onOptionsItemSelected(item)
    }

    private fun isInYourWishlist(){
        var isInWishlist : Boolean = false
        val fromId = FirebaseAuth.getInstance().uid
        val alienProfileUserId = events_data.events_id
        val ref = FirebaseDatabase.getInstance().getReference("/users-events-likes/").child(fromId!!)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (child in p0.children) {
                    if (child.key == alienProfileUserId){
                        isInWishlist = true
                    }
                }
                if (isInWishlist == true) {
                    hidAddToWishlistButton()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun addToWishlist(){
        //checkIfYouAreBanned()
        val addItToWishlistByUid = events_data.events_id
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-events-likes/")
        val usersReference = ref.child(fromId!!)
        val addItToWishlist: HashMap<String, Any> = hashMapOf(
            addItToWishlistByUid!! to 1)
        usersReference.updateChildren(addItToWishlist)

        whriteToUserNotification("Likes Event " + events_data.title )
        fetchCurrentUser(addItToWishlistByUid, "Likes Event " + events_data.title ,"likeyou")
        // self.sentProposalNotification(toId: goToControllerByMemberUid!, fromId: uid, text: self.notifLike)
        hidAddToWishlistButton()
//        print("Add to wishlist")
//        inWishList = true
    }
    private fun hidAddToWishlistButton(){
        bt_events_like.setVisibility(View.GONE)
    }
    private fun sendNotification(notification: JSONObject) {

        var FCM_API: String = "https://fcm.googleapis.com/fcm/send";
        var jsonObjectRequest: JsonObjectRequest = CustomJsonObjectRequestBasicAuth(
            Request.Method.POST, FCM_API, notification,
            Response.Listener { response ->

                try {
//                    Log.i(LatestMessagesActivity.TAG, "onResponse: " + response.toString());
                }catch (e:Exception){
//                    Log.i(LatestMessagesActivity.TAG, "onError: " + e);
                }
            },
            Response.ErrorListener {

            })

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)


    }

    private fun sendNotificationToUser(fromName: String, alienProfileUserId: String, text: String, type: String) {

        var UserDeviceId: String? = ""

        val ref = FirebaseDatabase.getInstance().getReference("/users/$alienProfileUserId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                UserDeviceId = p0.child("fromDevice").value.toString()

                var notification: JSONObject = JSONObject()
                var notifcationBody: JSONObject = JSONObject()
                var dataBody: JSONObject = JSONObject()

                try {
                    dataBody.put("fromName", fromName);
                    dataBody.put("text", text)
                    dataBody.put("uid", FirebaseAuth.getInstance().uid);
                    dataBody.put("method", type);


                    notification.put("to", UserDeviceId);
                    //  notification.put("notification", notifcationBody);
                    notification.put("data", dataBody);
                } catch (e: JSONException) {
//            Log.e(TAG, "onCreate: " + e.getMessage() );
                }
                sendNotification(notification);

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun fetchCurrentUser(alienProfileUserId: String, text: String, type: String){
        var fromName: String = ""
        val fromId = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/users/$fromId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                fromName = p0.child("name").value.toString()

                sendNotificationToUser(fromName, alienProfileUserId, text, type)

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun whriteToUserNotification(text: String) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-notifications/")
        val notificationRef = ref.child(events_data.userid!!).push()
        val timestamp = System.currentTimeMillis()/1000
        val values: java.util.HashMap<String, Any?> = hashMapOf(
            "text" to text,
            "timestamp" to timestamp,
            "senderUid" to fromId
        )
        notificationRef.updateChildren(values)
    }
}
