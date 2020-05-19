package com.blackcharm.registerlogin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.blackcharm.R
import kotlinx.android.synthetic.main.activity_main.*

class RegisterLoginActivityMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        viewpager_main.adapter = fragmentAdapter

        tabs_main.setupWithViewPager(viewpager_main)


        registerReceiver(broadcast_reciever, IntentFilter("finish_activity"))
//        register_button_register.setOnClickListener {
//            performRegister()
//        }


//        already_have_account_text_view.setOnClickListener {
//            Log.d("RegisterLoginActivityMain", "Trying to show main activity")
//
//
//            //Launch login_activity
//
//            val intent = Intent(this, LoginActivity:: class.java)
//            startActivity(intent)
//        }
//
//        selectphoto_button_register.setOnClickListener {
//
//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            startActivityForResult(intent, 0)
//        }


    }

    val broadcast_reciever = object : BroadcastReceiver() {

        override fun onReceive(arg0: Context, intent: Intent) {
            val action = intent.action
            if (action == "finish_activity") {
                Log.d("quitregister", "trying")
                finish()

            }

        }

    }

//    var selectedPhotoUri: Uri? = null
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
//
//            selectedPhotoUri = data.data
//
//            Log.d("Register", "Image: ${selectedPhotoUri}")
//
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
//
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
//
//        }
//    }
//
//    private fun performRegister() {
//        val email = email_edittext_register.text.toString()
//        val password = password_edittext_register.text.toString()
//
//        if(email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        Log.d("RegisterLoginActivityMain", "Email is: " + email)
//        Log.d("RegisterLoginActivityMain", "Password: $password")
//
//        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener {
//                if (!it.isSuccessful) return@addOnCompleteListener
//
//                //else if success
//
//                Log.d("Main", "Success register: ${it.result?.user?.uid}")
//
//                uploadImageToFirebaseStorage()
//            }
//            .addOnFailureListener {
//               // Log.d("Main", "Failed to create user: ${it.message}")
//                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun uploadImageToFirebaseStorage() {
//        Log.d("Main", "Trying to upload image")
//        if (selectedPhotoUri == null) return
//
//        val filename = UUID.randomUUID().toString()
//        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
//
//        Log.d("Main", "Uploading")
//
//        ref.putFile(selectedPhotoUri!!)
//            .addOnSuccessListener {
//                Log.d("Register", "Succesfully uploaded image: ${it.metadata?.path}")
//
//                ref.downloadUrl.addOnSuccessListener {
//                    Log.d("RegisterLoginActivityMain", "File Location: $it")
//
//                    saveUserToFirebaseDatabase(it.toString())
//
//                }
//            }
//            .addOnFailureListener {
//                //Do some logging
//            }
//    }
//
//    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
//        val uid = FirebaseAuth.getInstance().uid ?: ""
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//
//        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)
//
//        ref.setValue(user)
//            .addOnSuccessListener {
//                Log.d("Register", "Saved user to DB")
//
//                val intent = Intent(this, LatestMessagesActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            }
//            .addOnFailureListener {
//                //Do some logging
//            }
//
//    }
}


//class User(val uid: String, val username: String, val profileImageUrl: String) {
//    constructor() : this("", "", "")
//}