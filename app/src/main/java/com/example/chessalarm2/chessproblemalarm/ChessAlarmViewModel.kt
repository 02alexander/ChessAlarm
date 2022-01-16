package com.example.chessalarm2.chessproblemalarm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chessalarm2.database.alarms.Alarm
import com.example.chessalarm2.database.alarms.AlarmsDatabaseDao
import kotlinx.coroutines.launch

class ChessAlarmViewModel(private val databaseDao: AlarmsDatabaseDao, private val alarmId: Long, application: Application) : AndroidViewModel(application){
    private lateinit var solution: List<Pair<Coordinate, Coordinate>>
    val alarm: MutableLiveData<Alarm> = MutableLiveData()

    init {
        Log.d("chess_alarm", "ChessAlarmViewModel created")
        fetchAlarm()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("chess_alarm", "ChessAlarmViewModel cleared")
    }

    fun fetchAlarm() {
        if (alarm.value == null) {
            viewModelScope.launch {
                alarm.value = databaseDao.get(alarmId)
                startAlarmAudio()
            }
        }
    }

    fun startAlarmAudio() {
        /*
        alarm.value?.let {
            playAudioFromId(getApplication(), mp, it.audioId)
        }*/
    }

    fun stopAlarmAudio() {
        //mp.stop()
    }

}