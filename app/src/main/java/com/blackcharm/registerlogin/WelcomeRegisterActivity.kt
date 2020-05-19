package com.blackcharm.registerlogin

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.facebook.AccessToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.blackcharm.R
import com.blackcharm.common.scaleBitmap
import com.blackcharm.messages.LatestMessagesActivity
import com.blackcharm.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.welcome_register_fragment.*
import java.io.ByteArrayOutputStream
import java.util.*

class WelcomeRegisterActivity: AppCompatActivity() {

    var registername: String? = ""
    var registeremail: String? = ""
    var registerphoto: Uri? = null
    var registerage: String? = ""
    var registerhometravelloc: String? = ""
    var registerpassword: String? = ""
    var registergendermale: Boolean = false
    var registergenderfemale: Boolean = false
    var finalgender: String? =""
    var referalMethod: String? =""
    var referalCode: String? =""
    var referalName: String? =""
    var referalCompany: String? =""
    var membershipdate: String? ="2020/03/17"
    var membershipstatus: String? =""

    var registerdatechecked: Boolean = false
    var registermeetchecked: Boolean = false
    var registertravel: Boolean = false

    var FbToken: AccessToken? = AccessToken.getCurrentAccessToken()

    lateinit var registerdatecheckedcheckbox: CheckBox
    lateinit var registermeetcheckedcheckbox: CheckBox
    lateinit var registertravelcheckbox: CheckBox

    var registeracceptingguests: String? = ""
    lateinit var welcomeregisteryescheckbox: CheckBox
    lateinit var welcomeregisternocheckbox: CheckBox
    lateinit var welcomeregistermaybecheckbox: CheckBox


