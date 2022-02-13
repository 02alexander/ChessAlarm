package com.boss.chessalarm2.puzzlesource

import android.app.Application
import com.boss.chessalarm2.database.puzzles.Puzzle
import com.boss.chessalarm2.database.puzzles.PuzzlesDatabase
import com.opencsv.CSVReaderBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

            val database =
                PuzzlesDatabase.getInstance(application.applicationContext).puzzleDatabaseDao

            val applicationScope = CoroutineScope(SupervisorJob()+Dispatchers.Main)
            applicationScope.launch {
                val count = database.count()

                val file = application.assets.open("lichess_db_puzzle.csv").reader()
                val reader = CSVReaderBuilder(file).withSkipLines(count.toInt()).build()
                reader.skip(count.toInt())
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
                    database.insert(puzzle)
                }
                val new_count = database.count()
            }
        }
    }
}