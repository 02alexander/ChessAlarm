package com.example.chessalarm2.chessproblemalarm

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.chessalarm2.R
import com.example.chessalarm2.databinding.ActivityChessAlarmBinding
import com.example.chessalarm2.parse_UCI

// https://android--code.blogspot.com/2018/05/android-kotlin-get-alarm-ringtone.html

class ChessAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChessAlarmBinding

    private var solution: List<Pair<Coordinate, Coordinate>> = parse_UCI("d3d6 f8d8 d6d8 f6d8")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                return
            }

            // plays opponents move and then removes the moves played from solution
            binding.chessView.board.move_piece(solution[1].first, solution[1].second)
            solution = solution.slice(2 until solution.size)
        }
    }
}