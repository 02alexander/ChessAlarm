package com.example.chessalarm2.configure

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chessalarm2.Scheduler
import com.example.chessalarm2.database.alarms.Alarm
import com.example.chessalarm2.database.alarms.AlarmsDatabaseDao
import kotlinx.coroutines.launch

class ConfigureViewModel(val database: AlarmsDatabaseDao, alarmId: Long, application: Application): AndroidViewModel(application) {

    val alarm = MutableLiveData<Alarm>(Alarm())
    private val scheduler = Scheduler(application)

    init {
        viewModelScope.launch {
            database.get(alarmId).let {
                alarm.value = it
            }
        }
    }

    // Save the alarm in the database and also changes the pending alarm if it's enabled.
    // NOTE, new_alarm must the same alarmId as this.alarm
    fun updateAlarm(new_alarm: Alarm) {
        assert(alarm.value!!.alarmId == new_alarm.alarmId)
        if (alarm.value!!.isEnabled) {
            scheduler.disableAlarm(alarm.value!!)
        }
        alarm.value = new_alarm
        if (alarm.value!!.isEnabled) {
            scheduler.enableAlarm(alarm.value!!)
        }
        saveAlarm()
    }

    fun saveAlarm() {
        viewModelScope.launch {
            alarm.value?.let {
                database.update(it)
            }
        }
    }
}