package com.example.chessalarm2.chessproblemalarm

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
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

    // TODO: disable castling rights after moving king or rook

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

        stopAlarmInNMinutes(60)

        //binding.chessView.loadFEN("rnbqkbnr/pppp1ppp/8/8/4P3/4K3/PPPP2pP/RNBQ1BNR b kq - 1 5")
        //binding.chessView.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")

        // called once alarm has been fetched from the database.
        viewModel.alarm.observe(this, {

            lifecycleScope.launch {
                val puzzles = puzzleDatabase.getEligiblePuzzles(it!!.rating)
                val puzzle = puzzles[0]
                //val puzzle = puzzleDatabase.get(13003469153L)!!
                Log.d("chess_alarm", "puzzle=${puzzle.toString()}")
                Log.d("chess_alarm", "moves=${puzzle.moves}")
                solution = parse_UCI(puzzle.moves)
                binding.chessView.loadFEN(puzzle.FEN)
                binding.chessView.play_move(solution!![0].first, solution!![0].second)
                Log.d("chess_alarm", "board=${binding.chessView.board.board}")
                solution = solution!!.slice(1 until solution!!.size)

                updatePlayerToMove()
                puzzle.beenPlayed = true
                puzzleDatabase.update(puzzle)
            }
        })
        turnScreenOnAndKeyguardOff()
    }

    private fun stopAlarmInNMinutes(minutes: Int) {
        Handler(Looper.getMainLooper()).postDelayed({
            on_solved()
        }, 1000*60*minutes.toLong())

    }

    private fun updatePlayerToMove() {
        val player = binding.chessView.board.cur_player
        if (player == Player.BLACK) {
            binding.playerToMoveTextView.text = "Black to move"
            binding.playerToMoveImageView.setImageResource(R.drawable.ic_bk)
        } else {
            binding.playerToMoveTextView.text = "White to move"
            binding.playerToMoveImageView.setImageResource(R.drawable.ic_wk)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }

    // used for debugging Chess
    fun temp_on_move(src: Coordinate, dst: Coordinate) {
        binding.chessView.play_move(src, dst)
        if (binding.chessView.board.is_in_check(Player.BLACK)) {
            Log.d("chess", "black king checked")
        } else if (binding.chessView.board.is_in_check(Player.WHITE)) {
            Log.d("chess", "white king checked")
        }

        if (binding.chessView.board.is_in_checkmate(Player.BLACK)) {
            Log.d("chess", "black is checkmated")
        } else if (binding.chessView.board.is_in_checkmate(Player.WHITE)) {
            Log.d("chess", "white king is checkmated")
        }

    }

    fun on_move(src: Coordinate, dst: Coordinate) {
        solution?.let {

            val cpy = binding.chessView.board.copy()
            cpy.play_move(src, dst)
            if (cpy.is_in_checkmate(cpy.cur_player)) {
                on_solved()
                return
            }
            Log.d("on_move()", "played ($src, $dst), correct (${it[0]})")
            if (it[0].first != src || it[0].second != dst) {
                Log.d(
                    "on_move()",
                    "wrong move, correct move is " + it[0].first.toString() + ", " + it[0].second.toString()
                )
                binding.chessView.indicateWrongMove(src, dst)
            } else {
                binding.chessView.play_move(src, dst)
                if (it.size <= 1) { // checks if this was the last move in the solutions
                    on_solved()
                    return
                }
                Log.d("chess", "opponent plays ${it[1]}")
                // plays opponents move and then removes the moves played from solution
                binding.chessView.play_move(it[1].first, it[1].second)
                solution = it.slice(2 until it.size)
            }
        }
    }

    private fun on_solved() {
        Log.d("on_move()", "You solved the puzzle!")
        viewModel.stopAlarmAudio()
        AlarmReceiver.mediaPlayer?.stop()
        finish()
    }

}

fun Activity.turnScreenOnAndKeyguardOff() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    } else {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }

    with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
        }
    }
}

fun Activity.turnScreenOffAndKeyguardOn() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(false)
        setTurnScreenOn(false)
    } else {
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }
}