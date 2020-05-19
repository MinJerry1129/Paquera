package com.blackcharm.views

import android.content.Context
import android.content.Intent
import android.widget.Button
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
import com.blackcharm.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.android.synthetic.main.mylikes_result_row.view.*


class MyLikesResultRow(var context: Context, val likerow: DataSnapshot, val adapter: GroupAdapter<ViewHolder>): Item<ViewHolder>() {

    var contactUser: User? = null
    lateinit var likeviewbutton: Button
    lateinit var unlikebutton: Button



    override fun bind(viewHolder: ViewHolder, position: Int) {

        val SenderUid: String?


        SenderUid = likerow.key



        val ref = FirebaseDatabase.getInstance().getReference("/users/$SenderUid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                contactUser = p0.getValue(User::class.java)
//                Log.d("Contactpage2", ""+p0)
                if (p0.value !== null){
                    viewHolder.itemView.mylikes_name.text = contactUser?.name
                    val targetImageView = viewHolder.itemView.mylikes_imageview
//                    Picasso.get().load(contactUser?.profileImageUrl)
//                        .placeholder(R.drawable.progress_animation)
//
//             //           .transform(FillSpace())
//                        .error(R.drawable.branco).into(targetImageView)

//                    Glide.with(targetImageView).load(contactUser?.profileImageUrl)
//                        .placeholder(R.drawable.progress_animation)
//                        //           .transform(FillSpace())
//                        .error(R.drawable.branco).into(targetImageView)

//                    if(Common.getInstance().membership_status == "yes"){
                        Picasso.get().load(contactUser?.profileImageUrl)
                            .placeholder(R.drawable.progress_animation)
                            .error(R.drawable.branco).into(targetImageView)
//                    }else{
//                        Picasso.get().load(contactUser?.profileImageUrl)
//                            .placeholder(R.drawable.progress_animation)
//                            .transform(BlurTransformation(context,25,3))
//                            .error(R.drawable.branco).into(targetImageView)
//                    }
                }


            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        viewHolder.itemView.mylikes_view_button.setOnClickListener {
//            if(Common.getInstance().membership_status == "yes"){
                val intent = Intent(context, AlienProfilePageActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("profileuid", SenderUid)
                context.startActivity(intent)
//            }else{
//                val intent = Intent(context, MembershipActivity::class.java)
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context.startActivity(intent)
//            }
        }
//        viewHolder.itemView.setOnClickListener { item
//            val userItem = item as UserItem
//
//            val intent = Intent(context, ChatLogActivity::class.java)
////                    intent.putExtra(USER_KEY, userItem.user.username)
//            intent.putExtra(USER_KEY, userItem.user)
//            startActivity(intent)
//        }
        viewHolder.itemView.unlike_button.setOnClickListener {
            //        checkIfYouAreBanned()
            val fromId = FirebaseAuth.getInstance().uid
            val wishReference = FirebaseDatabase.getInstance().getReference("/users-wishlists/")
            val likesReference = FirebaseDatabase.getInstance().getReference("/users-likes/")
            wishReference.child(fromId!!).child(SenderUid!!).removeValue { databaseError, databaseReference ->
                if (databaseError != null) {

                }
                else {
                    val likeresultrow = adapter.getAdapterPosition(viewHolder.item)
                    adapter.removeGroup(likeresultrow)
                }


            }
            likesReference.child(SenderUid!!).child(fromId!!).removeValue()
        }





    }



    override fun getLayout(): Int {
        return R.layout.mylikes_result_row

    }

}