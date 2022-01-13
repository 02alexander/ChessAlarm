package com.example.chessalarm2.chessproblemalarm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chessalarm2.database.alarms.Alarm
import com.example.chessalarm2.database.alarms.AlarmsDatabaseDao

class ChessAlarmViewModelFactory (
    private val databaseDao: AlarmsDatabaseDao,
    private val alarmId: Long,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChessAlarmViewModel::class.java)) {
            return ChessAlarmViewModel(databaseDao, alarmId, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}