package com.blackcharm.views

import android.content.Context
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.blackcharm.R
import com.blackcharm.bottom_nav_pages.ProfileFragment
import com.blackcharm.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.profile_pics_item.view.*


class ProfilePicsResultRow(var context: Context, val image: String, val imagekey: String, val adapter: GroupAdapter<ViewHolder>): Item<ViewHolder>() {

    var contactUser: User? = null
    lateinit var likeviewbutton: Button
    lateinit var unlikebutton: Button
    private var myContext = FragmentActivity()



    override fun bind(viewHolder: ViewHolder, position: Int) {

        val SenderUid: String?


      //  SenderUid = likerow.key
        val targetImageView = viewHolder.itemView.iv_photo
        Glide.with(targetImageView).load(image)
            .placeholder(R.drawable.progress_animation)

            .error(R.drawable.branco).into(targetImageView)


//        val ref = FirebaseDatabase.getInstance().getReference("/users/$SenderUid")
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                contactUser = p0.getValue(User::class.java)
////                Log.d("Contactpage2", ""+p0)
//                if (p0.value !== null){
//                    viewHolder.itemView.peoplelike_name.text = contactUser?.name
//                    val targetImageView = viewHolder.itemView.peoplelike_imageview
//                    Picasso.get().load(contactUser?.profileImageUrl).into(targetImageView)
//
//
//                }
//
//
//            }
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })

//        viewHolder.itemView.peoplelike_view_button.setOnClickListener {
//            val intent = Intent(context, AlienProfilePageActivity::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra("profileuid", SenderUid)
//
//            context.startActivity(intent)
//        }
        viewHolder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)
            val array = arrayOf("Selecionar como foto do perfil","Excluir esta foto")



            builder.setTitle("Foto de ação")


            builder.setItems(array,{_, which ->

                val selected = array[which]
                if(selected == "Selecionar como foto do perfil"){
                    selectprofilepic(image, viewHolder)

                }
                if(selected == "Excluir esta foto") {
                    deleteselectedphoto(image, viewHolder)

                }
            })
            builder.setNeutralButton("Cancelar"){dialog,which ->
            }

            val dialog: AlertDialog = builder.create()
            //dialog.getWindow().setLayout(600, 400);
            dialog.show()
             true

        }
        viewHolder.itemView.setOnClickListener {
//            val intent = Intent(context, ChatLogActivity::class.java)
//
//            intent.putExtra("USER_KEY", chatPartnerUser)
//            intent.putExtra("USER_UID", chatPartnerId)
//
//            context.startActivity(intent)
            Log.d("tetet", ""+viewHolder.item)
            Log.d("tetet", "hf"+viewHolder.itemId)
            Log.d("tetet", "hf"+imagekey)
        }


    }
private fun deleteselectedphoto(deleteimage: String, viewHolder: ViewHolder) {
    val fromId = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/user-profile-images/").child(fromId!!).child(imagekey)
    ref.removeValue()
    val notifyrow = adapter.getAdapterPosition(viewHolder.item)
    adapter.removeGroup(notifyrow)




}
    private fun selectprofilepic(profileimage: String, viewHolder: ViewHolder) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("users").child(fromId!!)
        val map: HashMap<String, Any> = hashMapOf(
            "profileImageUrl" to profileimage)
        Log.d("profile image: ", profileimage)
        ref.updateChildren(map).addOnSuccessListener {
            if(it !=null) {
                var testest: ProfileFragment = ProfileFragment()

                testest.onResume()
            }

        }



}



    override fun getLayout(): Int {
        return R.layout.profile_pics_item

    }

}