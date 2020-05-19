package com.blackcharm.ProfilePage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.blackcharm.R
import com.blackcharm.common.scaleBitmap
import com.blackcharm.models.User
import com.blackcharm.views.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_my_profile_pics.*
import java.io.ByteArrayOutputStream
import java.util.*

class ProfilePicsActivity: AppCompatActivity() {

    companion object {
        var currentUser: User? = null
        var messagelast: String? = null
        var counter: Int? = 1;
        var final: Int? = 0;
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;

    }
    val adapter = GroupAdapter<ViewHolder>()
    var selectedPhotoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile_pics)

        //**********Show back button on top toolbar**********

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.myprofile_pics_toolbar);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        //**********End show back button on top toolbar**********


        val layoutManager = GridLayoutManager(this, 4)
        var recyclerview_contacts_type = findViewById<RecyclerView>(R.id.recyclerview_profile_pics)
        recyclerview_contacts_type.layoutManager = layoutManager


        recyclerview_contacts_type.adapter = adapter
       // recyclerview_contacts_type.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))

        getProfileImages(this)

        profile_add_new_photo.setOnClickListener {
            //Intent to pick image
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        profile_pics_place_button.setOnClickListener {
            val intent =  Intent(this, ProfileHomePicsActivity::class.java)
            startActivity(intent)

        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            selectedPhotoUri = data.data

            Log.d("Register", "Image: ${data.data}")

            val bitmap = MediaStore.Images.Media.getBitmap(this?.contentResolver, selectedPhotoUri)

            val baos = ByteArrayOutputStream()


            var scaled : Bitmap  = scaleBitmap(bitmap!!)

            scaled.compress(Bitmap.CompressFormat.JPEG, resources.getInteger(R.integer.image_compression), baos)

            val b = baos.toByteArray()

            uploadNewImage(b)


        }
    }

private fun uploadNewImage(imageByteData: ByteArray){
    //checkIfYouAreBanned()
    val fromId = FirebaseAuth.getInstance().uid
    //let imageName = NSUUID().uuidString
    val filename = UUID.randomUUID().toString()
    val ref = FirebaseStorage.getInstance().getReference("/users_images/$filename")


    ref.putBytes(imageByteData)
        .addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                Log.d("RegisterLoginActivityMain", "File Location: $it")
//
                val timestamp = System.currentTimeMillis()/1000
//
                val imageupload: HashMap<String, Any?> = hashMapOf(
                    timestamp.toLong().toString() to it.toString())
                val ref1 = FirebaseDatabase.getInstance().getReference("/user-profile-images/")
                val userProfReference = ref1.child(fromId!!)
                userProfReference.updateChildren(imageupload)
//
            }
        }
        .addOnFailureListener {
            //Do some logging
        }



 }



private fun getProfileImages(context: Context) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-profile-images/$fromId")

//        Log.d("Receivenotification", "listenForNotifications $fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Receive notification", "error")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                adapter.add(ProfilePicsResultRow(context, p0.value.toString(), p0.key!!, adapter))

            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {




            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {



            }

            override fun onChildRemoved(p0: DataSnapshot) {
//                adapter.clear()
//                getProfileImages(context)


            }



        })
    }



}