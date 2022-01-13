package com.example.chessalarm2.chessproblemalarm

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chessalarm2.R
import com.example.chessalarm2.alarms.AlarmsViewModel
import com.example.chessalarm2.configure.ConfigureViewModel
import com.example.chessalarm2.configure.ConfigureViewModelFactory
import com.example.chessalarm2.database.alarms.AlarmsDatabase
import com.example.chessalarm2.database.alarms.AlarmsDatabaseDao
import com.example.chessalarm2.databinding.ActivityChessAlarmBinding
import com.example.chessalarm2.parse_UCI

// https://android--code.blogspot.com/2018/05/android-kotlin-get-alarm-ringtone.html

class ChessAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChessAlarmBinding
    private var solution: List<Pair<Coordinate, Coordinate>> = parse_UCI("d3d6 f8d8 d6d8 f6d8")
    private lateinit var viewModel: ChessAlarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val alarmId: Long?
        alarmId = if (savedInstanceState == null) {
            Log.d("chess_alarm", "fetching audioId from intent.extras")
            val extras = intent.extras
            extras?.getLong("alarmId")
        } else {
            Log.d("chess_alarm", "fetching audioId from savedInstanceState")
            savedInstanceState.getSerializable("alarmId") as Long?
        }

        Log.d("chess_alarm", alarmId.toString())

        val database = AlarmsDatabase.getInstance(application).alarmsDatabaseDao
        val viewModelFactory = ChessAlarmViewModelFactory(database, alarmId!!, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChessAlarmViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chess_alarm)

        binding.chessView.board.move_piece(solution[0].first, solution[0].second)
        solution = solution.slice(1 until solution.size)
        binding.chessView.setOnChessMoveListener(::on_move)
    }

    fun on_move(src: Coordinate, dst: Coordinate) {
        if (solution[0].first != src || solution[0].second != dst) {
            Log.d("on_move()", "wrong move, correct move is "+solution[0].first.toString()+", "+solution[1].second.toString())
        } else {
            binding.chessView.board.move_piece(src, dst)
            if (solution.size <= 1) { // checks if this was the last move in the solutions
                Log.d("on_move()", "You solved the puzzle!")
                viewModel.stopAlarmAudio()
                AlarmReceiver.mediaPlayer?.stop()
                finish()
                return
            }
            // plays opponents move and then removes the moves played from solution
            binding.chessView.board.move_piece(solution[1].first, solution[1].second)
            solution = solution.slice(2 until solution.size)
        }
    }


}