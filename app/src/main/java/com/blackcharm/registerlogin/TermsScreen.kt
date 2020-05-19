package com.blackcharm.registerlogin

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.blackcharm.R
import com.blackcharm.messages.LatestMessagesActivity
import kotlinx.android.synthetic.main.activity_terms_screen.*
import org.json.JSONObject
import java.util.*

class TermsScreen : AppCompatActivity() {

    lateinit var termsfacebookbutton: LoginButton
    lateinit var termsfacebookbuttoncustom: Button
    lateinit var txtversion: TextView


    var callbackManager: CallbackManager? = null

    var firebaseAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_screen)

        termsfacebookbutton = findViewById(R.id.terms_facebook_button1)
        termsfacebookbuttoncustom = findViewById(R.id.terms_facebook_button)
        txtversion = findViewById(R.id.version_txt)

        var pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        var versionName = pinfo.versionName;

        txtversion.text = versionName ?: ""

        FacebookSdk.sdkInitialize(getApplicationContext())
        // AppEventsLogger.activateApp(this@TermsScreen)

        firebaseAuth = FirebaseAuth.getInstance()

        callbackManager = CallbackManager.Factory.create()
        updateWithToken(AccessToken.getCurrentAccessToken())


        termsfacebookbutton.setReadPermissions(Arrays.asList("public_profile", "email"))


// Callback registration
        termsfacebookbutton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code


                var request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken()) { `object`, response ->

                    var email = `object`.getString("email")

                    //Log.d("FB1", "" + email)
                    //Log.d("FBLOGIN_FAILD", `object`.toString())

                    handleFacebookAccessToken(loginResult.accessToken, `object`)

//                    try {
//                        //here is the data that you want
//                        Logger.d("FBLOGIN_JSON_RES", `object`.toString())
//
//                        if (`object`.has("id")) {
//                            handleSignInResultFacebook(`object`)
//                        } else {
//                            Logger.e("FBLOGIN_FAILD", `object`.toString())
//                        }
//
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        dismissDialogLogin()
//                    }


                }

                val parameters = Bundle()
                parameters.putString("fields", "id, email, name, gender, picture.type(large).width(800).height(800)")
                request.parameters = parameters
                request.executeAsync()



                Log.d("FB", "" + loginResult)

            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })




        text_terms_confirm.text = Html.fromHtml(getString(R.string.confirm_terms))

        //make links clickable within the text on checkbox
        text_terms_confirm.setMovementMethod(LinkMovementMethod.getInstance());



        terms_email_button.setOnClickListener {
            if(confirm_age_checkbox.isChecked == false || terms_checkbox.isChecked == false){

                    val builder = AlertDialog.Builder(this@TermsScreen)
                    builder.apply {
                        setPositiveButton("Ok",
                            DialogInterface.OnClickListener { dialog, id ->

                            })
                    }
                builder.setTitle("Notice")
                builder.setMessage("Please accept our terms and privacy condition and make sure you are over 18 years old")
                    // Set other dialog properties


                    // Create the AlertDialog
                val dialog: AlertDialog = builder.create()
                dialog.show()


            }
            else {
                val intent =  Intent(this, RegisterLoginActivityMain::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

            }

        }


    }
    private fun updateWithToken(currentAccessToken: AccessToken?) {


        if (currentAccessToken != null) {
            LoginManager.getInstance().logOut();
        }
    }
    public fun onClickFacebookButton(view: View) {

        if (view == termsfacebookbuttoncustom) {
            if(confirm_age_checkbox.isChecked == false || terms_checkbox.isChecked == false){

                val builder = AlertDialog.Builder(this@TermsScreen)
                builder.apply {
                    setPositiveButton("Ok",
                        DialogInterface.OnClickListener { dialog, id ->

                        })
                }
                builder.setTitle("Notice")
                builder.setMessage("Please accept our terms and privacy condition and make sure you are over 18 years old")
                // Set other dialog properties


                // Create the AlertDialog
                val dialog: AlertDialog = builder.create()
                dialog.show()


            }
            else {
                terms_facebook_button1.performClick()
            }

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)


    }


    private fun handleFacebookAccessToken(token: AccessToken, userInfo: JSONObject) {
        Log.d("FB", "handleFacebookAccessToken:" + token)
        Log.d("FB", "handleFacebookAccessToken:" + userInfo.toString())

        val credential = FacebookAuthProvider.getCredential(token.token)

        firebaseAuth!!.signInWithCredential(credential)

            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    var uid = FirebaseAuth.getInstance().uid
                    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            if (p0.exists()) {
                                val intent = Intent(this@TermsScreen, LatestMessagesActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()

                            } else {
                                FirebaseAuth.getInstance().signOut()
                                uid = FirebaseAuth.getInstance().uid

                                val fbUserEmail = userInfo["email"] as? String
                                val fbUserId = userInfo["id"] as? String
                                val fbUserName = userInfo["name"] as? String
                                val fbUserPic = userInfo.getJSONObject("picture").getJSONObject("data").getString("url")

                                Log.d("FBPIC", "" + fbUserPic)
                                Log.d("FBPIC", "" + fbUserId)

                                val intent = Intent(this@TermsScreen, RegisterLoginActivityMain::class.java)
                                intent.putExtra("Name", fbUserName)
                                intent.putExtra("Email", fbUserEmail)
                                intent.putExtra("FbId", fbUserId)
                                intent.putExtra("FbToken", token)
                                intent.putExtra("FbPhoto", fbUserPic)

                                startActivity(intent)
                                finish()


                            }
                        }

                    })


                    // Sign in success, update UI with the signed-in user's information
                    // Log.d("FB", "signInWithCredential:success")
                    val user = firebaseAuth!!.currentUser
                    //Log.d("FB", ""+user)
                    //Log.d("FB", ""+credential)
                    //startActivity(Intent(this@TermsScreen, LatestMessagesActivity::class.java))

                } /*else if (!task.isSuccessful && task.exception is FirebaseAuthUserCollisionException) {
                    val exception = task.exception as FirebaseAuthUserCollisionException

                    if (exception.errorCode == "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL") {
                        firebaseAuth!!.fetchSignInMethodsForEmail(userInfo["email"] as String).addOnCompleteListener(this) { task2 ->
                            if ( task2.isSuccessful) {
                            }
                        }
                    }
                }*/ else {
                    // If sign in fails, display a message to the user.
                    Log.w("FB", "signInWithCredential:failure", task.getException())
                    Toast.makeText(this@TermsScreen, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}
