package com.blackcharm.discovery

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.blackcharm.ImageViewpaperActivity
import com.blackcharm.NotificationHandler.CustomJsonObjectRequestBasicAuth
import com.blackcharm.NotificationHandler.VolleySingleton
import com.blackcharm.R
import com.blackcharm.common.RecyclerItemClickListener
import com.blackcharm.messages.ChatLogActivity
import com.blackcharm.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_discover_detail.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.ArrayList
import java.io.IOException as IOException1

class DiscoveryDetailActivity : AppCompatActivity() {

    var discovery_data : Discovery = Discovery();
    private var recyclerView: RecyclerView? = null
    private var mImages: ArrayList<DiscoveryImage>? = null
    private var mAdapter: DiscoveryImageAdapter? = null
    var chatPartnerUser: User? = null
    var isInWishlist: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover_detail)

        //**********Show back button on top toolbar**********

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.alien_profile_toolbar);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        //**********End show back button on top toolbar**********

        discovery_data = intent.getSerializableExtra("discovery_data") as Discovery

        try {

            Glide.with(this)
                .load(discovery_data.images.get(0).uri)
                .into(discovery_view_image)
        }catch (e : Exception)
        {}

        discovery_view_toolbar_title.text = discovery_data.title
        discovery_view_title.text = discovery_data.title
        setTextWithSpan(discovery_view_subtitle,"Apresentacao",discovery_data.subtitle)
        setTextWithSpan(discovery_view_location,"Localizacao",discovery_data.location)
        setTextWithSpan(discovery_view_price,"Preco",discovery_data.price)
        setTextWithSpan(discovery_view_includes,"Includes",discovery_data.includes)
        setTextWithSpan(discovery_view_duarations,"Aberto",discovery_data.durations)
        setTextWithSpan(discovery_view_minage,"Min age",discovery_data.minage)
        setTextWithSpan(discovery_view_groupsize,"Group size",discovery_data.groupsize)
        setTextWithSpan(discovery_view_email,"Email",discovery_data.email)
        setTextWithSpan(discovery_view_website,"Website",discovery_data.website)
        setTextWithSpan(discovery_view_instagram,"Instagram",discovery_data.instagram)
        setTextWithSpan(discovery_view_phone,"Phone",discovery_data.phone)

        discovery_view_description.text = discovery_data.description


        mAdapter = DiscoveryImageAdapter(discovery_data.images)
        recyclerView = findViewById(R.id.discovery_view_image_box) as RecyclerView
        recyclerView!!.layoutManager = GridLayoutManager(this, 3)
        recyclerView!!.adapter = mAdapter



        recyclerView?.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                recyclerView,
                object : RecyclerItemClickListener.ClickListener {

                    override fun onItemClick(view: View, position: Int) {
//
//                        val fullScreenIntent =
//                            Intent(view.context, FullScreenImageActivity::class.java)
//                        fullScreenIntent.data = Uri.parse(discovery_data.images.get(position).getUri())
//                        startActivity(fullScreenIntent)

                        val images : ArrayList<String> = ArrayList()
                        for (i in 0 until discovery_data.images.size)
                        {
                            images.add(discovery_data.images.get(i).getUri());
                        }

                        val fullScreenIntent =
                            Intent(view.getContext(), ImageViewpaperActivity::class.java)
                        fullScreenIntent.putStringArrayListExtra("urls", images);
                        startActivity(fullScreenIntent)
                    }

                    override fun onLongItemClick(view: View, position: Int) {

                    }
                })
        )


        discovery_view_email.setOnClickListener{
            try {
                val emailIntent : Intent = Intent(android.content.Intent.ACTION_SEND)
                emailIntent.setType("plain/text")
                val emailaddress = arrayOf (discovery_data.email.replace(" ",""))
                emailIntent.putExtra(Intent.EXTRA_EMAIL, emailaddress);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
            catch (e : Exception) {

            }
        }

        discovery_view_website.setOnClickListener{
            try {
                val browserIntent: Intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("http://"+discovery_data.website.replace(" ","")));
                startActivity(browserIntent);
            }
            catch (e:Exception){

            }
        }

        discovery_view_phone.setOnClickListener {
            try {
                val callIntent: Intent =  Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + discovery_data.phone.replace("+","").replace(" ","").replace("[^0-9]", "")));
                startActivity(callIntent);
            }
            catch (e:Exception){

            }
        }

        discovery_view_instagram.setOnClickListener{
             val uri :Uri = Uri.parse("http://instagram.com/_u/"+discovery_data.instagram.replace(" ","").replace("@",""))
             val likeIng :Intent = Intent(Intent.ACTION_VIEW, uri)

            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (e : ActivityNotFoundException) {
                startActivity( Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/"+discovery_data.instagram.replace(" ","").replace("@",""))));
            }
        }

        changeMoreInfoBtn.setOnClickListener{
            if(discovery_view_description.maxLines==2)
            {
                discovery_view_description.maxLines=100
                discovery_view_description.ellipsize = TextUtils.TruncateAt.MARQUEE
                changeMoreInfoBtn.text="less"
            }
            else
            {
                discovery_view_description.maxLines=2
                discovery_view_description.ellipsize = TextUtils.TruncateAt.END
                changeMoreInfoBtn.text="more"
            }
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/"+discovery_data.userid)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
        bt_discover_message.setOnClickListener {
                 val intent = Intent(this, ChatLogActivity::class.java)

            intent.putExtra("USER_KEY", chatPartnerUser)
            intent.putExtra("USER_UID", discovery_data.userid)

            startActivity(intent)
        }
        bt_discover_like.setOnClickListener {
            addToWishlist()
        }

        bt_discover_share.setOnClickListener{
            var imageToShare : String  = discovery_data.images[0].uri; //Image You wants to share


            Picasso.get().load(imageToShare).into(object : com.squareup.picasso.Target {
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                    var  title : String = discovery_data.title //Title you wants to share

                    var shareIntent : Intent = Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                    shareIntent.setType("*/*");
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, discovery_data.description);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                    startActivity(Intent.createChooser(shareIntent, "Select App to Share Text and Image"));
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

            })
        }

        isInYourWishlist()
    }

    fun getLocalBitmapUri(bmp:Bitmap?) : Uri? {
        var builder :StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        var bmpUri : Uri? = null;
        try {
            var file :File =  File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            var out : FileOutputStream = FileOutputStream(file);
            bmp?.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch ( e : IOException1) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    internal fun setTextWithSpan(textView: TextView, spanText: String, text: String) {
        val sb = SpannableStringBuilder("$spanText:  $text")
        val start = 0
        val end = spanText.length+1
        val boldStyle = StyleSpan(Typeface.BOLD)
        sb.setSpan(boldStyle, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        textView.text = sb
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.alien_profile_menu, menu);
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        var id: Int = item!!.getItemId()


        return super.onOptionsItemSelected(item)
    }


    private fun isInYourWishlist(){
        val fromId = FirebaseAuth.getInstance().uid
        val alienProfileUserId = discovery_data.discovery_id
        val ref = FirebaseDatabase.getInstance().getReference("/users-discovery-likes/").child(fromId!!)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (child in p0.children) {
                    if (child.key == alienProfileUserId){
                        isInWishlist = true
                    }
                }
                if (isInWishlist == true) {
                    hidAddToWishlistButton()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun addToWishlist(){
        //checkIfYouAreBanned()
        val addItToWishlistByUid = discovery_data.discovery_id
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-discovery-likes/")
        val usersReference = ref.child(fromId!!)
        val addItToWishlist: HashMap<String, Any> = hashMapOf(
            addItToWishlistByUid!! to 1)
        usersReference.updateChildren(addItToWishlist)

        whriteToUserNotification("Likes Discovery " + discovery_data.title )
        fetchCurrentUser(addItToWishlistByUid, "Likes Discovery " + discovery_data.title ,"likeyou")
        // self.sentProposalNotification(toId: goToControllerByMemberUid!, fromId: uid, text: self.notifLike)
        hidAddToWishlistButton()
//        print("Add to wishlist")
//        inWishList = true
    }
    private fun hidAddToWishlistButton(){
        bt_discover_like.setVisibility(View.GONE)
    }
    private fun sendNotification(notification: JSONObject) {

        var FCM_API: String = "https://fcm.googleapis.com/fcm/send";
        var jsonObjectRequest: JsonObjectRequest = CustomJsonObjectRequestBasicAuth(
            Request.Method.POST, FCM_API, notification,
            Response.Listener { response ->

                try {
//                    Log.i(LatestMessagesActivity.TAG, "onResponse: " + response.toString());
                }catch (e:Exception){
//                    Log.i(LatestMessagesActivity.TAG, "onError: " + e);
                }
            },
            Response.ErrorListener {

            })

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)


    }

    private fun sendNotificationToUser(fromName: String, alienProfileUserId: String, text: String, type: String) {

        var UserDeviceId: String? = ""

        val ref = FirebaseDatabase.getInstance().getReference("/users/$alienProfileUserId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                UserDeviceId = p0.child("fromDevice").value.toString()

                var notification: JSONObject = JSONObject()
                var notifcationBody: JSONObject = JSONObject()
                var dataBody: JSONObject = JSONObject()

                try {
                    dataBody.put("fromName", fromName);
                    dataBody.put("text", text)
                    dataBody.put("uid", FirebaseAuth.getInstance().uid);
                    dataBody.put("method", type);


                    notification.put("to", UserDeviceId);
                    //  notification.put("notification", notifcationBody);
                    notification.put("data", dataBody);
                } catch (e: JSONException) {
//            Log.e(TAG, "onCreate: " + e.getMessage() );
                }
                sendNotification(notification);

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun fetchCurrentUser(alienProfileUserId: String, text: String, type: String){
        var fromName: String = ""
        val fromId = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/users/$fromId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                fromName = p0.child("name").value.toString()

                sendNotificationToUser(fromName, alienProfileUserId, text, type)

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun whriteToUserNotification(text: String) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-notifications/")
        val notificationRef = ref.child(discovery_data.userid!!).push()
        val timestamp = System.currentTimeMillis()/1000
        val values: java.util.HashMap<String, Any?> = hashMapOf(
            "text" to text,
            "timestamp" to timestamp,
            "senderUid" to fromId
        )
        notificationRef.updateChildren(values)
    }
}
