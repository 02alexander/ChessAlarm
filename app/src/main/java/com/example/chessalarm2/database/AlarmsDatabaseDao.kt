package com.example.chessalarm2.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlarmsDatabaseDao {

    @Insert
    suspend fun insert(alarm: Alarm)

    @Update
    suspend fun update(alarm: Alarm)

    @Query("SELECT * from alarms_table WHERE alarmId = :key")
    suspend fun get(key: Long): Alarm?

    @Query("DELETE FROM alarms_table")
    suspend fun clear()

    @Query("SELECT * FROM alarms_table ORDER BY alarmId DESC")
    fun getAllAlarms(): LiveData<List<Alarm>>
}