    lateinit var registerbuttonregister: Button
    var firebaseAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_register_fragment)

        firebaseAuth = FirebaseAuth.getInstance()

        //**********Show back button on top toolbar**********

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.welcome_register_toolbar);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        //**********End show back button on top toolbar**********

        registerbuttonregister = findViewById(R.id.register_button_register)

        registerdatecheckedcheckbox = findViewById(R.id.welcome_register_date_checkbox)
        registermeetcheckedcheckbox = findViewById(R.id.welcome_register_meet_checkbox)
        registertravelcheckbox = findViewById(R.id.welcome_register_travel_checkbox)



        welcomeregisteryescheckbox = findViewById(R.id.welcome_register_yes_checkbox)
        welcomeregisternocheckbox = findViewById(R.id.welcome_register_no_checkbox)
        welcomeregistermaybecheckbox = findViewById(R.id.welcome_register_maybe_checkbox)




        welcomeregisteryescheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                welcomeregisteryescheckbox.setTextColor(getResources().getColor(R.color.LightOrange))
                welcomeregisternocheckbox.setChecked(false)
                welcomeregistermaybecheckbox.setChecked(false)

                welcomeregisteryescheckbox.setClickable(false)
                welcomeregisternocheckbox.setClickable(true)
                welcomeregistermaybecheckbox.setClickable(true)

            }
            else {
                welcomeregisteryescheckbox.setTextColor(getResources().getColor(R.color.CommonBlue))

            }
        }
        welcomeregisternocheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                welcomeregisternocheckbox.setTextColor(getResources().getColor(R.color.LightOrange))
                welcomeregisteryescheckbox.setChecked(false)
                welcomeregistermaybecheckbox.setChecked(false)

                welcomeregisternocheckbox.setClickable(false)
                welcomeregisteryescheckbox.setClickable(true)
                welcomeregistermaybecheckbox.setClickable(true)
            }
            else {
                welcomeregisternocheckbox.setTextColor(getResources().getColor(R.color.CommonBlue))
            }
        }
        welcomeregistermaybecheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                welcomeregistermaybecheckbox.setTextColor(getResources().getColor(R.color.LightOrange))
                welcomeregisteryescheckbox.setChecked(false)
                welcomeregisternocheckbox.setChecked(false)

                welcomeregistermaybecheckbox.setClickable(false)
                welcomeregisteryescheckbox.setClickable(true)
                welcomeregisternocheckbox.setClickable(true)
            }
            else {
                welcomeregistermaybecheckbox.setTextColor(getResources().getColor(R.color.CommonBlue))
            }
        }





        registername = intent.extras.getString("Name")
        registeremail = intent.extras.getString("Email")
        registerphoto = intent.getParcelableExtra("Photo")
        registerage = intent.extras.getString("Age")
        registerhometravelloc = intent.extras.getString("HomeTravelLoc")
        registergendermale = intent.extras.getBoolean("genderMale")
        registergenderfemale = intent.extras.getBoolean("genderFemale")
        registerpassword = intent.extras.getString("password")
        FbToken = intent.extras.getParcelable<AccessToken>("FbToken")
        referalMethod = intent.extras.getString("referalMethod")
        referalCode = intent.extras.getString("referalCode")
        referalName = intent.extras.getString("referalName")
        referalCompany = intent.extras.getString("referalCompany")





        Log.d("WelcomePage", registername)
        Log.d("WelcomePage", registeremail)
        Log.d("WelcomePage", registerage)
        Log.d("WelcomePage", registerhometravelloc)
        Log.d("WelcomePage", registergendermale.toString())
        Log.d("WelcomePage", registergenderfemale.toString())
        Log.d("WelcomePage", registerpassword)

        if(registergenderfemale == true) {
            finalgender = "female"
        }
        if(registergendermale == true) {
            finalgender = "male"
        }




        registerbuttonregister?.setOnClickListener {

            registerbuttonregister.isEnabled = false

            if(welcomeregisteryescheckbox.isChecked) {
                registeracceptingguests = "yes"
            }

            if(welcomeregisternocheckbox.isChecked){
                registeracceptingguests = "no"
            }

            if(welcomeregistermaybecheckbox.isChecked){
                registeracceptingguests = "maybe"
            }

            if(registerdatecheckedcheckbox.isChecked) {
                registerdatechecked = true
            } else {
                registerdatechecked = false
            }

            if(registermeetcheckedcheckbox.isChecked){
                registermeetchecked = true
            } else {
                registermeetchecked = false
            }

            if(registertravelcheckbox.isChecked){
                registertravel = true
            } else {
                registertravel = false
            }

            if(whatdoyoudo_welcome_register.text.toString().contains("...") || about_welcome_register.text.toString().contains("...") ) {
                Toast.makeText(this, "Dots not allowed", Toast.LENGTH_SHORT).show()
                registerbuttonregister.isEnabled = true

            }
//            if(whatdoyoudo_welcome_register.text.toString().equals("") || about_welcome_register.text.toString().equals("")) {
//                Toast.makeText(this, "Please", Toast.LENGTH_SHORT).show()
//            }
            else if(!registerdatecheckedcheckbox.isChecked && !registermeetcheckedcheckbox.isChecked && !registertravelcheckbox.isChecked) {
                Toast.makeText(this, "Please check and make sure all the fields are filled correctly", Toast.LENGTH_SHORT).show()
                welcome_what_are_you_looking_for_text_title.setTextColor(ContextCompat.getColor(this, R.color.LightOrange))
                registerbuttonregister.isEnabled = true
            }
            else if(whatdoyoudo_welcome_register.text.toString().equals("")) {
                Toast.makeText(this, "Please tell us what do you do", Toast.LENGTH_SHORT).show()
                whatdoyoudotitle.setTextColor(ContextCompat.getColor(this, R.color.LightOrange))
                registerbuttonregister.isEnabled = true
            }
            else if(about_welcome_register.text.toString().equals("")) {
                Toast.makeText(this, "Please write a few words about you", Toast.LENGTH_SHORT).show()
                writefewwordstitle.setTextColor(ContextCompat.getColor(this, R.color.LightOrange))
                registerbuttonregister.isEnabled = true
            }
            else if(registerphoto == null) {
                Toast.makeText(this, "You have to set a profile picture", Toast.LENGTH_SHORT).show()
                registerbuttonregister.isEnabled = true
            }

            else{
                performRegister()
            }





        }
    }

    private fun performRegister() {
        var imgdata: ByteArray = ByteArray(0)

        Picasso.get().load(registerphoto)
            .placeholder(R.drawable.progress_animation)

            .error(R.drawable.branco).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                val baos = ByteArrayOutputStream()
                var scaled : Bitmap  = scaleBitmap(bitmap!!)
                scaled?.compress(Bitmap.CompressFormat.JPEG, resources.getInteger(R.integer.image_compression), baos)
            //    bitmap = Bitmap.createScaledBitmap()

                imgdata = baos.toByteArray()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
        })

