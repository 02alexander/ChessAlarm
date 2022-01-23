package com.example.chessalarm2.database.alarms

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Alarm::class], version = 8, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AlarmsDatabase : RoomDatabase() {

    abstract val alarmsDatabaseDao: AlarmsDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: AlarmsDatabase? = null

        fun getInstance(context: Context): AlarmsDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AlarmsDatabase::class.java,
                        "alarms_table"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}