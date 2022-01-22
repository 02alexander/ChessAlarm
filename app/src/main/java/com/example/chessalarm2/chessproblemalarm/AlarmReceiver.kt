package com.example.chessalarm2.chessproblemalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.chessalarm2.R
import com.example.chessalarm2.database.alarms.AlarmsDatabase
import com.example.chessalarm2.playAudioFromId
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    companion object {

        // mediaPlayer starts in AlarmReceiver but its ChessAlarmActivity that stops this mediaPlayer which is a global variable.
        @JvmField
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onReceive(context: Context, intent: Intent?) {

        Log.d("onReceive", "Received signal to start alarm")

        val t = Intent(context, ChessAlarmActivity::class.java)
        val alarmId = intent!!.extras!!.getLong("alarmId")
        t.putExtra("alarmId", alarmId)

        val database = AlarmsDatabase.getInstance(context).alarmsDatabaseDao
        GlobalScope.launch {
            val alarm = database.get(alarmId)!!
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )
            Log.d("onReceive", alarm.audioId.toString())
            playAudioFromId(context, mediaPlayer!!, alarm.audioId)
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, t, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(context, notificationManager)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Chess Alarm")
            .setContentText("Press to start solving the problem.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(pendingIntent, true)

        with(notificationManager) {
            notify(0, builder.build())
        }

        Log.d("AlarmReceiver", "onReceive done")

    }

    private fun createNotificationChannel(context: Context?, notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Chess alarm channel"
            val descriptionText = "Channel for sending signals to start the alarm"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }

    }
}

private const val CHANNEL_ID = "channelId"
