package com.blackcharm.views

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.blackcharm.Common
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.blackcharm.ProfilePage.AlienProfilePageActivity
import com.blackcharm.ProfilePage.MembershipActivity
import com.blackcharm.R
import com.blackcharm.models.NotificationLine
import com.blackcharm.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.android.synthetic.main.notifications_row.view.*
import java.util.*


class NotificationsRow(var context: Context, val notificationrow: NotificationLine, val adapter: GroupAdapter<ViewHolder>, val notificationkey: String, val notificationseen: Int): Item<ViewHolder>() {

    var notificationUser: User? = null



    override fun bind(viewHolder: ViewHolder, position: Int) {

        val SenderUid: String
        val notificationType: String
        val timeInt: Int



        SenderUid = notificationrow.senderUid
        notificationType = notificationrow.text
        timeInt = notificationrow.timestamp


        //**************TIME CALCULATION**************//

        val nowInt = System.currentTimeMillis()/1000

        //Log.d("testtesttest", "hello wold")

        val minute = 60
        val hour = 60 * minute
        val day = 24 * hour
        val week =  7 * day
        val month = 30 * day
        val year = 12 * month
        var agoString = String()

        var timeInterval = nowInt - timeInt


        if (timeInterval < minute) {
            agoString = "$timeInterval seconds ago"
        } else if (timeInterval < hour) {
            timeInterval /= minute
            agoString = "$timeInterval minutos atrás"
        } else if (timeInterval < day) {
            timeInterval /= hour
            agoString = "$timeInterval hours ago"
        } else if (timeInterval < week) {
            timeInterval /= day
            agoString = "$timeInterval days ago"
        } else if (timeInterval < month) {
            timeInterval /= week
            agoString = "$timeInterval weeks ago"
        } else if (timeInterval < year) {
            timeInterval /= month
            agoString = "$timeInterval month ago"
        } else if (timeInterval > year) {
            timeInterval /= year
            agoString = "$timeInterval years ago"
        }

        //**************TIME CALCULATION**************//

        if(notificationseen == 0) {
            //viewHolder.itemView.notification_new_icon.setVisibility(View.GONE)
           // Log.d("notificationrow", ""+notificationrow.seen)
            viewHolder.itemView.notification_new_icon.setVisibility(View.VISIBLE)
            Log.d("notificationrow", ""+notificationseen)

        }
        if (notificationseen == 1){
            viewHolder.itemView.notification_new_icon.setVisibility(View.GONE)
            Log.d("notificationrow", ""+notificationseen)

        }


        val ref = FirebaseDatabase.getInstance().getReference("/users/$SenderUid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                notificationUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_textview_notification.text = notificationUser?.name
                viewHolder.itemView.notification_type_textview.text = notificationType
                viewHolder.itemView.time_textview_notification.text = agoString

                val targetImageView = viewHolder.itemView.imageview_notification

                if(Common.getInstance().membership_status == "yes"){
                    Picasso.get().load(notificationUser?.profileImageUrl)
                        .placeholder(R.drawable.progress_animation)
                        .error(R.drawable.branco).into(targetImageView)
                }else{
                    Picasso.get().load(notificationUser?.profileImageUrl)
                        .placeholder(R.drawable.progress_animation)
                        .transform(BlurTransformation(context,25,3))
                        .error(R.drawable.branco).into(targetImageView)
                }

//                Glide.with(targetImageView).load(notificationUser?.profileImageUrl)
//                    .placeholder(R.drawable.progress_animation)
//            //        .transform(FillSpace())
//                    .error(R.drawable.branco).into(targetImageView)


            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        viewHolder.itemView.notification_clear_button.setOnClickListener { item ->
            val fromId = FirebaseAuth.getInstance().uid
            FirebaseDatabase.getInstance().getReference("users-notifications").child(fromId!!).child(notificationkey!!).removeValue()

            val notifyrow = adapter.getAdapterPosition(viewHolder.item)
            adapter.removeGroup(notifyrow)
            Log.d("notificationrow", ""+notificationUser?.name)



        }

        viewHolder.itemView.notification_view_button.setOnClickListener {

            if (Common.getInstance().membership_status == "yes"){
                val intent = Intent(context, AlienProfilePageActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("profileuid", SenderUid)

                context.startActivity(intent)

                singleSee(notificationkey, viewHolder)
            }else{
                val intent = Intent(context, MembershipActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }

        }


    }
    private fun singleSee(id: String, viewHolder: ViewHolder){
        val fromId = FirebaseAuth.getInstance().uid
        val wishList = FirebaseDatabase.getInstance().getReference("users-notifications").child(fromId!!).child(id)
        val update: HashMap<String, Any> = hashMapOf(
            "seen" to 1)

        wishList.updateChildren(update)
        viewHolder.itemView.notification_new_icon.setVisibility(View.GONE)

    }

    override fun getLayout(): Int {
        return R.layout.notifications_row

    }
}