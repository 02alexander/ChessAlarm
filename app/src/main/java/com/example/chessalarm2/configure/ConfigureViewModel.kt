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

    var old_alarm: Alarm? = null
    val alarm = MutableLiveData<Alarm>(Alarm())
    private val scheduler = Scheduler(application)

    init {
        viewModelScope.launch {
            database.get(alarmId).let {
                old_alarm = it
                alarm.value = it
            }
        }
    }

    fun saveAlarm() {
        viewModelScope.launch {
            alarm.value?.let { new_alarm ->
                old_alarm?.let {
                    if (it.isEnabled) {
                        scheduler.disableAlarm(it)
                        scheduler.enableAlarm(new_alarm)
                    }
                    database.update(new_alarm)
                }
            }
        }
    }
}