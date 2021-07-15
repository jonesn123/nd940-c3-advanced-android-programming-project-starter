package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var downloadButton: LoadingButton

    private lateinit var notificationManager: NotificationManager

    private var radioSelectedId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
        downloadButton = findViewById(R.id.custom_button)
        setSupportActionBar(toolbar)
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            var url: String? = null
            when (radioSelectedId) {
                R.id.radio1 -> {
                    url = "https://github.com/bumptech/glide"
                }
                R.id.radio2 -> {
                    url =
                        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                }
                R.id.radio3 -> {
                    url = "https://github.com/square/retrofit"
                }
                else -> {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.toast_desc),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            download(url ?: URL)

        }

        radio_group.setOnCheckedChangeListener { _, checkedId ->
            radioSelectedId = checkedId
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            downloadID = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: 0
            notificationManager.cancelAll()
            when (radioSelectedId) {
                R.id.radio1 -> {
                    sendNotification(
                        DetailActivity.newIntent(
                            this@MainActivity,
                            radio1.text.toString(),
                            DetailActivity.STATUS.SUCCESS
                        )
                    )
                }
                R.id.radio2 -> {
                    sendNotification(
                        DetailActivity.newIntent(
                            this@MainActivity,
                            radio2.text.toString(),
                            DetailActivity.STATUS.FAIL
                        )
                    )
                }
                R.id.radio3 -> {
                    sendNotification(
                        DetailActivity.newIntent(
                            this@MainActivity,
                            radio3.text.toString(),
                            DetailActivity.STATUS.SUCCESS
                        )
                    )
                }
            }
            downloadButton.completedDownload()
        }
    }

    private fun sendNotification(intent: Intent) {
        val notificationID = downloadID.toInt()
        val contentIntent = Intent(this, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            this, notificationID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, notificationID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
            this,
            getString(R.string.notification_channel_id)
        )

            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(
                getString(R.string.notification_title)
            )
            .setContentText(getString(R.string.notification_desc))

            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

            .addAction(
                R.drawable.ic_assistant_black_24dp,
                getString(R.string.check_the_status),
                pendingIntent
            )

            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(notificationID, builder.build())
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    /**
     * Cancels all notifications.
     *
     */
    fun NotificationManager.cancelNotifications() {
        cancelAll()
    }

}


