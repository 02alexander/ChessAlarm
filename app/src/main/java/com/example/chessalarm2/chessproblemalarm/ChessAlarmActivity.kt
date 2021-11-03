package com.example.chessalarm2.chessproblemalarm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.chessalarm2.R
import com.example.chessalarm2.databinding.ActivityChessAlarmBinding

class ChessAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChessAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chess_alarm)
    }
}