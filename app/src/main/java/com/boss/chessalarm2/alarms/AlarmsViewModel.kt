package com.boss.chessalarm2.alarms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.boss.chessalarm2.Scheduler
import com.boss.chessalarm2.database.alarms.Alarm
import com.boss.chessalarm2.database.alarms.AlarmsDatabaseDao
import kotlinx.coroutines.launch

class AlarmsViewModel(
    val database: AlarmsDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    val alarms = database.getAllAlarms()
    private val _navigateToConfigure = MutableLiveData<Long>()
    val navigateUpListener
        get() = _navigateToConfigure

    val alarmsString = Transformations.map(alarms) { alarms ->
        var res: String = ""
        for (alarm in alarms) {
            res += alarm.rating.toString()+" "+alarm.time.toString()
            //res += alarm.difficulty.toString()+" "+alarm.time.toString()
        }
        res
    }

    val scheduler = Scheduler(application)

    fun onAddAlarm() {
        viewModelScope.launch {
            val alarm = Alarm()
            database.insert(alarm)
        }
    }

    fun onDeleteAlarm(alarmId: Long) {
        viewModelScope.launch {
            val alarm = database.get(alarmId)!!
            if (alarm.isEnabled) {
                val scheduler = Scheduler(getApplication())
                scheduler.disableAlarm(alarm)
            }
            database.delete(alarmId)
        }
    }

    fun onAlarmClicked(id: Long) {
        _navigateToConfigure.value = id
    }

    //fun onToggleClicked()

    fun onConfigureNavigated() {
        _navigateToConfigure.value = null
    }
}