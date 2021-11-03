package com.example.chessalarm2.chessproblemalarm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.chessalarm2.R

class ChessView @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var player: Player = Player.WHITE
    private var moveEnabled = true
    private val board = Chess()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 55.0f
            typeface = Typeface.create("", Typeface.BOLD)
            strokeWidth = 4.0f
        }
        val left = 0
        val right = (1-(left/width))*width
        val top = (height-right+left)/2
        val bottom = (height+right-left)/2
        paint.color = Color.GRAY
        canvas.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), paint)
        //canvas.drawLine(left.toFloat(), top.toFloat(), left.toFloat(), bottom.toFloat(), paint)
        val stepSize = (right-left) / BOARD_SIZE
        Log.d("onDraw", "stepSize="+stepSize.toString()+", "+"top="+top.toString()+", bottom="+bottom.toString())
        /*for (i in 0..BOARD_SIZE) {
            canvas.drawLine(left.toFloat(), (top-stepSize*i).toFloat(), right.toFloat(), (top-stepSize*i).toFloat(), paint)
            canvas.drawLine((left+stepSize*i).toFloat(), top.toFloat(), (left+stepSize*i).toFloat(), bottom.toFloat(), paint)
        }*/

        for (x in 0 until BOARD_SIZE) {
            for (y in 0 until BOARD_SIZE) {
                if ((x + y)%2 == 1) {
                    paint.color = context.getColor(R.color.square_black)
                } else {
                    paint.color = context.getColor(R.color.square_white)
                }
                canvas.drawRect((left+stepSize*x).toFloat(), (top+stepSize*y).toFloat(), (left+stepSize*(x+1)).toFloat(), (top+stepSize*(y+1)).toFloat(), paint)

                val (piece, player) = board[x][y]
                if (piece != Piece.EMPTY) {
                    val drawable = ResourcesCompat.getDrawable(resources, pieceToDrawable(piece, player), null)!!
                    drawable.setBounds(left+stepSize*x, top+stepSize*y, left+stepSize*(x+1), top+stepSize*(y+1))
                    drawable.draw(canvas)
                }
            }
        }
    }
}

private fun pieceToDrawable(piece: Piece, player: Player): Int {
    return when (player) {
        Player.WHITE -> when (piece) {
            Piece.BISHOP -> R.drawable.ic_wb
            Piece.KING -> R.drawable.ic_wk
            Piece.QUEEN -> R.drawable.ic_wq
            Piece.ROOK -> R.drawable.ic_wr
            Piece.PAWN -> R.drawable.ic_wp
            Piece.KNIGHT -> R.drawable.ic_wn
            else -> R.drawable.ic_wk // must never be reached
        }
        Player.BLACK -> when (piece) {
            Piece.BISHOP -> R.drawable.ic_bb
            Piece.KING -> R.drawable.ic_bk
            Piece.QUEEN -> R.drawable.ic_bq
            Piece.ROOK -> R.drawable.ic_br
            Piece.PAWN -> R.drawable.ic_bp
            Piece.KNIGHT -> R.drawable.ic_bn
            else -> R.drawable.ic_bk // must never be reached
        }
    }
}