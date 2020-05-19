package com.blackcharm.bottom_nav_pages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blackcharm.R
import com.blackcharm.SearchListener.IFirebaseLoadDone
import com.blackcharm.SearchModel.SearchModel
import kotlinx.android.synthetic.main.activity_invite_contacts.*

class InviteFriendsActivity: AppCompatActivity(), IFirebaseLoadDone {
    override fun onSearchResaultSuccess(profileList: List<SearchModel>, dataid: List<String>) {

    }

    override fun onSearchResaultFailed(message: String) {

    }

    override fun OnSearchPermittedUserSuccess(profileList:List<String>, flag :Int) {
        //Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show()
    }

    lateinit var whatsappbtn: Button
    lateinit var facebookbtn: Button
    lateinit var phonebtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_contacts)

        text_invite.text = Html.fromHtml(getString(R.string.invite_text))

        later_invite_friends_button.setOnClickListener {
            this.finish()
        }

        whatsappbtn = findViewById(R.id.contacts_whatsapp_button)
        facebookbtn = findViewById(R.id.contacts_facebook_button)
        phonebtn = findViewById(R.id.contacts_phone_button)

        whatsappbtn.setOnClickListener {
            sendWhatsapp("Hey! Be my friend on this great new travel app.\n" +
                    "IOS: https://itunes.apple.com/app/id1439459277\n" +
                    "Android: http://play.google.com/store/apps/details?id=com.myfriendsroomlimited")
        }
        facebookbtn.setOnClickListener {
            shareOnFacebookMessenger("Hey! Be my friend on this great new travel app. " +
                    "https://itunes.apple.com/app/id1439459277\n" +
                    "Android: http://play.google.com/store/apps/details?id=com.myfriendsroomlimited")
        }
        phonebtn.setOnClickListener {
            shareOnSms("Hey! Be my friend on this great new travel app.\n" +
                    "IOS: https://itunes.apple.com/app/id1439459277\n" +
                    "Android: http://play.google.com/store/apps/details?id=com.myfriendsroomlimited")
        }





    }
    override fun onResume() {
        super.onResume()


    }

    private fun sendWhatsapp(message: String){
        var sendIntent: Intent = Intent()

        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.setType("text/plain")
        sendIntent.setPackage("com.whatsapp")
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent)
        }
        else {
            Toast.makeText(this, "Please install whatsapp app", Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun shareOnFacebookMessenger(message: String){

        var sendIntent: Intent = Intent()

        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.setType("text/plain")
        sendIntent.setPackage("com.facebook.orca");
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent)
        }
        else {
            Toast.makeText(this, "Please install facebook messenger.", Toast.LENGTH_SHORT)
                .show()
        }
//        FacebookSdk.sdkInitialize(this)
//         var content: ShareLinkContent = ShareLinkContent.Builder()
//            .setContentUrl(Uri.parse("https://itunes.apple.com/app/id1439459277"))
//            .build()
//
//            MessageDialog.show(this, content);


    }
    private fun shareOnSms(message: String){

//        var sendIntent: Intent = Intent()
//
//        sendIntent.setAction(Intent.ACTION_SEND)
//        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
//        sendIntent.setType("text/plain")
//        sendIntent.setPackage("com.facebook.orca");
//        if (sendIntent.resolveActivity(getPackageManager()) != null) {
//            startActivity(sendIntent)
//        }
//        else {
//            Toast.makeText(this, "Please install facebook messenger.", Toast.LENGTH_SHORT)
//                .show()
//        }


        var sendIntent: Intent = Intent(Intent.ACTION_VIEW)

        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body", message)

        startActivity(sendIntent);


    }

}