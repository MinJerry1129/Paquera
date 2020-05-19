package com.blackcharm.ProfilePage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.blackcharm.Common
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.blackcharm.NotificationHandler.CustomJsonObjectRequestBasicAuth
import com.blackcharm.NotificationHandler.VolleySingleton
import com.blackcharm.R
import com.blackcharm.common.getTimeString
import com.blackcharm.messages.ChatLogActivity
import com.blackcharm.messages.LatestMessagesActivity
import com.blackcharm.models.User
import kotlinx.android.synthetic.main.alian_user_profile.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AlienProfilePageActivity : AppCompatActivity() {

    companion object {
        var currentUserProfile: User? = null
        var profilemenu: Menu? = null
        var blockbutton: MenuItem? = null
        var unblockbutton: MenuItem? = null

    }

    var alienProfileUser: String? = null
    var itIsAFriend: Boolean = false
    var isInWishlist: Boolean = false
    var friendshipProposal: String = ""
    // lateinit var alienprofileadddeletebutton: Button
    lateinit var alienprofileadddeletebutton: LinearLayout
    // lateinit var addToWishList: Button
    lateinit var addToWishList: LinearLayout
    // lateinit var friendslinecontactbutton: Button
    lateinit var friendslinecontactbutton: LinearLayout
    lateinit var alienprofiletype: TextView
    lateinit var alienprofileblockedtext: TextView
    var chatPartnerUser: User? = null
    var inFoFArray = false
    var membershipStatus: String? = null
    var mProfileImages : ArrayList<String>  = ArrayList() ;


    lateinit var reportbutton: MenuItem
    private var FCM_API: String = "https://fcm.googleapis.com/fcm/send";
    private var mAdapter: ProfileImageAdapter? = null
    private var recyclerView: RecyclerView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alian_user_profile)

        //**********Show back button on top toolbar**********

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.alien_profile_toolbar);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        //**********End show back button on top toolbar**********

        alienProfileUser = intent.extras.getString("profileuid")

        Log.d("alienprofile", alienProfileUser.toString())

        // alienprofileadddeletebutton = findViewById(R.id.alien_profile_add_delete_button)
        alienprofileadddeletebutton = findViewById(R.id.bt_friend)
        // addToWishList = findViewById(R.id.friendsline_like_button)
        addToWishList = findViewById(R.id.bt_like)
        // friendslinecontactbutton = findViewById(R.id.friendsline_contact_button)
        friendslinecontactbutton = findViewById(R.id.bt_message)
        alienprofiletype = findViewById(R.id.alien_profile_friend_type)
        alienprofileblockedtext = findViewById(R.id.alien_profile_blocked_text)

        val ref = FirebaseDatabase.getInstance().getReference("/users/$alienProfileUser")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })


        friendslinecontactbutton.setOnClickListener {
//            val intent =  Intent(this, ContactPersonActivity::class.java)
//            intent.putExtra("profileuid", alienProfileUser)
//            intent.putExtra("profilename", alien_profile_name.text.toString())
//            startActivity(intent)



            val intent = Intent(this, ChatLogActivity::class.java)

            intent.putExtra("USER_KEY", chatPartnerUser)
            intent.putExtra("USER_UID", alienProfileUser)

            startActivity(intent)


        }

