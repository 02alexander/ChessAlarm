package com.example.chessalarm2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chessalarm2.database.Alarm
import com.example.chessalarm2.database.AlarmsDatabase
import com.example.chessalarm2.database.AlarmsDatabaseDao
import com.example.chessalarm2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var alarmsDao: AlarmsDatabaseDao
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess_alarm)
    }
}