//        var testimg = Picasso.get().load(registerphoto).into(targetimg)






        if (FbToken != null) {


            val credential = FacebookAuthProvider.getCredential(FbToken!!.token)
            firebaseAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->


                    if (task.isSuccessful) {

                        var uid = FirebaseAuth.getInstance().uid
                        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
                        Log.w("THISUID", ""+uid)


                        val filename = UUID.randomUUID().toString()
                        val ref1 = FirebaseStorage.getInstance().getReference("/images/$filename")

                        Log.d("Main", "Uploading")


//                        var testimg: InputStream = URL(registerphoto.toString()).openStream()


                        ref1.putBytes(imgdata!!)
                            .addOnSuccessListener {
                                Log.d("Register", "Succesfully uploaded image: ${it.metadata?.path}")

                                ref1.downloadUrl.addOnSuccessListener {
                                    //Log.d("RegisterLoginActivityMain", "File Location: $it")

                                    val map: HashMap<String?, Any?> = hashMapOf(
                                        "name" to registername!!,
                                        "age"  to registerage!!,
                                        "occupation" to whatdoyoudo_welcome_register.text.toString(),
                                        "email" to registeremail!!,
                                        "aboutMe" to about_welcome_register.text.toString()!!,
                                        "yourSex" to finalgender!!,
                                        "profileImageUrl" to it.toString(),
                                        "loc" to registerhometravelloc!!,
                                        "acceptingGuests" to registeracceptingguests!!,
                                        "meetChecked" to registermeetchecked,
                                        "dateChecked" to registerdatechecked,
                                        "travelChecked" to registertravel,
                                        "referalMethod" to referalMethod,
                                        "membershipdate" to membershipdate,
                                        "membershipstatus" to membershipstatus
//                                        "referalCode" to referalCode,
//                                        "referalName" to referalName,
//                                        "referalCompany" to referalCompany
                                    )



                                    if (referalMethod !== "from a person") {
                                        map.put("referalCode", referalCode)
                                        map.put("referalName", referalName)
                                        map.put("referalCompany", referalCompany)
                                    }

                                    ref.updateChildren(map)
                                        .addOnSuccessListener {
                                            val intent = Intent(this, LatestMessagesActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            intent.putExtra("Register", "true")
                                            startActivity(intent)
                                            val intent1 = Intent("finish_activity")
                                            sendBroadcast(intent1)
                                            finish()


                                        }
                                        .addOnFailureListener {
                                            //Do some logging

                                            registerbuttonregister.isEnabled = true
                                        }

                                }
                            }
                            .addOnFailureListener {
                                //Do some logging

                                registerbuttonregister.isEnabled = true
                            }




                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("FB", "signInWithCredential:failure", task.getException())
                        Toast.makeText(
                            this@WelcomeRegisterActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()

                        registerbuttonregister.isEnabled = true

                    }
                }
        }
        else {
            val email = registeremail
            val password = registerpassword
//
//        if(email!!.isEmpty() || password!!.isEmpty()) {
//            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
//            return
//        }

//        Log.d("RegisterLoginActivityMain", "Email is: " + email)
//        Log.d("RegisterLoginActivityMain", "Password: $password")

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    //else if success

                    Log.d("Main", "Success register: ${it.result?.user?.uid}")

                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    // Log.d("Main", "Failed to create user: ${it.message}")
                    Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()

                    registerbuttonregister.isEnabled = true
                }
        }
    }

    private fun uploadImageToFirebaseStorage() {
        Log.d("Main", "Trying to upload image")
        if (registerphoto == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        Log.d("Main", "Uploading")

        val bitmap = MediaStore.Images.Media.getBitmap(this?.contentResolver, registerphoto)

        val baos = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, resources.getInteger(R.integer.image_compression), baos)

        val imageByteData = baos.toByteArray()

        ref.putBytes(imageByteData)
            .addOnSuccessListener {
                Log.d("Register", "Succesfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    //Log.d("RegisterLoginActivityMain", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())

                }
            }
            .addOnFailureListener {
                //Do some logging
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        var token: String? = null

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("notific", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                token = task.result?.token

                // Log and toast
//                val msg = getString(R.string.msg_token_fmt, token)
                Log.d("notific", token)
                //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            })

        val user =
            User(registername!!, "", registeremail!!, profileImageUrl, registerage!!, whatdoyoudo_welcome_register.text.toString(), registerhometravelloc!!, "", registerdatechecked, registermeetchecked, registeracceptingguests!!, about_welcome_register.text.toString(), "", "", finalgender!!, registertravel, token, false, false, "", 999999999999999, referalMethod,membershipdate, "" ,referalCode, referalName, referalCompany)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Register", "Saved user to DB")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("Register", "true")
                startActivity(intent)
                val intent1 = Intent("finish_activity")
                sendBroadcast(intent1)
                finish()
            }
            .addOnFailureListener {
                //Do some logging
            }

    }

}