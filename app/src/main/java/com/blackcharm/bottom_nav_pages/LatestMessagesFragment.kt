package com.blackcharm.bottom_nav_pages


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.blackcharm.R
import com.blackcharm.messages.NewMessageActivity
import com.blackcharm.models.ChatMessage
import com.blackcharm.models.User
import com.blackcharm.registerlogin.RegisterLoginActivityMain
import com.blackcharm.registerlogin.TermsScreen
import com.blackcharm.views.LatestMessageRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_latest_messages.*


class LatestMessagesFragment : Fragment() {

    companion object {
        var currentUser: User? = null
        var messagelast: String? = null
        var counter: Int? = 1;
        var final: Int? = 0;
        var messagecount = 0

    }

    lateinit var toolbarcontact: Button
    lateinit var toolbarsignout: Button

    //var chatMessage: ChatMessage? = null

    val TAG = "LatestMessages"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_latest_messages, container, false)

        var recyclerview_latest_messages = view.findViewById<RecyclerView>(R.id.recyclerview_latest_messages)

        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL))

//        adapter.setOnItemClickListener { item, view ->
//            val intent = Intent(getActivity(), ChatLogActivity::class.java)
//            val row = item as LatestMessageRow
//            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
//
//            startActivity(intent)
//        }

        // Set the toolbar as support action bar

        (activity as AppCompatActivity).setSupportActionBar(profile_latest_messages_toolbar)
        //setSupportActionBar(toolbar)

        // Now get the support action bar
        val actionBar = (activity as AppCompatActivity).supportActionBar

        // Set toolbar title/app title
        //actionBar!!.title = ""

        // Set action bar/toolbar sub title
        //actionBar.subtitle = "App subtitle"

        // Set action bar elevation
        //actionBar.elevation = 4.0F

        // Display the app icon in action bar/toolbar
        //actionBar.setDisplayShowHomeEnabled(false)
        //actionBar.setLogo(R.mipmap.ic_launcher)
        //actionBar.setDisplayUseLogoEnabled(true)

        toolbarcontact = view.findViewById(R.id.toolbar_contacts)
        toolbarsignout = view.findViewById(R.id.toolbar_signout)

//        toolbarcontact.setOnClickListener {
//            val intent = Intent(getActivity(), NewMessageActivity::class.java)
//            startActivity(intent)
//        }

        toolbarcontact.setOnClickListener {
            val intent = Intent(getActivity(), ContactsActivity::class.java)
            startActivity(intent)
        }

        toolbarsignout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent =  Intent(getActivity(), TermsScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        fetchCurrentUser()

        veryfyUserLoggedIn()

        listenForLatestMessages()
        return view
    }

    val latestmessagesMap = HashMap<String, ChatMessage>()

//    private fun refreshRecyclerViewMessages(messagecountfinal: Int) {
    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        var latestmessagesMapRow = latestmessagesMap.entries.sortedWith(compareBy { it.value.timestamp }).reversed()

//        Log.d("SENDMESSAGE", "==========================AFTER THE SORT============================")
//        Log.d("SENDMESSAGE", ""+latestmessagesMapRow)

    for (obj in latestmessagesMapRow) {

       // latestmessagesMap.values.forEach {
//            Log.d("SENDMESSAGE", ""+obj)
        //Log.d("thisthisthis", ""+messagecountfinal)
//            adapter.add(LatestMessageRow(context!!, it, messagecountfinal))
            adapter.add(LatestMessageRow(context!!, obj.value))





        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId")


        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                fetchLastMessageId(p0, fromId)
                //fetchmessagecount(p0, fromId)


            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                fetchLastMessageId(p0, fromId)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun fetchmessagecount(p0: DataSnapshot, fromId: String?) {
        val userId = p0.key
        messagecount = 0
        Log.d("userId", "$userId")

        val ref1 = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$userId")

        ref1.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("messagecount", ""+p0.value)

                for(singlecount in p0.children) {
                    if(singlecount.value.toString().toInt() == 1){
                        Log.d("messagecount", "hegjrhgew")
                        messagecount += 1
                    }
                    Log.d("thisthisthis", ""+userId)
                    Log.d("thisthisthis", ""+messagecount)
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun fetchLastMessageId(p0: DataSnapshot, fromId: String?) {
        val userId = p0.key
        Log.d("userId", "$userId")

        val ref1 = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$userId").limitToLast(1)

        ref1.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                fetchLastMessage(p0)
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                fetchLastMessage(p0)
                Log.d(TAG,"changing")
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun fetchLastMessage(p0: DataSnapshot) {

        val messageId = p0.key
        Log.d("messageId", "$messageId")

        val ref2 = FirebaseDatabase.getInstance().getReference("/messages/$messageId")

        ref2.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
//              final = final?.plus(1)
//              Log.d(TAG, "$final")
//              val count = p0.getChildrenCount() -1;


                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                    latestmessagesMap[chatMessage.toId] = chatMessage
                }
                else {
                    latestmessagesMap[chatMessage.fromId] = chatMessage
                }


                Log.d("SENDMESSAGE", "$latestmessagesMap[chatMessage.toId]")
//                refreshRecyclerViewMessages(messagecount)
                refreshRecyclerViewMessages()



                //adapter.add(LatestMessageRow(chatMessage))

                messagelast = (chatMessage?.text)

                Log.d(TAG, "${messagelast}")

            }
            override fun onCancelled(p0: DatabaseError) {}
        })


    }

    val adapter = GroupAdapter<ViewHolder>()



    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun veryfyUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent =  Intent(getActivity(), TermsScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(getActivity(), NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent =  Intent(getActivity(), RegisterLoginActivityMain::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)


            }
        }
        return super.onOptionsItemSelected(item)
    }


}