//        profilemenu =
//        blockbutton = profilemenu.find
//        reportbutton = findViewById(R.id.action_report)
//        reportbutton.setVisibility(View.GONE)

        addToWishList.setOnClickListener {
            addToWishlist()
        }

        mAdapter = ProfileImageAdapter(mProfileImages)
        recyclerView = findViewById(R.id.profile_images_view_box) as RecyclerView
        recyclerView!!.layoutManager = GridLayoutManager(this, 3)
        recyclerView!!.adapter = mAdapter

        isInYourWishlist()
        fetchprofileuser()
        setFriendshipStatus()
        setupUserBlockedStatus()
        setupYouInBlacklistStatus()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alien_profile_menu, menu);
        profilemenu = menu
        blockbutton = profilemenu!!.findItem(R.id.action_block)
        unblockbutton = profilemenu!!.findItem(R.id.action_unblock)
        return super.onCreateOptionsMenu(menu)



    }
    override fun onStart() {
        super.onStart()
        var mReference = FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().uid);

        mReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var sdf = SimpleDateFormat("yyyy/MM/dd")
                val currentDate = sdf.format(Date());
                println("currnet date " + currentDate)

                var membershipdate : String = p0.child("membershipdate").getValue().toString();
                println("membership date " + membershipdate)
                if(currentDate > membershipdate){
                    membershipStatus = "no"
                    Common.getInstance().membership_status = "no"
                }else{
                    membershipStatus = "yes"
                    Common.getInstance().membership_status = "yes"
                }
                isInYourWishlist()
                fetchprofileuser()
                setFriendshipStatus()
                setupUserBlockedStatus()
                setupYouInBlacklistStatus()

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        var id: Int = item!!.getItemId()



        //noinspection SimplifiableIfStatement
        if (id == R.id.action_block) {
            //item.setVisible(false)
            //Toast.makeText(this, "Action block", Toast.LENGTH_LONG).show()
            addToBalcklistAction()
            return true
        }
        if (id == R.id.action_unblock) {
            //item.setVisible(false)
            //Toast.makeText(this, "Action block", Toast.LENGTH_LONG).show()
            removeFromBalcklistAction()
            return true
        }

        if (id == R.id.action_report) {
//            Toast.makeText(this, "Action report", Toast.LENGTH_LONG).show()
            reportAction()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupUserBlockedStatus(){
        var uid = FirebaseAuth.getInstance().uid
        val alienProfileUserId = alienProfileUser
        var ref = FirebaseDatabase.getInstance().getReference("users-blacklists").child(uid!!).child(alienProfileUserId!!)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var snapValueType = p0.value
                if (snapValueType == null) {
                    print("not in blacklist")


                    blockbutton?.setVisible(true)
                    unblockbutton?.setVisible(false)
                    alienprofileadddeletebutton.setVisibility(View.VISIBLE)
                //    addToWishList.setVisibility(View.VISIBLE)
                    friendslinecontactbutton.setVisibility(View.VISIBLE)
                    alienprofileblockedtext.setVisibility(View.GONE)
//                    inBlacklist = false

                } else {
                    print("in black list")
//                    blockbutton = profilemenu!!.findItem(R.id.action_block)
//                    unblockbutton = profilemenu!!.findItem(R.id.action_unblock)
                    blockbutton?.setVisible(false)
                    unblockbutton?.setVisible(true)
                    alienprofileadddeletebutton.setVisibility(View.GONE)
                    addToWishList.setVisibility(View.GONE)
                    friendslinecontactbutton.setVisibility(View.GONE)
                    alienprofileblockedtext.setVisibility(View.VISIBLE)
//                    inBlacklist = true
//                self.hideUiIfInBlacklist(whoBlacklisted: "user")
//                self.friendText.text = "Blocked"

//                self.friendTextHeightAnchor?.isActive = false
//                self.friendTextHeightAnchor = self.friendText.heightAnchor.constraint(equalToConstant: 20)
//                self.friendTextHeightAnchor?.isActive = true
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun addToBalcklistAction() {
        val builder = AlertDialog.Builder(this)
        val alienProfileUserId = alienProfileUser
        val fromId = FirebaseAuth.getInstance().uid

        builder.setTitle("Deseja bloquear essa pessoa?")
        builder.setMessage("Você não poderá mais ver essa pessoa em:\nProcurar Resultados\nLista de amigos\nLista de amigos de amigos\nLista de Desejos\n\nSe você é amigo, o bloqueio deste usuário também não o fará.")

        builder.setPositiveButton("Sim"){dialog, which ->
            var ref = FirebaseDatabase.getInstance().getReference("users-blacklists")
            var currUserAddToBlacklist = ref.child(fromId!!)
            val map: HashMap<String, Any> = hashMapOf(
                alienProfileUserId!! to 1)
            currUserAddToBlacklist.updateChildren(map)
           setupUserBlockedStatus()

            deleteUserFromEverywhere()

        }
        builder.setNeutralButton("Não"){dialog,which ->
        }

        val dialog: AlertDialog = builder.create()

        //dialog.getWindow().setLayout(600, 400);
        dialog.show()
        var messageView:TextView  = dialog.findViewById(android.R.id.message)!!;
        messageView.setGravity(Gravity.CENTER);
    }

    private fun removeFromBalcklistAction() {
        val builder = AlertDialog.Builder(this)
        val alienProfileUserId = alienProfileUser
        val fromId = FirebaseAuth.getInstance().uid

        builder.setTitle("Desbloquear este usuário?")

        builder.setPositiveButton("YES"){dialog, which ->
            var ref = FirebaseDatabase.getInstance().getReference("users-blacklists")
            var currUserDeleteFromBlacklist = ref.child(fromId!!).child(alienProfileUserId!!)
            currUserDeleteFromBlacklist.removeValue()
           setupUserBlockedStatus()


        }
        builder.setNeutralButton("No"){dialog,which ->
        }

        val dialog: AlertDialog = builder.create()
        //dialog.getWindow().setLayout(600, 400);
        dialog.show()
    }

    private fun reportAction(){
        val builder = AlertDialog.Builder(this)
        val alienProfileUserId = alienProfileUser
        builder.setTitle("Denunciar este usuário?")
        builder.setPositiveButton("SIM") { dialog, which ->
            val intent =  Intent(this, ReportActivity::class.java)
            intent.putExtra("Method", "profile")
            intent.putExtra("ReportUserId", alienProfileUserId)
            startActivity(intent)
        }
        builder.setNeutralButton("Não"){dialog,which ->
        }
        val dialog: AlertDialog = builder.create()
        //dialog.getWindow().setLayout(600, 400);
        dialog.show()

    }

    private fun deleteUserFromEverywhere(){
        var uid = FirebaseAuth.getInstance().uid
        val alienProfileUserId = alienProfileUser


        if (itIsAFriend == true) {
            var ref = FirebaseDatabase.getInstance().getReference("users-friends")
            var currUserDelFriend = ref.child(uid!!).child(alienProfileUserId!!)
            currUserDelFriend.removeValue()
            var friendUserDelCurrent = ref.child(alienProfileUserId!!).child(uid!!)
            friendUserDelCurrent.removeValue()
        } else {
            var refPropRec = FirebaseDatabase.getInstance().getReference("users-friends-proposals").child(uid!!).child("received").child(alienProfileUserId!!)
            refPropRec.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var snapValueType = p0.value
                    if (snapValueType != null) {
                        var ref = FirebaseDatabase.getInstance().getReference("users-friends-proposals")
                        var currUserRemoveFriendProposal =
                            ref.child(uid!!).child("received").child(alienProfileUserId!!)
                        currUserRemoveFriendProposal.removeValue()
//                    print(currUserRemoveFriendProposal)
                        var friendUserRemoveCurrentProposal =
                            ref.child(alienProfileUserId!!).child("sent-to").child(uid!!)
                        friendUserRemoveCurrentProposal.removeValue()
//                    print(friendUserRemoveCurrentProposal)
                    } else {
                        var refPropSent =
                            FirebaseDatabase.getInstance().getReference("users-friends-proposals").child(uid!!)
                                .child("sent-to").child(alienProfileUserId!!)
                        refPropSent.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {

                                var sentSnapValType = p0.value
                                if (sentSnapValType != null) {
                                    var ref = FirebaseDatabase.getInstance().getReference("users-friends-proposals")
                                    var currUserRemoveFriendProposal =
                                        ref.child(uid!!).child("sent-to").child(alienProfileUserId!!)
                                    currUserRemoveFriendProposal.removeValue()
//                            print(currUserRemoveFriendProposal)
                                    var friendUserRemoveCurrentProposal =
                                        ref.child(alienProfileUserId!!).child("received").child(uid!!)
                                    friendUserRemoveCurrentProposal.removeValue()
//                            print(friendUserRemoveCurrentProposal)
                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })
                    }
                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }
//        if (inFoFArray == true) {
//            yourFriendsOfFriendsArray.removeValue(forKey: goToControllerByMemberUid!)
//        }
    }

    private fun setupYouInBlacklistStatus(){
        val builder = AlertDialog.Builder(this)
        val fromId = FirebaseAuth.getInstance().uid
        val alienProfileUserId = alienProfileUser
        var ref = FirebaseDatabase.getInstance().getReference("users-blacklists").child(alienProfileUserId!!).child(fromId!!)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var snapValueType = p0.value
                if (snapValueType != null) {
                    Log.d("blacklist", ""+fromId)
                    Log.d("blacklist", ""+alienProfileUserId)
                    print("You are in blacklist of this user")
                    alienprofileadddeletebutton.setVisibility(View.GONE)
                    addToWishList.setVisibility(View.GONE)
                    friendslinecontactbutton.setVisibility(View.GONE)

//                    youAreInBlacklist = true
//                    self.hideUiIfInBlacklist(whoBlacklisted: "you")
                    builder.setTitle("This user has blacklisted you")
                    builder.setNeutralButton("Ok") { dialog, which ->
                    }
                    val dialog: AlertDialog = builder.create()
                    //dialog.getWindow().setLayout(600, 400);
                    dialog.show()

                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }



    private fun setFriendshipStatus(){
        inFoFArray = false
        var commonFriend = ""
        val fromId = FirebaseAuth.getInstance().uid
        val alienProfileUserId = alienProfileUser
        val friendsRef = FirebaseDatabase.getInstance().getReference("/users-friends/$fromId")
        friendsRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var isfriendval = p0.child(alienProfileUserId!!).value.toString()

                if(isfriendval == "1") {
//                    Log.d("testingfriend", "is your friend")
                    itIsAFriend = true
                }
                if (itIsAFriend == true) {
                    deleteFromFriendsButtonSetup()
                }
                else {
                    //Is not a friend. Lets check if proposal was sent from that user
                    val proposalsRef =
                        FirebaseDatabase.getInstance().getReference("/users-friends-proposals/$fromId/received")
                    proposalsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            var isproposalval = p0.child(alienProfileUserId!!).value.toString()

                            if (isproposalval == "1") {
//                              Log.d("testingfriend", "is your friend")
                                friendshipProposal = "received"
                            }
                            if (friendshipProposal == "received") {
                                acceptFriendshipProposalSetup()
                            } else {
                                //No request was sent. Lets check if user has sent a request to that user
                                val proposalsRef = FirebaseDatabase.getInstance()
                                    .getReference("/users-friends-proposals/$fromId/sent-to")
                                proposalsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(p0: DataSnapshot) {
                                        var isproposalval = p0.child(alienProfileUserId!!).value.toString()

                                        if (isproposalval == "1") {

                                            friendshipProposal = "sent-to"
                                        }
                                        if (friendshipProposal == "sent-to") {
                                            proposalWasSentButtonSetup()
                                        } else if (friendshipProposal == "") {
                                            //Nothing was found. He can add him to friend
                                            addToFriendsButtonSetup()
                                        }

                                    }

                                    override fun onCancelled(p0: DatabaseError) {

                                    }
                                })

                            }

                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }

                    })


                    for ((singleFoFKey, singleFoFValue) in LatestMessagesActivity.yourFriendsOfFriendsArray) {

                        var FoFArray: List<String> =
                            singleFoFValue?.replace("[", "")?.replace("]", "")?.split(",")!!.map { it.trim() }

                        FoFArray.forEach {
                            var singleFoFfriend = it
                            Log.d("foffriendsingleFoFKey: ", singleFoFKey.toString())
                            Log.d("foffriendsingleFoFVal: ", singleFoFfriend.toString())
                            if (singleFoFfriend == alienProfileUserId) {
                                inFoFArray = true
//                            if (singleFoFKey != "friend") {
                                commonFriend = singleFoFKey!!
//                            }
                            }
                        }

                    }
                    Log.d("foffriend: ", inFoFArray.toString())
                    Log.d("foffriendArray: ", LatestMessagesActivity.yourFriendsOfFriendsArray.toString())
                    Log.d("foffriendAlienUID: ", alienProfileUserId.toString())
                    Log.d("foffriendMYUID: ", fromId.toString())


                    if (inFoFArray == true) {

                        var ref = FirebaseDatabase.getInstance().getReference().child("users").child(commonFriend)
                        ref.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                var name = p0.child("name").value.toString()
                                var string =
                                    "<font color='#f7a837'>FRIEND </font><font color='#686868'>OF </font>" + name!!
                                alien_profile_friend_of_friend_type.setText(
                                    Html.fromHtml(string),
                                    TextView.BufferType.SPANNABLE
                                )
                                alien_profile_friend_of_friend_type.setVisibility(View.VISIBLE)


                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })

                    }
                }




            }
            override fun onCancelled(p0: DatabaseError) {

            }

        })



    }

    //****************SETUP BUTTON AND STATUS****************


    private fun deleteFromFriendsButtonSetup() {
        //*** alienprofileadddeletebutton.text = "delete friend"
        //*** alienprofiletype.setVisibility(View.VISIBLE)
        //*** alienprofiletype.text = "FRIEND"

        alienprofileadddeletebutton.setOnClickListener {
            deleteFromFriendsAction()
        }

    }
    private fun acceptFriendshipProposalSetup() {
        //*** alienprofileadddeletebutton.text = "ACCEPT FRIEND"
        //*** alienprofiletype.setVisibility(View.GONE)

        alienprofileadddeletebutton.setOnClickListener {
            acceptFriendshipAction()
        }

    }
    private fun proposalWasSentButtonSetup() {
        //*** alienprofileadddeletebutton.text = "CANCEL REQUEST"
        //*** alienprofiletype.setVisibility(View.GONE)

        alienprofileadddeletebutton.setOnClickListener {
            cancelProposalAction()
        }

    }
    private fun addToFriendsButtonSetup() {
        //*** alienprofileadddeletebutton.text = "ADD FRIEND"
        //*** alienprofiletype.setVisibility(View.GONE)

        alienprofileadddeletebutton.setOnClickListener {
            addFriendAction()
        }

    }

    //****************END SETUP BUTTON AND STATUS****************



    //****************SETUP BUTTON ACTIONS****************

    private fun deleteFromFriendsAction() {
        val alienProfileUserId = alienProfileUser
        val fromId = FirebaseAuth.getInstance().uid

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Excluir amigo?")

        builder.setPositiveButton("Sim"){dialog, which ->

            val ref = FirebaseDatabase.getInstance().getReference("/users-friends/")
            val currUserDelFriend = ref.child(fromId!!).child(alienProfileUserId!!)
            currUserDelFriend.removeValue()
            val friendUserDelCurrent = ref.child(alienProfileUserId!!).child(fromId!!)
            friendUserDelCurrent.removeValue()

            //*** alienprofileadddeletebutton.text = "ADD FRIEND"
            //*** alienprofiletype.setVisibility(View.GONE)

            alienprofileadddeletebutton.setOnClickListener {
                addFriendAction()
            }


        }
        builder.setNeutralButton("No"){dialog,which ->
        }

        val dialog: AlertDialog = builder.create()
        //dialog.getWindow().setLayout(600, 400);
        dialog.show()

        Log.d("testingfriend", "deleteFromFriendsAction")


    }
    private fun acceptFriendshipAction() {
        val alienProfileUserId = alienProfileUser
        val fromId = FirebaseAuth.getInstance().uid
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Aceitar amigo?")

        builder.setPositiveButton("Sim"){dialog, which ->

            val ref = FirebaseDatabase.getInstance().getReference("/users-friends-proposals/")
            val currUserRemoveFriendProposal = ref.child(fromId!!).child("received").child(alienProfileUserId!!)
            currUserRemoveFriendProposal.removeValue()

            val friendUserRemoveCurrentProposal = ref.child(alienProfileUserId!!).child("sent-to").child(fromId!!)
            friendUserRemoveCurrentProposal.removeValue()
            val ref1 = FirebaseDatabase.getInstance().getReference("/users-friends/")
            val currUserAddFriend = ref1.child(fromId!!)
            val map: HashMap<String, Any> = hashMapOf(
                alienProfileUserId to 1)
            currUserAddFriend.updateChildren(map)
            val friendUserAddCurrent = ref1.child(alienProfileUserId!!)
            val friendUserAddCurrentMap: HashMap<String, Any> = hashMapOf(
                fromId to 1)
            friendUserAddCurrent.updateChildren(friendUserAddCurrentMap)

            whriteToUserNotification("Accepted your friend request")
            fetchCurrentUser(alienProfileUserId, "Accepted your friend request", "friend")

            //*** alienprofileadddeletebutton.text = "delete friend"
            //*** alienprofiletype.setVisibility(View.VISIBLE)
            //*** alienprofiletype.text = "FRIEND"

            alienprofileadddeletebutton.setOnClickListener {
                deleteFromFriendsAction()
            }


        }
        builder.setNeutralButton("No"){dialog,which ->
        }

        val dialog: AlertDialog = builder.create()
        //dialog.getWindow().setLayout(600, 400);
        dialog.show()

        Log.d("testingfriend", "acceptFriendshipAction")

    }
    private fun cancelProposalAction() {
        val alienProfileUserId = alienProfileUser
        val fromId = FirebaseAuth.getInstance().uid
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Cancelar pedido?")

        builder.setPositiveButton("Sim"){dialog, which ->
            val ref = FirebaseDatabase.getInstance().getReference("/users-friends-proposals/")
            val currUserRemoveFriendProposal = ref.child(fromId!!).child("sent-to").child(alienProfileUserId!!)
            currUserRemoveFriendProposal.removeValue()
            print(currUserRemoveFriendProposal)
            val friendUserRemoveCurrentProposal = ref.child(alienProfileUserId!!).child("received").child(fromId!!)
            friendUserRemoveCurrentProposal.removeValue()

            //*** alienprofileadddeletebutton.text = "ADD FRIEND"
            //*** alienprofiletype.setVisibility(View.GONE)

            alienprofileadddeletebutton.setOnClickListener {
                addFriendAction()
            }
        }


        builder.setNeutralButton("No"){dialog,which ->
        }

        val dialog: AlertDialog = builder.create()
        //dialog.getWindow().setLayout(600, 400);
        dialog.show()

        Log.d("testingfriend", "cancelProposalAction")
    }
    private fun addFriendAction() {
        val alienProfileUserId = alienProfileUser
        val fromId = FirebaseAuth.getInstance().uid
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Adicionar amigo?")

        builder.setPositiveButton("Sim"){dialog, which ->
            val ref = FirebaseDatabase.getInstance().getReference("/users-friends-proposals/")
            val observe = ref.child(fromId!!)
            var concidence = false
            val preobserve = ref.child(fromId!!)
            val map: HashMap<String, Any> = hashMapOf(
                "temp" to 1)
            preobserve.updateChildren(map)
            observe.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.key == "received") {
                        for (child in p0.children) {
                            val childKey = child.key
                            if (childKey == alienProfileUserId && concidence == false) {
                                concidence = true
                            }
                        }
                    }
                    if (concidence == false) {
                        val currUserAddFriendProposal = ref.child(fromId!!).child("sent-to")
                        val currUserAddFriendProposalMap: HashMap<String, Any> = hashMapOf(
                            alienProfileUserId!! to 1)
                        currUserAddFriendProposal.updateChildren(currUserAddFriendProposalMap)
                        val friendUserAddCurrentProposal = ref.child(alienProfileUserId!!).child("received")
                        val friendUserAddCurrentProposalMap: HashMap<String, Any> = hashMapOf(
                            fromId!! to 1)
                        friendUserAddCurrentProposal.updateChildren(friendUserAddCurrentProposalMap)
                        whriteToUserNotification("Quer ser seu amigo")
                        fetchCurrentUser(alienProfileUserId, "Quer ser seu amigo", "friend")
                    } else {
                        val currUserRemoveFriendProposal = ref.child(fromId!!).child("received").child(alienProfileUserId!!)
                        currUserRemoveFriendProposal.removeValue()
                        val friendUserRemoveCurrentProposal = ref.child(alienProfileUserId!!).child("sent-to").child(fromId!!)
                        friendUserRemoveCurrentProposal.removeValue()
                        val ref1 = FirebaseDatabase.getInstance().getReference("/users-friends/")
                        val currUserAddFriend = ref1.child(fromId!!)
                        val currUserAddFriendMap: HashMap<String, Any> = hashMapOf(
                            alienProfileUserId!! to 1)
                        currUserAddFriend.updateChildren(currUserAddFriendMap)
                        val friendUserAddCurrent = ref1.child(alienProfileUserId!!)
                        val friendUserAddCurrentMap: HashMap<String, Any> = hashMapOf(
                            fromId!! to 1)
                        friendUserAddCurrent.updateChildren(friendUserAddCurrentMap)
                        whriteToUserNotification("Aceitou seu pedido de amizade")
                        fetchCurrentUser(alienProfileUserId, "Aceitou seu pedido de amizade", "friend")

                    }
                    val removeTemp = ref.child(fromId!!).child("temp")
                    removeTemp.removeValue()

                    //*** alienprofileadddeletebutton.text = "CANCEL REQUEST"
                    //*** alienprofiletype.setVisibility(View.GONE)

                    alienprofileadddeletebutton.setOnClickListener {
                        cancelProposalAction()
                    }

                }
                override fun onCancelled(p0: DatabaseError) {

                }
                })


                }

        builder.setNeutralButton("No"){dialog,which ->
        }

        val dialog: AlertDialog = builder.create()
        //dialog.getWindow().setLayout(600, 400);
        dialog.show()

        Log.d("testingfriend", "addFriendAction")

    }

    private fun fetchCurrentUser(alienProfileUserId: String, text: String, type: String){
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

    private fun sendNotification(notification: JSONObject) {

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

    private fun addToWishlist(){
        //checkIfYouAreBanned()
        val addItToWishlistByUid = alienProfileUser
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-wishlists/")
        val usersReference = ref.child(fromId!!)
        val addItToWishlist: HashMap<String, Any> = hashMapOf(
            addItToWishlistByUid!! to 1)
        usersReference.updateChildren(addItToWishlist)
        val ref1 = FirebaseDatabase.getInstance().getReference("/users-likes/")
        val likesReference = ref1.child(addItToWishlistByUid!!)
        val currUserliked: HashMap<String, Any> = hashMapOf(
            fromId!! to 1)
        likesReference.updateChildren(currUserliked)
        whriteToUserNotification("Gostou de você")
        fetchCurrentUser(addItToWishlistByUid, "Chat with", "likeyou")
       // self.sentProposalNotification(toId: goToControllerByMemberUid!, fromId: uid, text: self.notifLike)
        hidAddToWishlistButton()
//        print("Add to wishlist")
//        inWishList = true
    }

    private fun whriteToUserNotification(text: String) {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-notifications/")
        val notificationRef = ref.child(alienProfileUser!!).push()
        val timestamp = System.currentTimeMillis()/1000
        val values: java.util.HashMap<String, Any?> = hashMapOf(
            "text" to text,
            "timestamp" to timestamp,
            "senderUid" to fromId
        )
        notificationRef.updateChildren(values)
    }

    //****************END SETUP BUTTON ACTIONS****************

    private fun isInYourWishlist(){
        val fromId = FirebaseAuth.getInstance().uid
        val alienProfileUserId = alienProfileUser
        val ref = FirebaseDatabase.getInstance().getReference("/users-wishlists/").child(fromId!!)
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
    private fun hidAddToWishlistButton(){
        bt_like.setVisibility(View.GONE)
    }

    private fun fetchprofileuser() {

        val alienProfileUserId = alienProfileUser
        val ref = FirebaseDatabase.getInstance().getReference("/users/$alienProfileUserId")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                try
                {
                    currentUserProfile = p0.getValue(User::class.java)

                    alien_toolbar_profilename.text = currentUserProfile?.name + " " + currentUserProfile?.age

                    Glide.with(alien_profile_activity_image).load(currentUserProfile?.profileImageUrl)
                        .placeholder(R.drawable.progress_animation)
                        .error(R.drawable.branco).into(alien_profile_activity_image)

                    alien_profile_name.text = currentUserProfile?.name + " " + currentUserProfile?.age
                    alien_profile_activity_location.text = currentUserProfile?.loc

                    try {
                        if (currentUserProfile?.disconectTime!! > -99999999999999 && currentUserProfile?.disconectTime!! < 99999999999999) {
                            alien_profile_activity_time.text = currentUserProfile?.disconectTime?.let { "Ultima atividade " + getTimeString(it) }

                        } else {
                            alien_profile_activity_time.setVisibility(View.GONE)
                        }
                    } catch (exception: KotlinNullPointerException) {
                        alien_profile_activity_time.setVisibility(View.GONE)
                    }



                    if (currentUserProfile?.occupation == ""){
                        alien_profile_activity_occupation.setVisibility(View.GONE)
                    }
                    else {
                        alien_profile_activity_occupation.text = currentUserProfile?.occupation
                    }

                    if (currentUserProfile?.currentLoc == ""){
                        alien_profile_activity_current_location.setVisibility(View.GONE)
                    }
                    else {
                        alien_profile_activity_current_location.text = "Localização atual: "+currentUserProfile?.currentLoc
                    }


    //                Log.d("alienprofile", ""+currentUserProfile?.currentLoc)

                    alien_profile_activity_offerservicetext.text = currentUserProfile?.aboutServices
                    alien_profile_activity_about_me_text.text = currentUserProfile?.aboutMe
                    alien_profile_activity_about_my_place_text.text = currentUserProfile?.aboutMyPlace

                    if(currentUserProfile?.aboutMyPlace.equals(""))
                    {
                        alien_profile_activity_about_my_place.visibility = View.GONE
                        alien_profile_activity_about_my_place_text.visibility = View.GONE
                    }

                    if(currentUserProfile?.aboutServicesEnabled == true && currentUserProfile?.offerServiseChecked == true) {

                        alien_profile_activity_offerservice.setVisibility(View.VISIBLE)
                        alien_profile_activity_offerservicetext.setVisibility(View.VISIBLE)

                    }
                    else {
                        alien_profile_activity_offerservice.setVisibility(View.GONE)
                        alien_profile_activity_offerservicetext.setVisibility(View.GONE)
                    }

                    if(currentUserProfile?.meetChecked == true) {

                        alien_profile_activity_meeting.text = "Fazer amizades"

                    }
                    else {
                        alien_profile_activity_meeting.setVisibility(View.GONE)
                    }
                    if(currentUserProfile?.dateChecked == true) {

                        alien_profile_activity_dating.text = "Paquera"

                    }
                    else {
                        alien_profile_activity_dating.setVisibility(View.GONE)
                    }
                    if(currentUserProfile?.acceptingGuests == "maybe" || currentUserProfile?.acceptingGuests == "yes") {

                        alien_profile_activity_guestaccept.text = "Sair com os amigos"

                    }
                    else {
                        alien_profile_activity_guestaccept.setVisibility(View.GONE)
                    }

                    mProfileImages.add(currentUserProfile?.profileImageUrl!!)

                    val image_ref = FirebaseDatabase.getInstance().getReference("/user-profile-images/$alienProfileUserId")
                    image_ref.addChildEventListener(object: ChildEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Log.d("Receive notification", "error")
                        }

                        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                            mProfileImages.add(p0.value.toString())
                            recyclerView?.adapter = mAdapter
                        }
                        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                        }

                        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                        }

                        override fun onChildRemoved(p0: DataSnapshot) {

                        }
                    })
                }
                catch(e : java.lang.Exception)
                {

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })
    }


}
