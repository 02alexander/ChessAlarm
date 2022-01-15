package com.example.chessalarm2.puzzlesource

import android.app.Application
import android.util.Log
import com.example.chessalarm2.database.puzzles.Puzzle
import com.example.chessalarm2.database.puzzles.PuzzlesDatabase
import com.opencsv.CSVReader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PopulateDatabase{

    companion object {

        private fun puzzleIdToLong(s: String): Long {
            var tot = 0L
            for (c in s) {
                tot = 128*tot + c.code.toLong()
            }
            return tot
        }

        fun populateDatabase(application: Application) {

            val database = PuzzlesDatabase.getInstance(application.applicationContext).puzzleDatabaseDao

            GlobalScope.launch {
                val count = database.count()
                Log.d("populateDatabase", "count=$count")
            }

            val file = application.assets.open("lichess_db_puzzle.csv").reader()
            val reader = CSVReader(file)
            for (line in reader) {
                val puzzleId = line[0]
                val FEN = line[1]
                val moves = line[2]
                val rating = line[3]
                val ratingDeviation = line[4]
                val popularity = line[5]
                val nbPlays = line[6]
                val themes = line[7]
                val gameUrl = line[8]

                val puzzle = Puzzle(
                    puzzleIdToLong(puzzleId),
                    FEN,
                    moves,
                    rating.toInt(),
                    ratingDeviation.toInt(),
                    popularity.toInt(),
                    nbPlays.toInt(),
                    false
                )
                GlobalScope.launch {
                    Log.d("populateDatabase", puzzle.toString())
                    database.insert(puzzle)
                }
            }
        }
    }
}