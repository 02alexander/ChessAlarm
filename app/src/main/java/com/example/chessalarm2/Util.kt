package com.example.chessalarm2

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chessalarm2.chessproblemalarm.Coordinate

class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

fun daysToString(days: List<Int>) : String {
    var res = ""
    if (days.isEmpty()) {
        res = "No days"
    } else {
        for (day in days) {
            res += when (day) {
                0 -> "mon "
                1 -> "tue "
                2 -> "wed "
                3 -> "thu "
                4 -> "fri "
                5 -> "sat "
                else -> "sun "
            }
        }
    }
    return res
}

fun parse_UCI(UCI: String): List<Pair<Coordinate, Coordinate>> {
    val moves_string = UCI.split(" ")
    val moves = mutableListOf<Pair<Coordinate, Coordinate>>()
    for (move_string in moves_string) {
        val src = Coordinate(move_string[0].code-'a'.code, 8-move_string[1].digitToInt())
        val dst = Coordinate(move_string[2].code-'a'.code, 8-move_string[3].digitToInt())
        moves.add(Pair(src,dst))
    }
    return moves
}