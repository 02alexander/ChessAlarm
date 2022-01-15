package com.example.chessalarm2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chessalarm2.database.alarms.AlarmsDatabaseDao
import com.example.chessalarm2.databinding.ActivityMainBinding
import com.example.chessalarm2.puzzlesource.PopulateDatabase

class MainActivity : AppCompatActivity() {

    lateinit var alarmsDao: AlarmsDatabaseDao
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val intent = Intent(this, ChessAlarmActivity::class.java)
        //intent.putExtra("audioId", 0)
        //startActivity(intent)
        setContentView(R.layout.activity_main)
        PopulateDatabase.populateDatabase(application)
    }

}