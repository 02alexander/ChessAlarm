package com.example.chessalarm2.chessproblemalarm

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.chessalarm2.R
import com.example.chessalarm2.database.alarms.AlarmsDatabase
import com.example.chessalarm2.database.puzzles.PuzzlesDatabase
import com.example.chessalarm2.databinding.ActivityChessAlarmBinding
import com.example.chessalarm2.parse_UCI
import kotlinx.coroutines.launch

// https://android--code.blogspot.com/2018/05/android-kotlin-get-alarm-ringtone.html

class ChessAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChessAlarmBinding
    //private var solution: List<Pair<Coordinate, Coordinate>> = parse_UCI("d3d6 f8d8 d6d8 f6d8")
    private var solution: List<Pair<Coordinate, Coordinate>>? = null
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

        val puzzleDatabase = PuzzlesDatabase.getInstance(application).puzzleDatabaseDao

        binding.chessView.setOnChessMoveListener(::on_move)

        viewModel.alarm.observe(this, {
            lifecycleScope.launch {
                val puzzles = puzzleDatabase.getEligiblePuzzles(it!!.rating)
                val puzzle = puzzles[0]
                Log.d("chess_alarm", "puzzle=${puzzle.toString()}")
                Log.d("chess_alarm", "moves=${puzzle.moves}")
                solution = parse_UCI(puzzle.moves)
                binding.chessView.loadFEN(puzzle.FEN)
                binding.chessView.board.move_piece(solution!![0].first, solution!![0].second)
                solution = solution!!.slice(1 until solution!!.size)
                Log.d("chess_alarm", "board=${binding.chessView.board.board}")
                puzzle.beenPlayed = true
                puzzleDatabase.update(puzzle)
            }
        })

    }

    fun on_move(src: Coordinate, dst: Coordinate) {
        solution?.let {
            if (it[0].first != src || it[0].second != dst) {
                Log.d(
                    "on_move()",
                    "wrong move, correct move is " + it[0].first.toString() + ", " + it[1].second.toString()
                )
            } else {
                binding.chessView.board.move_piece(src, dst)
                if (it.size <= 1) { // checks if this was the last move in the solutions
                    Log.d("on_move()", "You solved the puzzle!")
                    viewModel.stopAlarmAudio()
                    AlarmReceiver.mediaPlayer?.stop()
                    finish()
                    return
                }
                // plays opponents move and then removes the moves played from solution
                binding.chessView.board.move_piece(it[1].first, it[1].second)
                solution = it.slice(2 until it.size)
            }
        }
    }


}