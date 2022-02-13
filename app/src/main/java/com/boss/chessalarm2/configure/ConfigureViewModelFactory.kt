package com.boss.chessalarm2.configure

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.boss.chessalarm2.database.alarms.AlarmsDatabaseDao

class ConfigureViewModelFactory(
    private val dataSource: AlarmsDatabaseDao,
    private val alarmId: Long,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfigureViewModel::class.java)) {
            return ConfigureViewModel(dataSource, alarmId, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
