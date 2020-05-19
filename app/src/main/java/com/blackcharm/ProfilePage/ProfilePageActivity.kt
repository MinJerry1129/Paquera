package com.blackcharm.ProfilePage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.blackcharm.R
import com.blackcharm.bottom_nav_pages.*
import kotlinx.android.synthetic.main.activity_latest_messages.*

class ProfilePageActivity : AppCompatActivity() {
    private lateinit var viewpager: ViewPager
    var active = LatestMessagesFragment;

//    companion object {
//        var currentUser: User? = null
//        var messagelast: String? = null
//        var counter: Int? = 1;
//        var final: Int? = 0;
//
//    }
//
//    lateinit var toolbarcontact: Button
//    lateinit var toolbarsignout: Button
//
//    //var chatMessage: ChatMessage? = null
//
//    val TAG = "LatestMessages"

    //*************************For bottom nav bar****************************
//    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item->
//        when(item.itemId){
//            R.id.bottomnav_messages -> {
//                Log.d("BottomNav","messages")
//                replaceFragment(LatestMessagesFragment())
//                return@OnNavigationItemSelectedListener true
//
//            }
//            R.id.bottomnav_searchline -> {
//                Log.d("BottomNav","friendsline")
//                replaceFragment(FriendsLineFragment())
//                return@OnNavigationItemSelectedListener true
//
//            }
//            R.id.bottomnav_search -> {
//                Log.d("BottomNav","search")
//                replaceFragment(SearchFragment())
//                return@OnNavigationItemSelectedListener true
//
//            }
//            R.id.bottomnav_profile -> {
//                Log.d("BottomNav","search")
//
//                return@OnNavigationItemSelectedListener true
//
//            }
//            R.id.bottomnav_notifications -> {
//                Log.d("BottomNav","search")
//
//                return@OnNavigationItemSelectedListener true
//
//            }
//        }
//        false
//
//    }



    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item->
        when(item.itemId){
            R.id.bottomnav_messages -> {
                Log.d("BottomNav","messages")
                viewpager.setCurrentItem(0)
                bottomNavigation.menu.getItem(0).isChecked = true
            }
            R.id.bottomnav_searchline -> {
                Log.d("BottomNav","friendsline")
                viewpager.setCurrentItem(1)
                bottomNavigation.menu.getItem(1).isChecked = true
            }
            R.id.bottomnav_search -> {
                Log.d("BottomNav","search")
                viewpager.setCurrentItem(2)
                bottomNavigation.menu.getItem(2).isChecked = true


            }
            R.id.bottomnav_profile -> {
                Log.d("BottomNav","profile")
                viewpager.setCurrentItem(3)
                bottomNavigation.menu.getItem(3).isChecked = true



            }
            R.id.bottomnav_notifications -> {
                Log.d("BottomNav","notifications")
                viewpager.setCurrentItem(4)
                bottomNavigation.menu.getItem(4).isChecked = true



            }
        }

        false

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

//        recyclerview_latest_messages.adapter = adapter
//        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
//
//        adapter.setOnItemClickListener { item, view ->
//            val intent = Intent(this, ChatLogActivity::class.java)
//            val row = item as LatestMessageRow
//            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
//            startActivity(intent)
//        }
//
//        // Set the toolbar as support action bar
//        setSupportActionBar(toolbar)
//
//        // Now get the support action bar
//        val actionBar = supportActionBar
//
//        // Set toolbar title/app title
//        actionBar!!.title = ""
//
//        // Set action bar/toolbar sub title
//        //actionBar.subtitle = "App subtitle"
//
//        // Set action bar elevation
//        actionBar.elevation = 4.0F
//
//        // Display the app icon in action bar/toolbar
//        actionBar.setDisplayShowHomeEnabled(false)
//        //actionBar.setLogo(R.mipmap.ic_launcher)
//        //actionBar.setDisplayUseLogoEnabled(true)
//
//        toolbarcontact = toolbar.findViewById(R.id.toolbar_contacts)
//        toolbarsignout = toolbar.findViewById(R.id.toolbar_signout)
//
//        toolbarcontact.setOnClickListener {
//            val intent = Intent(this, NewMessageActivity::class.java)
//            startActivity(intent)
//        }
//
//        toolbarsignout.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            val intent =  Intent(this, RegisterLoginActivityMain::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }


