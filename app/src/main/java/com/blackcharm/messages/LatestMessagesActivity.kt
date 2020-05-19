package com.blackcharm.messages

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.android.material.badge.BadgeDrawable

import com.blackcharm.bottom_nav_pages.ProfileFragment
import com.blackcharm.R
import com.blackcharm.bottom_nav_pages.*
import kotlinx.android.synthetic.main.activity_latest_messages.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.blackcharm.models.User
import com.blackcharm.registerlogin.TermsScreen
import info.androidhive.fontawesome.FontDrawable
import java.util.*
import kotlin.collections.HashMap


class LatestMessagesActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private lateinit var viewpager: ViewPager
    var firstFragmet = FriendsLineFragmentEmpty()
    var secondfragment = LatestMessagesFragment()
    var thirdfragment = SearchFragmentEmpty()
    var fourthfragment = ProfileFragment()
    var fifthfragment = NotificationsFragment()

    var searchScreen = SearchFragment()

    var allContactsListArray: ArrayList<String> = ArrayList()
    var yourBlacklist: ArrayList<String> = ArrayList()
    var users: ArrayList<String> = ArrayList()


    private val listener: com.google.android.gms.location.LocationListener? = null
    private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocation: Location? = null
    private var mLocationManager: LocationManager? = null
    private var locationManager: LocationManager? = null
    private var mLocationRequest: LocationRequest? = null
    lateinit  var geocoder: Geocoder
    var currentUser: User? = null
    var currentUserLocationGPS: String? = ""
    var select_msg = 0;
    var select_not = 0;

    //var registerverify:String? =""


    private val isLocationEnabled: Boolean
        get() {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    var active = LatestMessagesFragment;

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item->
        when(item.itemId){
            R.id.bottomnav_searchline -> {
                Log.d("BottomNav","friendsline")
                viewpager.setCurrentItem(0)
                bottomNavigation.menu.getItem(0).isChecked = true
                renderMenuIcons(bottomNavigation.menu, 0)
                select_msg=0
                select_not = 0
//                bottomNavigation.menu.getItem(0).setIcon(R.drawable.icon_hearts)
            }
            R.id.bottomnav_messages -> {
                Log.d("BottomNav","messages")
                viewpager.setCurrentItem(1)
                bottomNavigation.menu.getItem(1).isChecked = true
                renderMenuIcons(bottomNavigation.menu, 1)
                select_msg=1
                select_not = 0
            }
            R.id.bottomnav_search -> {
                Log.d("BottomNav","search")
                viewpager.setCurrentItem(2)
                bottomNavigation.menu.getItem(2).isChecked = true
                renderMenuIcons(bottomNavigation.menu, 2)

                thirdfragment.onResume1()
                select_msg=0
                select_not = 0
//                searchScreen.onResume1()

            }
            R.id.bottomnav_profile -> {
                Log.d("BottomNav","profile")
                viewpager.setCurrentItem(3)
                bottomNavigation.menu.getItem(3).isChecked = true
                renderMenuIcons(bottomNavigation.menu, 3)
                select_msg=0
                select_not = 0


            }
            R.id.bottomnav_notifications -> {
                Log.d("BottomNav","notifications")
                viewpager.setCurrentItem(4)
                bottomNavigation.menu.getItem(4).isChecked = true
                renderMenuIcons(bottomNavigation.menu, 4)
                select_msg=0
                select_not = 1

            }
        }

        false

    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val listPermissionsNeeded = ArrayList<String>()


        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

    fun renderMenuIcons(menu: Menu, indexSelect: Int) {

        for (x in 0 until menu.size())
            setMenuIcon(menu, x, ContextCompat.getColor(this, if (indexSelect == x) R.color.menu_icon_yellow else R.color.white))
    }

    fun setMenuIcon(menu: Menu, menuIndex: Int, color: Int) {

        val icons = intArrayOf(R.string.fa_home_solid, R.string.fa_envelope, R.string.fa_search_solid, R.string.fa_user, R.string.fa_bell)
        val menuItem = menu.getItem(menuIndex)
        val drawable = FontDrawable(this, icons[menuIndex], true, false)
        drawable.setTextColor(color)
        drawable.setTextSize(14f)
        menuItem.setIcon(drawable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)


        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
            Handler().postDelayed({
                continueOnCreate()
            }, SPLASH_TIME_OUT.toLong())
        }
        takeAllContactsList()

        renderMenuIcons(bottomNavigation.menu, 0)
        listenForLatestMessages()
        listenForNotifications()
    }


    fun continueOnCreate() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        Log.d("gggg","uooo");
        checkLocation()
        geocoder = Geocoder(this, Locale.ENGLISH);

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("notific", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                writeNewDeviceId(token)

                Log.d("notific", token)
            })

        newSigninActions()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "Notification type"
            val descriptionText = "Notification type"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel("the first channel", name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }


        //*************************For bottom nav bar****************************
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        initViews()

        setupViewPager()
        getCurrentUserLocation()


        if (intent.getBooleanExtra("isFirstVisit", true))
            fetchFoFList()
        else {
            goToFoFList()
        }
    }

    override fun onResume(){
        super.onResume();

        newSigninActions()
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId")


        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                if(select_msg==0){
                    val drawable = FontDrawable(baseContext, R.string.fa_envelope, true, false)
                    drawable.setTextColor(ContextCompat.getColor(baseContext,R.color.colorAlet))
                    drawable.setTextSize(14f)
                    bottomNavigation.menu.getItem(1).setIcon(drawable)
                }

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun listenForNotifications() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-notifications/$fromId")

        Log.d("Receivenotification", "listenForNotifications $fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Receive notification", "error")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                if(select_not==0){
                    val drawable = FontDrawable(baseContext, R.string.fa_bell, true, false)
                    drawable.setTextColor(ContextCompat.getColor(baseContext,R.color.colorAlet))
                    drawable.setTextSize(14f)
                    bottomNavigation.menu.getItem(4).setIcon(drawable)
                }


//                val children = p0!!.children
//                children.forEach {
//
//                }


            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }



        })
    }

    fun newSigninActions() {
        chekIfUserDeleteAccount()
        setActiveDisconectStatus()
    }

    private fun setActiveDisconectStatus() {
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var disconectTimeRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid!!).child("disconectTime")
        disconectTimeRef.onDisconnect().setValue(ServerValue.TIMESTAMP)
        var presenceRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid!!).child("isActive")
        presenceRef.onDisconnect().setValue(false)
        presenceRef.setValue(true)

    }

    private fun chekIfUserDeleteAccount() {
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid!!).child("wasDeleted")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.value == "bySelf") {
                    val builder = AlertDialog.Builder(this@LatestMessagesActivity)

                    builder.setTitle("Esta conta foi excluída")

                    builder.setMessage("Deseja restaurá-lo?")

                    builder.setPositiveButton("Sim") { dialog, which ->


                        val uid = FirebaseAuth.getInstance().uid ?: ""
                        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

                        ref.addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {

                                val arcAvatar = p0.child("arcProfileImageUrl").value.toString()
                                val arcHomeLocation = p0.child("arcLoc").value.toString()

                                val map: HashMap<String, Any> = hashMapOf(
                                    "profileImageUrl" to arcAvatar,
                                    "loc" to arcHomeLocation
                                )

                                ref.updateChildren(map)
                                ref.child("wasDeleted").removeValue()
                            }
                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })




                    }

                    builder.setNeutralButton("No"){dialog,which ->
                        FirebaseAuth.getInstance().signOut()
                        val intent =  Intent(this@LatestMessagesActivity, TermsScreen::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }

                    val dialog: AlertDialog = builder.create()
                    //dialog.getWindow().setLayout(600, 400);
                    dialog.show()

                } else if (p0.value == "byAdministration") {
                    val builder = AlertDialog.Builder(this@LatestMessagesActivity)

                    builder.setTitle("Você foi banido de Paquera")

                    builder.setNeutralButton("Ok"){dialog,which ->
                        FirebaseAuth.getInstance().signOut()
                        val intent =  Intent(this@LatestMessagesActivity, TermsScreen::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
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

    private fun takeAllContactsList() {
        allContactsListArray = ArrayList()
        var uid = FirebaseAuth.getInstance().currentUser?.uid

        yourBlacklist = ArrayList()
//        print("=======TakeAllContacts=====")
        var blacklistRef = FirebaseDatabase.getInstance().getReference().child("users-blacklists").child(uid!!)

        blacklistRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (blockedUser in p0.children) {
                    yourBlacklist.add(blockedUser.key.toString())
                }
                var yourFriendsArray: ArrayList<String> = ArrayList()
                var yourFriends = FirebaseDatabase.getInstance().getReference().child("users-friends").child(uid)

                yourFriends.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {

                        var i = p0.childrenCount
                        for (yourFriend in p0.children) {
                            var friendUid = yourFriend.key
//                    print("friendUid: ", friendUid)
                            yourFriendsArray.add(friendUid.toString())
                            var yourFriendRef =
                                FirebaseDatabase.getInstance().getReference().child("users-friends")
                                    .child(friendUid!!)
                            yourFriendRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(p0: DataSnapshot) {
                                    var list = ArrayList<String>()


//                        print("CHCount: ",snapshot.children)
                                    for (child in p0.children) {
                                        var friendOfFriendUid = child.key
                                        list.add(friendOfFriendUid!!)
                                        yourFriendsOfFriendsArray.put(friendUid, list.toString())
                                    }
                                    i = i - 1
                                    if (i == 0L) {
                                        for (singleFriend in yourFriendsArray) {
                                            yourFriendsOfFriendsArray.put("friend", singleFriend)
                                        }
                                        yourFriendsOfFriendsArray.remove(uid)
                                        for (blacklistItem in yourBlacklist) {
                                            yourFriendsOfFriendsArray.remove(blacklistItem)
                                        }
                                        var i = yourFriendsOfFriendsArray.size
                                        for ((friendOfFriendKey, friendOfFriendValue) in yourFriendsOfFriendsArray) {
                                            var ref = FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(friendOfFriendKey!!)


                                            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onDataChange(p0: DataSnapshot) {

                                                    i = i - 1

                                                    var singleUserArray: HashMap<Any?, String?> = hashMapOf()
                                                    var searchResultName = p0.child("name").value.toString()
                                                    var searchResultProfileImageUrl =
                                                        p0.child("profileImageUrl").value.toString()
                                                    var userId = p0.key
                                                    if (friendOfFriendValue == "friend") {
                                                        singleUserArray.put(true, "isYourFriend")
                                                    } else {
                                                        singleUserArray.put(false, "isYourFriend")
                                                    }
                                                    singleUserArray.put(searchResultName!!, "name")
                                                    singleUserArray.put(
                                                        searchResultProfileImageUrl!!,
                                                        "profileImageUrl"
                                                    )
                                                    singleUserArray.put(userId, "userId")
                                                    users.add(singleUserArray.toString())
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
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }
                })
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun writeNewDeviceId(token: String?) {
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var ref = FirebaseDatabase.getInstance().getReference().child("users").child(uid!!)
        val map: HashMap<String, Any?> = hashMapOf(
            "fromDevice" to token)
        ref.updateChildren(map)
    }

    private fun getCurrentUserLocation(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$fromId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for(profilesSnapShot in p0.children) {
                    currentUser = p0.getValue(User::class.java)
                    currentUserLocationGPS = currentUser?.currentLoc
                   // Log.d("currentLoc", ""+currentUserLocationGPS)
                    Log.d("currentLoc", ""+currentUserLocationGPS)
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    //  -----------------------------------------
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED

                // Fill with actual results from user
                if (grantResults.size > 0) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]

                    // Check for both permissions
                    if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "location services permission granted")
                        // process the normal flow
                        continueOnCreate()
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ")

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("Service Permissions are required for this app",
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                        DialogInterface.BUTTON_NEGATIVE ->
                                            // proceed with logic by disabling the related features or quit the app.
                                            finish()
                                    }
                                })
                        } else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }

                    }
                }


            }
        }
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    private fun explain(msg: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(msg)
            .setPositiveButton("Yes") { paramDialogInterface, paramInt ->
                //  permissionsclass.requestPermission(type,code);
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.myfriendsroomlimited")))
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> finish() }
        dialog.show()
    }
