package com.blackcharm.SearchAdapter

import android.content.Context
import android.content.Intent
import android.util.Log
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
import com.blackcharm.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.blackcharm.NotificationHandler.CustomJsonObjectRequestBasicAuth
import com.blackcharm.NotificationHandler.VolleySingleton
import com.blackcharm.ProfilePage.AlienProfilePageActivity
import com.blackcharm.ProfilePage.MembershipActivity
import com.blackcharm.R
import com.blackcharm.SearchModel.SearchModel
import com.blackcharm.models.User
import com.squareup.picasso.Picasso
import info.androidhive.fontawesome.FontDrawable
import jp.wasabeef.picasso.transformations.BlurTransformation
import org.json.JSONException
import org.json.JSONObject

class SearchAdapter(internal var context: Context,
                    internal var data:List<SearchModel>, internal var dataid: List<String>):PagerAdapter() {


    var alienProfileUser: String? = null

    private var FCM_API: String = "https://fcm.googleapis.com/fcm/send";

    internal var layoutInflater:LayoutInflater
    init {

        layoutInflater = LayoutInflater.from(context)

    }
    var chatPartnerUser: User? = null

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
        val profilesearchname = view.findViewById<View>(R.id.profile_search_name) as TextView
        val profilesearchage = view.findViewById<View>(R.id.profile_search_age) as TextView
        val profilesearchoccupation = view.findViewById<View>(R.id.profile_search_occupation) as TextView
        // val profilesearchlocation = view.findViewById<View>(R.id.profile_search_location) as TextView
        val profilesearchimage = view.findViewById<View>(R.id.profile_search_image) as ImageView
        val friendslineprofilebutton = view.findViewById<View>(R.id.friendsline_profile_button) as Button
        val friendslinecontactbutton = view.findViewById<View>(R.id.friendsline_contact_button) as Button
        val friendslineblockbutton = view.findViewById<View>(R.id.friendsline_block_button) as Button

//        val drawable = FontDrawable(context, R.string.fa_heart_solid, true, false)
//        drawable.setTextColor(context.getColor(R.color.orange_yellow))
//        drawable.textSize = 65F



//        friendslinecontactbutton.background = drawable

        val mainview_information_view = view.findViewById<LinearLayout>(R.id.mainview_information_view) as LinearLayout
        mainview_information_view.visibility = View.VISIBLE

        profilesearchname.text = data[position].name
        profilesearchage.text = data[position].age
//        if(Common.getInstance().membership_status == "yes"){
            Picasso
                .get()
                .load(data[position].profileImageUrl)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.branco)
                .into(profilesearchimage)
//        }else{
//            Picasso
//                .get()
//                .load(data[position].profileImageUrl)
//                .placeholder(R.drawable.progress_animation)
//                .transform(BlurTransformation(context,25,3))
//                .error(R.drawable.branco)
//                .into(profilesearchimage)
//        }



        if (data[position].occupation == ""){
            profilesearchoccupation.setVisibility(View.GONE)
        }
        else {
            profilesearchoccupation.text = data[position].occupation
        }


        // profilesearchlocation.text = data[position].loc

        alienProfileUser = data[position].key



