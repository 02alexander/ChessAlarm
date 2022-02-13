package com.boss.chessalarm2

import android.content.Context
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boss.chessalarm2.chessproblemalarm.Chess
import com.boss.chessalarm2.chessproblemalarm.Coordinate
import com.boss.chessalarm2.chessproblemalarm.Piece

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
        val src = Coordinate(move_string.slice(0..1))
        val dst = Coordinate(move_string.slice(2..3))
        if (move_string.length == 5) {
            val piece = when(move_string[4]) {
                'q' -> Piece.QUEEN
                'n' -> Piece.KNIGHT
                'r' -> Piece.ROOK
                'b' -> Piece.BISHOP
                else -> Piece.QUEEN
            }
            dst.y = Chess.eligiblePromotionPieceToInt(piece)
        }
        moves.add(Pair(src,dst))
    }
    return moves
}

private fun getAudioPath(context: Context, id: Long) : String? {
    val uri: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
    val type = MediaStore.Audio.Media.IS_ALARM
    val selection = "$type != 0 AND ${MediaStore.Audio.Media._ID} == $id"
    val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
    val cursor: Cursor? = context.contentResolver.query(
        uri,
        null,
        selection,
        null,
        sortOrder
    )

    if (cursor!= null && cursor.moveToFirst()){
        val path:Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
        val audioPath: String = cursor.getString(path)
        return audioPath
    }
    return null
}

fun playAudioFromId(context: Context, mediaPlayer: MediaPlayer, id: Long) {
    var new_id = 0L
    if (id == -1L) {
        val sound = getDefaultSound(context)
        new_id = sound.id
    } else {
        new_id = id
    }
    val path = getAudioPath(context, new_id)
    val uri = Uri.parse("file:///"+path)
    //mediaPlayer.reset()
    mediaPlayer.setDataSource(context, uri)
    mediaPlayer.prepare()
    mediaPlayer.start()
}

fun getAlarmSounds(context: Context) : List<Sound> {
    val list:MutableList<Sound> = mutableListOf()

    val uri: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI

    val type = MediaStore.Audio.Media.IS_ALARM
    val selection = "$type != 0"
    val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
    val cursor: Cursor? = context.contentResolver.query(
        uri, // Uri
        null, // Projection
        selection, // Selection
        null, // Selection arguments
        sortOrder // Sort order
    )
    if (cursor!= null && cursor.moveToFirst()){
        val path:Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
        val id:Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
        val title:Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)

        do {
            val audioPath: String = cursor.getString(path)
            val audioTitle:String = cursor.getString(title)
            val audioId:Long = cursor.getLong(id)
            list.add(Sound(audioId, audioTitle, audioPath))
        }while (cursor.moveToNext())
    }
    return list
}

fun getDefaultSound(context: Context) : Sound {
    val sounds = getAlarmSounds(context)
    return sounds[0]
}

data class Sound(val id: Long, val title:String, val path: String)