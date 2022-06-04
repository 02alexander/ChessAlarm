package com.boss.chessalarm2.chessproblemalarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.boss.chessalarm2.R
import com.boss.chessalarm2.databinding.ActivitySnoozeBinding
import java.util.*

const val MIN_SNOOZE: Int = 5

class SnoozeActivity : AppCompatActivity() {

    private var alarmId: Long? = null

    private lateinit var binding: ActivitySnoozeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Snooze", "Activity started.")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_snooze)

        alarmId = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.getLong("alarmId")
        } else {
            savedInstanceState.getSerializable("alarmId") as Long?
        }

        binding.snoozeButton.setOnClickListener {
            onSnooze()
        }

        binding.solvePuzzleButton.setOnClickListener {
            onSolve()
        }
    }

    fun onSnooze() {
        Log.d("Snooze", "Snooze button pressed")
        val alarmManager = this.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val c = Calendar.getInstance()
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, c.get(Calendar.MINUTE)+ MIN_SNOOZE)
            set(Calendar.DAY_OF_WEEK, c.get(Calendar.DAY_OF_WEEK))
        }
        if (calendar.timeInMillis < System.currentTimeMillis()+3000) {
            calendar.add(Calendar.DAY_OF_YEAR,7)
        }
        val alarmIntent = Intent(this.applicationContext, AlarmReceiver::class.java).let { intent ->
            intent.action = "com.example.alarmmanager"
            intent.putExtra("alarmId", alarmId)
            PendingIntent.getBroadcast(this.applicationContext, 4356345, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(calendar.timeInMillis, alarmIntent), alarmIntent)
        /*alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 1*1000,
            alarmIntent
        )*/
        Log.d("Snooze", "stopping sound")
        AlarmReceiver.mediaPlayer?.stop()
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(AlarmReceiver.NOTIFICATION_ID)
        finish()
    }


    fun onSolve() {
        val intent = Intent(this.applicationContext, ChessAlarmActivity::class.java)
        intent.putExtra("alarmId", alarmId)
        startActivity(intent)
        finish()
    }
}