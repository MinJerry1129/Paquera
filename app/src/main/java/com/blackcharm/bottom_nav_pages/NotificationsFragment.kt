package com.blackcharm.bottom_nav_pages


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.blackcharm.R
import com.blackcharm.messages.NewMessageActivity
import com.blackcharm.models.NotificationLine
import com.blackcharm.models.User
import com.blackcharm.registerlogin.RegisterLoginActivityMain
import com.blackcharm.registerlogin.TermsScreen
import com.blackcharm.views.NotificationsRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_notifications.*


class NotificationsFragment : Fragment() {

    companion object {
        var currentUser: User? = null
        var messagelast: String? = null
        var counter: Int? = 1;
        var final: Int? = 0;

    }

    var listNotificationLine = ArrayList<NotificationLine>()


    val TAG = "Notifications"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        var recyclerview_notifications = view.findViewById<RecyclerView>(R.id.recyclerview_notifications)

        Log.d("Receivenotification", "Notification fragment visible")







        recyclerview_notifications.adapter = adapter
        recyclerview_notifications.addItemDecoration(DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL))




//        adapter.setOnItemClickListener { item, view ->
//            val notifyrow = adapter.getAdapterPosition(item)
//            adapter.removeGroup(notifyrow)
//            Log.d("notificationrow", ""+adapter.getAdapterPosition(item))
//
//        }





//        adapter.setOnItemClickListener { item, view ->
//            val intent = Intent(getActivity(), ChatLogActivity::class.java)
//            val row = item as NotificationsRow
//            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
//            startActivity(intent)
//        }

        // Set the toolbar as support action bar

        (activity as AppCompatActivity).setSupportActionBar(notifications_toolbar)

        fetchCurrentUser()

        veryfyUserLoggedIn()

        listenForNotifications()
        return view
    }

    val latestnotificationsMap = HashMap<String, NotificationLine>()

//    private fun refreshRecyclerViewNotifications() {
//        adapter.clear()
//        latestnotificationsMap.values.forEach {
//            adapter.add(NotificationsRow(it))
//        }
//    }

    private fun listenForNotifications() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users-notifications/$fromId")

        Log.d("Receivenotification", "listenForNotifications $fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Receive notification", "error")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                fetchLastNotificationId(p0, fromId)

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

    private fun fetchLastNotificationId(p0: DataSnapshot, fromId: String?) {
        val userId = p0.key
        Log.d("userId", "$userId")
        val ref1 = FirebaseDatabase.getInstance().getReference("/users-notifications/$fromId/$userId")


        ref1.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                var notificationseen = 0

                val notification_line = p0.getValue(NotificationLine::class.java) ?: return

                /*if (p0.child("seen").exists()){
                    notificationseen = p0.child("seen").value.toString().toInt()

                }
                else {
                    notificationseen = 0
                }*/


                //latestnotificationsMap[notification_line.senderUid] = notification_line

                Log.d("Receivenotification", ""+notification_line.senderUid)

                //adapter.add(NotificationsRow(context!!, notification_line, adapter, p0.key!!, notificationseen))

                notification_line.keyVal = p0.key!!
                notification_line.notificationseen = notificationseen

                listNotificationLine.add(notification_line)

                reloadAdapter()





                Log.d("Receive notification", "$latestnotificationsMap[notification_line.senderUid]")
                //refreshRecyclerViewNotifications()

                //adapter.add(LatestMessageRow(chatMessage))

                //messagelast = (chatMessage?.text)

                //Log.d(TAG, "${messagelast}")
            }

            override fun onCancelled(p0: DatabaseError) {}

        })
    }

    private fun reloadAdapter() {
        adapter.clear()
        var sortedList = listNotificationLine.sortedWith(compareByDescending({ it.timestamp }))
        sortedList.forEach {
            adapter.add(NotificationsRow(context!!, it, adapter, it.keyVal, it.notificationseen))
        }
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

    override fun onResume() {
        super.onResume()



    }


}
