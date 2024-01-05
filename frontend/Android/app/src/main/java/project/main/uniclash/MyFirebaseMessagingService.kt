package project.main.uniclash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage



const val channelId = "notification_channel"
const val channelName = "project.main.uniclash"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Show the notification
    override fun onMessageReceived(message: RemoteMessage) {
        if (message.notification != null) {
            generateNotification(message.notification!!.title!!, message.notification!!.body!!)
        }
    }
    // Generate the notification
    private fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext, channelId
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "Refreshed token: $token")

        // TODO: Send the token to your server for storage

    }


}
