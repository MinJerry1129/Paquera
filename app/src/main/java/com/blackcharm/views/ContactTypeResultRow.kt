package com.blackcharm.views

import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.blackcharm.ProfilePage.AlienProfilePageActivity
import com.blackcharm.R
import com.blackcharm.messages.ChatLogActivity
import com.blackcharm.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.contact_type_result_row.view.*



class ContactTypeResultRow(var context: Context, val contactrow: String, val adapter: GroupAdapter<ViewHolder>): Item<ViewHolder>() {

    var contactUser: User? = null



    override fun bind(viewHolder: ViewHolder, position: Int) {

        val SenderUid: String?



        SenderUid = contactrow



        val ref = FirebaseDatabase.getInstance().getReference("/users/$SenderUid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                contactUser = p0.getValue(User::class.java)
//                Log.d("Contactpage2", ""+p0)
                if (p0.value !== null){
                    viewHolder.itemView.contact_type_name.text = contactUser?.name
                    val targetImageView = viewHolder.itemView.contact_type_imageview
                    Glide.with(targetImageView).load(contactUser?.profileImageUrl)
                        .placeholder(R.drawable.progress_animation)

                        .error(R.drawable.branco).into(targetImageView)
                }


            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        viewHolder.itemView.contact_type_imageview.setOnClickListener {
            val intent = Intent(context, AlienProfilePageActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("profileuid", SenderUid)

            context.startActivity(intent)
        }
//        viewHolder.itemView.setOnClickListener { item
//            val userItem = item as UserItem
//
//            val intent = Intent(context, ChatLogActivity::class.java)
////                    intent.putExtra(USER_KEY, userItem.user.username)
//            intent.putExtra(USER_KEY, userItem.user)
//            startActivity(intent)
//        }
        viewHolder.itemView.setOnClickListener {
            val intent = Intent(context, ChatLogActivity::class.java)

            intent.putExtra("USER_KEY", contactUser)
            intent.putExtra("USER_UID", SenderUid)

            context.startActivity(intent)
        }





    }

    override fun getLayout(): Int {
        return R.layout.contact_type_result_row

    }

}