//  -----------------------------------------

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {

        startLocationUpdates()

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (mLocation == null) {
            startLocationUpdates()
        }

    }

    override fun onConnectionSuspended(i: Int) {
        Log.i(TAG, "Connection Suspended")
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode())
    }

    override fun onStart() {
        super.onStart()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient!!.isConnected()) {
            mGoogleApiClient!!.disconnect()
        }
    }

    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL)
            .setFastestInterval(FASTEST_INTERVAL)

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
            mLocationRequest, this)
        Log.d("reque", "--->>>>")
    }

    override fun onLocationChanged(location: Location) {

        val msg = "Updated Location: " +
                java.lang.Double.toString(location.latitude) + "," +
                java.lang.Double.toString(location.longitude)

        var addresses: List<Address>  = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if (addresses.isEmpty()) {
        }
        else {
            var currentLocation: String? = addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName()

            if(currentUserLocationGPS == currentLocation && currentUserLocationGPS != null){
//                Log.d("checkissue", "inside if")
//                Log.d("checkissue", ""+currentUserLocationGPS)
//                Log.d("checkissue", ""+currentLocation)

            }
            else {
                val fromId = FirebaseAuth.getInstance().uid
                var ref = FirebaseDatabase.getInstance().getReference("users").child(fromId!!).child("currentLoc")
                ref.setValue(currentLocation)
                getCurrentUserLocation()

            }
//            Log.d("location", "--->>>>" + addresses)
//            Log.d("location", "--->>>>" + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName())
        }

//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        // You can now create a LatLng Object for use with maps
        val latLng = LatLng(location.latitude, location.longitude)
        //Log.d("location", "--->>>>"+latLng)
    }

    private fun checkLocation(): Boolean {
        if (!isLocationEnabled)
            showAlert()
        return isLocationEnabled
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Habilitar localização")
            .setMessage("Suas configurações de locais estão definidas como 'Desativadas'.\nAtive o local para usar este aplicativo")
            .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
        dialog.show()
    }

    companion object {

        var yourFriendsOfFriendsArray: MutableMap<String?, String?> = hashMapOf()

        private val TAG = "MainActivity"
        var contactlist: MutableList<String?> = ArrayList()

        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        private val SPLASH_TIME_OUT = 2000
    }


    private fun initViews() {

        viewpager = findViewById(R.id.viewpager)
        viewpager.setOffscreenPageLimit(10)
        viewpager.beginFakeDrag()

    }

    fun getPager(): ViewPager  {
        bottomNavigation.menu.getItem(0).isChecked = true
        renderMenuIcons(bottomNavigation.menu, 0)
        return viewpager
    }

    private fun setupViewPager() {

        val adapter = BottomPagerAdapter(getSupportFragmentManager())



        adapter.addFragment(firstFragmet, "ONE")
        adapter.addFragment(secondfragment, "two")
        adapter.addFragment(thirdfragment, "three")
        adapter.addFragment(fourthfragment, "four")
        adapter.addFragment(fifthfragment, "five")




        viewpager!!.adapter = adapter

    }



    private fun goToFoFList() {


        (this).getPager().setCurrentItem(0)
        bottomNavigation.menu.getItem(0).isChecked = true
        renderMenuIcons(bottomNavigation.menu, 0)
    }






    private fun fetchFoFList() {

        (this).getPager().setCurrentItem(2)
        bottomNavigation.menu.getItem(2).isChecked = true
        renderMenuIcons(bottomNavigation.menu, 2)

        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-friends/$fromId")
        contactlist.clear()

        //Log.d("Contactpage2", "All contact list function")

//        Log.d("Receivenotification", "listenForNotifications $fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Receive notification", "error")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                contactlist.add(p0.key)

                val ref1 = FirebaseDatabase.getInstance().getReference("/users-friends/"+p0.key)


                //Log.d("Contactpage2", "Trying to fetch friends of friends")

                ref1.addChildEventListener(object: ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("Receive notification", "error")
                    }

                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                        //Log.d("Contactpage2", "Fetching")

                        if(p0.key != fromId) {
                            if(p0.key !in contactlist){
                                contactlist.add(p0.key)
                            }
                        }
                    }
                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                    }

                    override fun onChildRemoved(p0: DataSnapshot) {

                    }

                })

            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }



        })
    }




}



