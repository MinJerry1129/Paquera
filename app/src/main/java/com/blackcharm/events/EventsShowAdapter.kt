package com.blackcharm.events

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.blackcharm.NotificationHandler.CustomJsonObjectRequestBasicAuth
import com.blackcharm.NotificationHandler.VolleySingleton
import com.blackcharm.R
import com.squareup.picasso.Picasso
import info.androidhive.fontawesome.FontDrawable
import jp.wasabeef.picasso.transformations.BlurTransformation
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class EventsShowAdapter(internal var context: Context,
                        internal var data:List<Events>):PagerAdapter() {

    var alienProfileUser: String? = null

    private var FCM_API: String = "https://fcm.googleapis.com/fcm/send";
    internal var layoutInflater:LayoutInflater
    init {

        layoutInflater = LayoutInflater.from(context)

    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0==p1
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = layoutInflater.inflate(R.layout.fragment_friends_line, container, false)
        val profilesearchimage = view.findViewById<View>(R.id.profile_search_image) as ImageView
        val friendslinedetailbutton = view.findViewById<View>(R.id.friendsline_profile_button) as Button
        val friendslinelikebutton = view.findViewById<View>(R.id.friendsline_contact_button) as Button

//        val drawable = FontDrawable(context, R.string.fa_heart_solid, true, false)
//        drawable.setTextColor(context.getColor(R.color.orange_yellow))
//        drawable.textSize = 65F
//
//        friendslinelikebutton.background = drawable

        val events_information_view = view.findViewById<View>(R.id.events_information_view) as LinearLayout
        events_information_view.visibility = View.VISIBLE

        val mainview_events_organisation = view.findViewById<View>(R.id.mainview_events_organisation) as TextView
        mainview_events_organisation.text = data[position].organise
        val mainview_events_datetime = view.findViewById<View>(R.id.mainview_events_datetime) as TextView
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = (data[position].getDatetime()).toDouble().toLong();
        val date = DateFormat.format("EEEE dd LLLL yyyy, hh:mm", cal).toString()
        mainview_events_datetime.text = date
        val mainview_events_title = view.findViewById<View>(R.id.mainview_events_title) as TextView
        mainview_events_title.text = data[position].title
        val mainview_events_location = view.findViewById<View>(R.id.mainview_events_location) as TextView
        mainview_events_location.text = data[position].location
//        profilesearchname.text = data[position].title
//        profilesearchoccupation.text = data[position].
        Picasso
            .get()
            .load(data[position].image.uri)
            .placeholder(R.drawable.progress_animation)

            .error(R.drawable.branco)
            .into(profilesearchimage)
//

        // profilesearchlocation.text = data[position].loc

        alienProfileUser = data[position].userid

        friendslinedetailbutton.setOnClickListener {
            //Toast.makeText(context, ""+dataid[position], Toast.LENGTH_SHORT).show()
            val intent = Intent(context, EventsDetailActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            val row = item as LatestMessageRow
            intent.putExtra("events_data", data[position])

            context.startActivity(intent)

        }

//        val ref = FirebaseDatabase.getInstance().getReference("/users/$dataid[position]")
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                chatPartnerUser = p0.getValue(User::class.java)
//
//            }
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })

        friendslinelikebutton.setOnClickListener {
            addToWishlist(data[position],view)

        }

        isInYourWishlist(data[position],view)

        container.addView(view)
            return view

    }

    private fun isInYourWishlist(events_data : Events, view: View){
        var isInWishlist:Boolean=false;
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
                    hidAddToWishlistButton(view)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun addToWishlist(events_data : Events, view : View){
        //checkIfYouAreBanned()
        val addItToWishlistByUid = events_data.events_id
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-events-likes/")
        val usersReference = ref.child(fromId!!)
        val addItToWishlist: HashMap<String, Any> = hashMapOf(
            addItToWishlistByUid!! to 1)
        usersReference.updateChildren(addItToWishlist)

        whriteToUserNotification(events_data.userid,"Likes Event " + events_data.title )
        fetchCurrentUser(addItToWishlistByUid, "Likes Event " + events_data.title ,"likeyou")
        // self.sentProposalNotification(toId: goToControllerByMemberUid!, fromId: uid, text: self.notifLike)
        hidAddToWishlistButton(view)
//        print("Add to wishlist")
//        inWishList = true
    }
    private fun hidAddToWishlistButton(view:View){
        val friendslinelikebutton = view.findViewById<View>(R.id.friendsline_contact_button) as Button
        friendslinelikebutton.setVisibility(View.GONE)
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

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)


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

    fun whriteToUserNotification(userid:String , text: String) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-notifications/")
        val notificationRef = ref.child(userid!!).push()
        val timestamp = System.currentTimeMillis()/1000
        val values: java.util.HashMap<String, Any?> = hashMapOf(
            "text" to text,
            "timestamp" to timestamp,
            "senderUid" to fromId
        )
        notificationRef.updateChildren(values)
    }



}