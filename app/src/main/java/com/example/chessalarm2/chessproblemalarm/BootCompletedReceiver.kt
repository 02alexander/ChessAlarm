package com.example.chessalarm2.chessproblemalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.chessalarm2.MainActivity
import com.example.chessalarm2.Scheduler
import com.example.chessalarm2.database.alarms.AlarmsDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent!!.getAction())) {
            GlobalScope.launch {
                val database = AlarmsDatabase.getInstance(context!!).alarmsDatabaseDao
                val alarms = database.blockingGetAllAlarms()
                val scheduler = Scheduler(context)
                for (alarm in alarms) {
                    if (alarm.isEnabled) {
                        scheduler.enableAlarm(alarm)
                    }
                }
            }



        }
    }
}