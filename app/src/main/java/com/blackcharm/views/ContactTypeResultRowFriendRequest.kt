package com.blackcharm.views

import android.content.Context
import android.content.Intent
import android.widget.Button
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.blackcharm.NotificationHandler.CustomJsonObjectRequestBasicAuth
import com.blackcharm.NotificationHandler.VolleySingleton
import com.blackcharm.ProfilePage.AlienProfilePageActivity
import com.blackcharm.R
import com.blackcharm.messages.ChatLogActivity
import com.blackcharm.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.contact_type_result_row_friend_request.view.*
import org.json.JSONException
import org.json.JSONObject


class ContactTypeResultRowFriendRequest(var context: Context, val contactrow: DataSnapshot, val adapter: GroupAdapter<ViewHolder>): Item<ViewHolder>() {

    var contactUser: User? = null
    lateinit var contactaddfriend: Button
    var SenderUid: String? = ""
    private var FCM_API: String = "https://fcm.googleapis.com/fcm/send";


    override fun bind(viewHolder: ViewHolder, position: Int) {



        contactaddfriend = viewHolder.itemView.findViewById(R.id.contact_add_friend)



        SenderUid = contactrow.key



        val ref = FirebaseDatabase.getInstance().getReference("/users/$SenderUid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                contactUser = p0.getValue(User::class.java)
//                Log.d("Contactpage2", ""+p0)
                if (p0.value !== null){
                    viewHolder.itemView.contact_type_name_request.text = contactUser?.name
                    val targetImageView = viewHolder.itemView.contact_type_imageview_request
                    Glide.with(targetImageView).load(contactUser?.profileImageUrl)
                        .placeholder(R.drawable.progress_animation)

                     //   .transform(FillSpace())
                        .error(R.drawable.branco).into(targetImageView)
                }


            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })


        viewHolder.itemView.contact_type_imageview_request.setOnClickListener {
            val intent = Intent(context, AlienProfilePageActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("profileuid", SenderUid)

            context.startActivity(intent)
        }

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(context, ChatLogActivity::class.java)

            intent.putExtra("USER_KEY", contactUser)
            intent.putExtra("USER_UID", SenderUid)

            context.startActivity(intent)
        }

        contactaddfriend.setOnClickListener {
            val alienProfileUserId = SenderUid
            val fromId = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/users-friends-proposals/")
            val currUserRemoveFriendProposal = ref.child(fromId!!).child("received").child(alienProfileUserId!!)
            currUserRemoveFriendProposal.removeValue()

            val friendUserRemoveCurrentProposal = ref.child(alienProfileUserId!!).child("sent-to").child(fromId!!)
            friendUserRemoveCurrentProposal.removeValue()
            val ref1 = FirebaseDatabase.getInstance().getReference("/users-friends/")
            val currUserAddFriend = ref1.child(fromId!!)
            val map: HashMap<String, Any> = hashMapOf(
                alienProfileUserId to 1)
            currUserAddFriend.updateChildren(map)
            val friendUserAddCurrent = ref1.child(alienProfileUserId!!)
            val friendUserAddCurrentMap: HashMap<String, Any> = hashMapOf(
                fromId to 1)
            friendUserAddCurrent.updateChildren(friendUserAddCurrentMap)

            val notifyrow = adapter.getAdapterPosition(viewHolder.item)
            adapter.removeGroup(notifyrow)
            fetchCurrentUser(alienProfileUserId, "Accepted your friend request")

        }






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

                try {
                    notifcationBody.put("title", fromName);
                    notifcationBody.put("body", text);

                    notification.put("to", UserDeviceId);
                    notification.put("notification", notifcationBody);
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

    override fun getLayout(): Int {
        return R.layout.contact_type_result_row_friend_request

    }



}