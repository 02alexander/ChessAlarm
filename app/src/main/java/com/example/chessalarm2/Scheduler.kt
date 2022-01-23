package com.example.chessalarm2

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.chessalarm2.chessproblemalarm.AlarmReceiver
import com.example.chessalarm2.database.alarms.Alarm
import java.util.*

class Scheduler(val context: Context) {

    private var alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun disableAlarm(alarm: Alarm) {
        val date = Date(alarm.time)
        Log.d("disableAlarm", date.toString())
        for (day in alarm.days) {
            val alarmIntent = getAlarmIntent(alarm, day)
            alarmManager.cancel(alarmIntent)

        }
    }

    fun enableAlarm(alarm: Alarm) {
        for (day in alarm.days) {
            val date = Date(alarm.time)
            val c = Calendar.getInstance()
            c.time = date
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, c.get(Calendar.MINUTE))
                set(Calendar.DAY_OF_WEEK, day+2)
            }
            if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR,7)
            }
            val alarmIntent = getAlarmIntent(alarm, day)
            Log.d("scheduler", "set alarm at "+Date(calendar.timeInMillis).toString())
            /*alarmManager.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 10,
                alarmIntent
            )*/
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent
            )
        }
    }

    private fun getAlarmIntent(alarm: Alarm, day: Int) : PendingIntent {
        //val application = context.getApplication<Application>()
        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            intent.action = "com.example.alarmmanager"
            intent.putExtra("alarmId", alarm.alarmId)
            PendingIntent.getBroadcast(context, getIntentId(alarm.alarmId, day), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        return alarmIntent
    }

    private fun getIntentId(alarmId: Long, day: Int): Int {
        return alarmId.toInt()+(1 shl 31-day)
    }
}