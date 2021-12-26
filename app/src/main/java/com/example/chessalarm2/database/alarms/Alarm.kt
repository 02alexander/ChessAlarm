package com.example.chessalarm2.database.alarms

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms_table")
data class Alarm (
    @PrimaryKey(autoGenerate = true)
    var alarmId: Long = 0,

    @ColumnInfo
    var time: Long = System.currentTimeMillis(),

    @ColumnInfo
    var difficulty: Int = 1,

    @ColumnInfo
    var days: List<Int> = listOf(),

    @ColumnInfo
    var isEnabled: Boolean = false,

    @ColumnInfo
    var audioId: Long = -1
)