//        view.setOnClickListener {
//            //Toast.makeText(context, ""+dataid[position], Toast.LENGTH_SHORT).show()
//            val intent = Intent(context, AlienProfilePageActivity::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////            val row = item as LatestMessageRow
//            intent.putExtra("profileuid", dataid[position])
//
//            context.startActivity(intent)
//
//        }
        profilesearchname.setOnClickListener {
            //Toast.makeText(context, ""+dataid[position], Toast.LENGTH_SHORT).show()
//            if (Common.getInstance().membership_status == "yes") {
                val intent = Intent(context, AlienProfilePageActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            val row = item as LatestMessageRow
                intent.putExtra("profileuid", alienProfileUser)

                context.startActivity(intent)
//            }else{
//                val intent = Intent(context, MembershipActivity::class.java)
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////            val row = item as LatestMessageRow
//                context.startActivity(intent)
//            }
        }
        friendslineprofilebutton.setOnClickListener {
//            if (Common.getInstance().membership_status == "yes") {
                val intent = Intent(context, AlienProfilePageActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            val row = item as LatestMessageRow
                intent.putExtra("profileuid", data[position].key)
            Log.d("clicked key:", data[position].key)

                context.startActivity(intent)
//            }else{
//                val intent = Intent(context, MembershipActivity::class.java)
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context.startActivity(intent)
//            }

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

        friendslinecontactbutton.setOnClickListener {
//            val intent = Intent(context, ContactPersonActivity::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra("profileuid", dataid[position])
//            intent.putExtra("profilename", data[position].name + " " + data[position].age)
//            context.startActivity(intent)

            /*val intent = Intent(context, ChatLogActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra("USER_KEY", data[position])
            intent.putExtra("USER_UID", data[position].key)

            context.startActivity(intent)*/
            val addItToWishlistByUid = data[position].key
            val fromId = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/users-wishlists/")
            val usersReference = ref.child(fromId!!)
            val addItToWishlist: HashMap<String, Any> = hashMapOf(
                addItToWishlistByUid!! to 1)
            usersReference.updateChildren(addItToWishlist)
            val ref1 = FirebaseDatabase.getInstance().getReference("/users-likes/")
            val likesReference = ref1.child(addItToWishlistByUid!!)
            val currUserliked: HashMap<String, Any> = hashMapOf(
                fromId!! to 1)
            likesReference.updateChildren(currUserliked)
            whriteToUserNotification("Gostou de você")
            fetchCurrentUser(addItToWishlistByUid, "Gostou de você")
            // self.sentProposalNotification(toId: goToControllerByMemberUid!, fromId: uid, text: self.notifLike)
            hidAddToWishlistButton(view)

        }
        friendslineblockbutton.setOnClickListener {
            val blockByUid = data[position].key
            val fromId = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/users-notlike/")
            val usersReference = ref.child(fromId!!)
            val addItToWishlist: HashMap<String, Any> = hashMapOf(
                blockByUid!! to 1)
            usersReference.updateChildren(addItToWishlist)
        }

        val fromId = FirebaseAuth.getInstance().uid
        val alienProfileUserId = alienProfileUser

        var isInWishlist : Boolean = false
        val ref = FirebaseDatabase.getInstance().getReference("/users-wishlists/").child(fromId!!)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (child in p0.children) {
                    if (child.key == alienProfileUserId){
                        isInWishlist = true
                        break;
                    }
                }
                if (isInWishlist == true) {
                    friendslinecontactbutton.setVisibility(View.GONE)
                }
                else
                    friendslinecontactbutton.setVisibility(View.VISIBLE)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        container.addView(view)
        return view

    }
//
//    private fun isInYourWishlist(view:View){
//        val fromId = FirebaseAuth.getInstance().uid
//        val alienProfileUserId = alienProfileUser
//        val ref = FirebaseDatabase.getInstance().getReference("/users-wishlists/").child(fromId!!)
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                for (child in p0.children) {
//                    if (child.key == alienProfileUserId){
//                        isInWishlist = true
//                    }
//                }
//                if (isInWishlist == true) {
//                    friendslinecontactbutton.setVisibility(View.INVISIBLE)
//                }
//                else
//                    friendslinecontactbutton.setVisibility(View.VISIBLE)
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//
//    }
    private fun addToWishlist(view: View){
        //checkIfYouAreBanned()
        val addItToWishlistByUid = alienProfileUser
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-wishlists/")
        val usersReference = ref.child(fromId!!)
        val addItToWishlist: HashMap<String, Any> = hashMapOf(
            addItToWishlistByUid!! to 1)
        usersReference.updateChildren(addItToWishlist)
        val ref1 = FirebaseDatabase.getInstance().getReference("/users-likes/")
        val likesReference = ref1.child(addItToWishlistByUid!!)
        val currUserliked: HashMap<String, Any> = hashMapOf(
            fromId!! to 1)
        likesReference.updateChildren(currUserliked)
        whriteToUserNotification("Gostou de você")
        fetchCurrentUser(addItToWishlistByUid, "Gostou de você")
        // self.sentProposalNotification(toId: goToControllerByMemberUid!, fromId: uid, text: self.notifLike)
        hidAddToWishlistButton(view)
//        print("Add to wishlist")
//        inWishList = true
    }


    private fun hidAddToWishlistButton(view: View){
        view.findViewById<View>(R.id.friendsline_contact_button).setVisibility(View.GONE)
    }

    private fun whriteToUserNotification(text: String) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-notifications/")
        val notificationRef = ref.child(alienProfileUser!!).push()
        val timestamp = System.currentTimeMillis()/1000
        val values: java.util.HashMap<String, Any?> = hashMapOf(
            "text" to text,
            "timestamp" to timestamp,
            "senderUid" to fromId
        )
        notificationRef.updateChildren(values)
    }

    private fun fetchCurrentUser(alienProfileUserId: String, text: String){
        var fromName: String = ""
        val fromId = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/users/$fromId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                fromName = p0.child("name").value.toString()

                sendNotificationToUser(fromName, alienProfileUserId, text)

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun sendNotificationToUser(fromName: String, alienProfileUserId: String, text: String) {

        var UserDeviceId: String? = ""

        val ref = FirebaseDatabase.getInstance().getReference("/users/$alienProfileUserId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                UserDeviceId = p0.child("fromDevice").value.toString()

                var notification: JSONObject = JSONObject()
                var notifcationBody: JSONObject = JSONObject()
                var dataBody: JSONObject = JSONObject()

                try {
                    notifcationBody.put("title", fromName);
                    notifcationBody.put("body", text)
                    notifcationBody.put("click_action", "chat");

                    dataBody.put("uid", FirebaseAuth.getInstance().uid);
                    dataBody.put("method", "profile");

                    notification.put("to", UserDeviceId);
                    notification.put("notification", notifcationBody);
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

    private fun sendNotification(notification: JSONObject) {

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

}