package com.example.blchatclone

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage

private const val channelID = "notification_channel"
private const val channelName = "com.example.blchatclone"

class MyFireBaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        if(message.notification != null){
            generateNotification(message.notification!!.title!!, message.notification!!.body!!)
        }
    }

    fun generateNotification(title: String, message: String){

        val intentToApp = Intent(this, MainActivity::class.java)
        intentToApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intentToApp, PendingIntent.FLAG_ONE_SHOT)

        //Need Channel id and name
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelID)

        builder.setSmallIcon(R.drawable.whatsapp_medium)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())
    }

    @SuppressLint("RemoteViewLayout")
    private fun getRemoteView(title: String, message: String): RemoteViews? {
        val remoteView = RemoteViews(channelName, R.layout.push_notification)

        remoteView.setTextViewText(R.id.notificationTitle, title)
        remoteView.setTextViewText(R.id.notificationMessage, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.whatsapp_medium)

        return remoteView
    }
}