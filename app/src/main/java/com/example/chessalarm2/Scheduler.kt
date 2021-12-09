package com.example.chessalarm2

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.chessalarm2.chessproblemalarm.AlarmReceiver
import com.example.chessalarm2.chessproblemalarm.ChessAlarmActivity
import com.example.chessalarm2.database.Alarm
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*

class Scheduler(val context: Context) {

    private var alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun disableAlarm(alarm: Alarm) {
        val date = Date(alarm.time)
        Log.d("disableAlarm", date.toString())
        val alarmIntent = getAlarmIntent(alarm)
        alarmManager.cancel(alarmIntent)
    }

    fun enableAlarm(alarm: Alarm) {
        val date = Date(alarm.time)
        val c = Calendar.getInstance()
        c.time = date
        Log.d("enableAlarm", date.toString())
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, c.get(Calendar.MINUTE))
        }
        val alarmIntent = getAlarmIntent(alarm)
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 1 * 1000,
            alarmIntent
        )
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
    }

    private fun getAlarmIntent(alarm: Alarm) : PendingIntent {
        //val application = context.getApplication<Application>()
        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            intent.action = "com.example.alarmmanager"
            PendingIntent.getBroadcast(context, alarm.alarmId.toInt(), intent, 0)
        }
        return alarmIntent
    }
}