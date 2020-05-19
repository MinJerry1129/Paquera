package com.blackcharm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.blackcharm.messages.ChatLogActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "FirebaseMessagingService"

    var notificationId = 10
    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Dikirim dari: ${remoteMessage.from}")
        Log.d(TAG, remoteMessage.getData().toString())
//        showNotification("asdf", "asdfasdf",this);
        val notifyData = remoteMessage.getData()
        System.out.println(notifyData.get("method"));

        if(notifyData.get("method").equals("chat",true))
        {
            showChatNotification("Message from "+notifyData.get("fromName"),notifyData.get("text"), notifyData.get("uid"))
        }
        else if(notifyData.get("method").equals("likeyou",true)){
            showChatNotification(notifyData.get("fromName")+" like you!",notifyData.get("text"), notifyData.get("uid"))
        }
        else if(notifyData.get("method").equals("friend",true)){
            showChatNotification(notifyData.get("fromName"),notifyData.get("text"), notifyData.get("uid"))
        }
    }

    fun showChatNotification(title: String?, body: String?, uid: String?) {

        val intent = Intent(applicationContext, ChatLogActivity::class.java)

        intent.putExtra("USER_UID", uid)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, "my_channel_id_01").apply {
            setSmallIcon(R.drawable.my_friends_room_logo_notification)
    //        setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
            setContentTitle(title)
            setContentText(body)
            setSound(soundUri)
            setContentIntent(pendingIntent)
            setDefaults(Notification.DEFAULT_VIBRATE)
          //  setTimeoutAfter(2000)
            setPriority(PRIORITY_HIGH)
            setVibrate(LongArray(0))
        }

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "my_channel_id_01",
                "Default channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.setShowBadge(true)
            channel.canShowBadge()
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())

//        with(NotificationManagerCompat.from(this)) {
//            // notificationId is a unique int for each notification that you must define
//            notify(0, notificationBuilder.build())
//        }
    }
}

