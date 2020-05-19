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
import com.blackcharm.messages.ChatLogActivity
import com.blackcharm.models.ChatMessage
import com.blackcharm.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.android.synthetic.main.latest_message_row.view.*


//class LatestMessageRow(var context: Context, val chatMessage: ChatMessage, val messagecount: Int): Item<ViewHolder>() {
class LatestMessageRow(var context: Context, val chatMessage: ChatMessage): Item<ViewHolder>() {

    var chatPartnerUser: User? = null
    var chatPartnerId: String? = null
    var messagecountfinal = 0

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val timeInt: Long
        timeInt = chatMessage.timestamp

        viewHolder.itemView.message_textview_latest_message.text = chatMessage.text


        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }




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
            agoString = "$timeInterval segundos atrás"
        } else if (timeInterval < hour) {
            timeInterval /= minute
            agoString = "$timeInterval minutos atrás"
        } else if (timeInterval < day) {
            timeInterval /= hour
            agoString = "$timeInterval horas atrás"
        } else if (timeInterval < week) {
            timeInterval /= day
            agoString = "Há $timeInterval dias"
        } else if (timeInterval < month) {
            timeInterval /= week
            agoString = "Há $timeInterval semanas"
        } else if (timeInterval < year) {
            timeInterval /= month
            agoString = "$timeInterval meses  atrás"
        } else if (timeInterval > year) {
            timeInterval /= year
            agoString = "Há $timeInterval anos"
        }

        //**************TIME CALCULATION**************//


        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)

                viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.name
                viewHolder.itemView.time_textview_latest_message.text = agoString



                val targetImageView = viewHolder.itemView.imageview_latest_message
//                Glide.with(targetImageView).load(chatPartnerUser?.profileImageUrl)
//                    .placeholder(R.drawable.progress_animation)
//                    .error(R.drawable.branco).into(targetImageView)
//                if(Common.getInstance().membership_status =="yes"){
                    Picasso
                        .get()
                        .load(chatPartnerUser?.profileImageUrl)
                        .placeholder(R.drawable.progress_animation)
                        .error(R.drawable.branco)
                        .into(targetImageView)
//                }else{
//                    Picasso
//                        .get()
//                        .load(chatPartnerUser?.profileImageUrl)
//                        .placeholder(R.drawable.progress_animation)
//                        .transform(BlurTransformation(context,25,3))
//                        .error(R.drawable.branco)
//                        .into(targetImageView)
//                }


                fetchmessagecount(viewHolder)

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
        viewHolder.itemView.setOnClickListener {
//            if(Common.getInstance().membership_status =="yes"){
                val intent = Intent(context, ChatLogActivity::class.java)

                intent.putExtra("USER_KEY", chatPartnerUser)
                intent.putExtra("USER_UID", chatPartnerId)

                context.startActivity(intent)
//            }else{
//                val intent = Intent(context, MembershipActivity::class.java)
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context.startActivity(intent)
//            }

        }
        viewHolder.itemView.imageview_latest_message.setOnClickListener {

//            if(Common.getInstance().membership_status =="yes"){
                val intent = Intent(context, AlienProfilePageActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("profileuid", chatPartnerId)
                context.startActivity(intent)
//            }else{
//                val intent = Intent(context, MembershipActivity::class.java)
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context.startActivity(intent)
//            }
        }


    }

    private fun fetchmessagecount(viewHolder: ViewHolder) {
        val fromId = FirebaseAuth.getInstance().uid
        //messagecountfinal = 0

        val ref1 = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$chatPartnerId")

        ref1.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("messagecount", ""+p0.value)

                for(singlecount in p0.children) {
                    if(singlecount.value.toString().toInt() == 1){
                        Log.d("messagecount", "hegjrhgew")
                        messagecountfinal += 1
                        viewHolder.itemView.counter_textview_latest_message.setVisibility(View.VISIBLE)
                    }

                    viewHolder.itemView.counter_textview_latest_message.text = messagecountfinal.toString()

                }
                if(messagecountfinal == 0){
                    viewHolder.itemView.counter_textview_latest_message.setVisibility(View.GONE)
                }
                else{
                    viewHolder.itemView.counter_textview_latest_message.setVisibility(View.VISIBLE)
                }




            }
            override fun onCancelled(p0: DatabaseError) {}
        })
    }


    override fun getLayout(): Int {
        return R.layout.latest_message_row

    }


}