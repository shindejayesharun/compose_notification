package com.shindejayesharun.composenotification

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import com.shindejayesharun.composenotification.ui.theme.ComposeNotificationTheme
import android.R
import android.R.attr
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

import android.graphics.Bitmap
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import android.R.attr.bitmap

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.*
import android.R.id.icon1

import android.R.attr.bitmap
import android.R.id
import com.bumptech.glide.Glide
import kotlinx.coroutines.awaitAll
import android.R.attr.bitmap
import android.R.attr.bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


// declaring variables
lateinit var notificationManager: NotificationManager
lateinit var notificationChannel: NotificationChannel
lateinit var builder: Notification.Builder
private val channelId = "i.apps.notifications"
private val description = "Test notification"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        setContent {
            ComposeNotificationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainView()
                }
            }
        }
    }
}

fun notificationShow(context: Context,header:String,description:String,bitmap:Bitmap){


    // pendingIntent is an intent for future use i.e after
    // the notification is clicked, this intent will come into action
    val intent = Intent(context, MainActivity::class.java)

    // FLAG_UPDATE_CURRENT specifies that if a previous
    // PendingIntent already exists, then the current one
    // will update it with the latest intent
    // 0 is the request code, using it later with the
    // same method again will get back the same pending
    // intent for future reference
    // intent passed here is to our afterNotification class
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    // RemoteViews are used to use the content of
    // some different layout apart from the current activity layout
    //val contentView = RemoteViews(, R.layout.activity_after_notification)

    // checking if android version is greater than oreo(API 26) or not
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)

        builder = Notification.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_media_play)
            .setContentText(header)
            .setContentTitle(description)
            .setOngoing(true)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_lock_idle_alarm))
            .setStyle( Notification.BigPictureStyle()
                .bigPicture(bitmap))
            .setContentIntent(pendingIntent)


    } else {

        builder = Notification.Builder(context)
            .setSmallIcon(R.drawable.sym_def_app_icon)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.star_big_off))
            .setContentIntent(pendingIntent)
    }
    notificationManager.notify(1234, builder.build())
}

fun getBitmapFromURL(strURL: String?): Bitmap? {
    return try {
        val url = URL(strURL)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.setDoInput(true)
        connection.connect()
        val input: InputStream = connection.getInputStream()
        BitmapFactory.decodeStream(input)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
@Composable
fun MainView() {
    val context = LocalContext.current
    var headingValue by rememberSaveable { mutableStateOf("") }
    var descriptionValue by rememberSaveable { mutableStateOf("") }
    Column {
        TextField(
            value = headingValue,
            onValueChange = {
                headingValue = it
            },
            label = { Text("Heading") },
        )
        TextField(
            value = descriptionValue,
            onValueChange = {
                descriptionValue = it
            },
            label = { Text("Description") },
        )
        Button(onClick = {


            print("heading $headingValue")
            print("description $descriptionValue")

            Glide.with(context)
                .asBitmap()
                .load("https://lh3.googleusercontent.com/a-/AOh14Gh-Lcev_OlFS5q0gafmPKABp7v6dpXJrWyzxIEpiw=s360-p-rw-no")
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        notificationShow(context = context,header = headingValue,description = descriptionValue,bitmap = resource)
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })

        },
        content = {Text("Submit")})
        Text("values $headingValue $descriptionValue")
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeNotificationTheme {
        MainView()
    }
}