package com.example.chessalarm2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.chessalarm2.chessproblemalarm.ChessAlarmActivity
import com.example.chessalarm2.database.alarms.AlarmsDatabaseDao
import com.example.chessalarm2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var alarmsDao: AlarmsDatabaseDao
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val intent = Intent(this, ChessAlarmActivity::class.java)
        //intent.putExtra("audioId", 0)
        //startActivity(intent)
        setContentView(R.layout.activity_main)
    }

}