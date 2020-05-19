package com.blackcharm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class FirebaseBackgroundService : BroadcastReceiver() {

    var myFirebaseMessagingService = MyFirebaseMessagingService()
    var notificationtitle: Any? = ""
    var notificationbody: Any? = ""



    override fun onReceive(context: Context, intent: Intent) {
        Log.d("notiftest", "I'm in!!!")

//
//
//        if (intent.extras != null) {
//            for (key in intent.extras!!.keySet()) {
//                val value = intent.extras!!.get(key)
//                Log.e("FirebaseDataReceiver", "Key: $key Value: $value")
//                if (key.equals("gcm.notification.title", ignoreCase = true) && value != null) {
//
//                    notificationtitle = value
//                }
//
//                if (key.equals("gcm.notification.body", ignoreCase = true) && value != null) {
//
//                    notificationbody = value
//                }
//            }
//
//
//            myFirebaseMessagingService.showNotification(notificationtitle as String, notificationbody as String, context)
//            var manager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            manager.cancel(0);
      //  }
    }

    companion object {

        private val TAG = "FirebaseService"
    }
}