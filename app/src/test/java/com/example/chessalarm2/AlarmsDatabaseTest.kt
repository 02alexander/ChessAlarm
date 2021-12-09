package com.example.chessalarm2

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.chessalarm2.database.Alarm
import com.example.chessalarm2.database.AlarmsDatabase
import com.example.chessalarm2.database.AlarmsDatabaseDao
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.IOException


@Config(manifest= Config.NONE)
@RunWith(AndroidJUnit4::class)
class AlarmsDatabaseTest {

    private lateinit var alarmDao: AlarmsDatabaseDao
    private lateinit var db: AlarmsDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, AlarmsDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        alarmDao = db.alarmsDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAlarm() {
        val alarm: Alarm = Alarm()
        alarmDao.insert(alarm)
        Thread.sleep(500)
        val tonight = alarmDao.getAllAlarms()
        Assert.assertEquals(1, tonight.value?.size)
    }
}