        //*************************For bottom nav bar****************************
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
//
//        val menu = bottomNavigation.menu
//        menu.findItem(R.id.bottomnav_messages).setIcon(R.drawable.icon_hearts)
//        println("adasdadfadfafdad")


//        replaceFragment(LatestMessagesFragment())

//        fetchCurrentUser()
//
//        veryfyUserLoggedIn()
//
//        listenForLatestMessages()

        initViews()

        setupViewPager()

    }

    private fun initViews() {

        viewpager = findViewById(R.id.viewpager)
        viewpager.beginFakeDrag()

    }
    private fun setupViewPager() {

        val adapter = BottomPagerAdapter(getSupportFragmentManager())

        var firstFragmet = LatestMessagesFragment()
        var secondfragment = FriendsLineFragment()
        var thirdfragment = SearchFragment()
        var fourthfragment = ProfileFragment()
        var fifthfragment = NotificationsFragment()

        adapter.addFragment(firstFragmet, "ONE")
        adapter.addFragment(secondfragment, "two")
        adapter.addFragment(thirdfragment, "three")
        adapter.addFragment(fourthfragment, "four")
        adapter.addFragment(fifthfragment, "five")
        viewpager!!.adapter = adapter
    }

//    val latestmessagesMap = HashMap<String, ChatMessage>()
//
//    private fun refreshRecyclerViewMessages() {
//        adapter.clear()
//        latestmessagesMap.values.forEach {
//            adapter.add(LatestMessageRow(it))
//        }
//    }
//
//    private fun listenForLatestMessages() {
//        val fromId = FirebaseAuth.getInstance().uid
//        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId")
//
//        ref.addChildEventListener(object: ChildEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                fetchLastMessageId(p0, fromId)
//
//            }
//
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//
//            }
//
//            override fun onChildRemoved(p0: DataSnapshot) {
//
//            }
//        })
//    }
//
//    private fun fetchLastMessageId(p0: DataSnapshot, fromId: String?) {
//        val userId = p0.key
//        Log.d("userId", "$userId")
//        val ref1 = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$userId").limitToLast(1)
//
//        ref1.addChildEventListener(object: ChildEventListener {
//
//            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                fetchLastMessage(p0)
//            }
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//                fetchLastMessage(p0)
//                Log.d(TAG,"changing")
//            }
//
//            override fun onCancelled(p0: DatabaseError) {}
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
//            override fun onChildRemoved(p0: DataSnapshot) {}
//        })
//    }
//
//    private fun fetchLastMessage(p0: DataSnapshot) {
//        val messageId = p0.key
//        Log.d("messageId", "$messageId")
//        val ref2 = FirebaseDatabase.getInstance().getReference("/messages/$messageId")
//
//        ref2.addListenerForSingleValueEvent(object: ValueEventListener {
//
//            override fun onDataChange(p0: DataSnapshot) {
////              final = final?.plus(1)
////              Log.d(TAG, "$final")
////              val count = p0.getChildrenCount() -1;
//
//                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
//
//                latestmessagesMap[chatMessage.toId] = chatMessage
//                refreshRecyclerViewMessages()
//
//                //adapter.add(LatestMessageRow(chatMessage))
//
//                messagelast = (chatMessage?.text)
//
//                Log.d(TAG, "$messagelast")
//            }
//            override fun onCancelled(p0: DatabaseError) {}
//        })
//
//
//    }
//
//    val adapter = GroupAdapter<ViewHolder>()
//
//
//    private fun fetchCurrentUser(){
//        val uid = FirebaseAuth.getInstance().uid
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        ref.addListenerForSingleValueEvent(object: ValueEventListener{
//            override fun onDataChange(p0: DataSnapshot) {
//                currentUser = p0.getValue(User::class.java)
//
//            }
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }
//
//    private fun veryfyUserLoggedIn() {
//        val uid = FirebaseAuth.getInstance().uid
//        if (uid == null) {
//            val intent =  Intent(this, RegisterLoginActivityMain::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }
//    }
//
//
//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when (item?.itemId) {
//            R.id.menu_new_message -> {
//                val intent = Intent(this, NewMessageActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.menu_sign_out -> {
//                FirebaseAuth.getInstance().signOut()
//                val intent =  Intent(this, RegisterLoginActivityMain::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }


    //*************************For bottom nav bar****************************
//    private fun replaceFragment(fragment: Fragment) {
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//
//        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
//        fragmentTransaction.commit()
//    }





//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.nav_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu to use in the action bar
//        val inflater = menuInflater
//        inflater.inflate(R.menu.toolbar_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
}
