package com.boss.chessalarm2.database.puzzles

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Puzzle::class], version = 2, exportSchema = false)
abstract class PuzzlesDatabase : RoomDatabase() {

    abstract val puzzleDatabaseDao: PuzzlesDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: PuzzlesDatabase? = null

        fun getInstance(context: Context): PuzzlesDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PuzzlesDatabase::class.java,
                        "puzzles_table"
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