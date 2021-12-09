package com.example.chessalarm2.chessproblemalarm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.chessalarm2.R
import com.example.chessalarm2.parse_UCI

class ChessView @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var moveEnabled = true
    private val board = Chess()
    private var currently_selected: Coordinate? = null

    private var legal_moves: List<Coordinate>? = null

    //private var solution: List<Pair<Coordinate, Coordinate>>? = null
    private var solution: List<Pair<Coordinate, Coordinate>> = parse_UCI("d3d6 f8d8 d6d8 f6d8")

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    init {
        Log.d("init", "played first move")
        board.move_piece(solution[0].first, solution[0].second)
        solution = solution.slice(1 until solution.size)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val action = event.action
        val cord = xyToBoardCord(x, y)
        if (action == MotionEvent.ACTION_DOWN && board.is_cord_in_board(cord)) {
            val (piece, player) = board[cord]
            //Log.d("sdfgsdf", "cord="+cord.toString())

            // removes focus of piece if user clicks on empty square
            legal_moves?.let {
                if (!(cord in it)) {
                    currently_selected = null
                    legal_moves = null
                } else if (board.cur_player == board[currently_selected!!].second) {
                    on_move(currently_selected!!, cord)
                    currently_selected = null
                    legal_moves = null
                }
            }

            // checks if user selects one of its own pieces.
            if (board[cord].first != Piece.EMPTY && board[cord].second == board.cur_player) {
                currently_selected = cord
                legal_moves = board.legal_moves(cord)
            }
            invalidate()
        }
        return super.onTouchEvent(event)
    }

    fun on_move(src: Coordinate, dst: Coordinate) {
        if (solution[0].first != src || solution[0].second != dst) {
            Log.d("on_move()", "wrong move, correct move is "+solution[0].first.toString()+", "+solution[1].second.toString())
        } else {
            board.move_piece(src, dst)
            if (solution.size <= 1) { // checks if this was the last move in the solutions
                Log.d("on_move()", "End of solution")
                return
            }

            // plays opponents move and then removes the moves played from solution
            board.move_piece(solution[1].first, solution[1].second)
            solution = solution.slice(2 until solution.size)
        }
    }

    // returns Pair(board_x,board_y) of cord and Pair(-1,-1) if x,y is outside of board
    fun xyToBoardCord(x: Float, y: Float): Coordinate {
        val left = 0
        val right = (1-(left/width))*width
        val top = (height-right+left)/2
        val stepSize = (right-left) / BOARD_SIZE
        val y = y-top

        val cx = x/stepSize
        val cy = y/stepSize
        if (cx > BOARD_SIZE || cy > BOARD_SIZE || cx < 0 || cy < 0) {
            return Coordinate(-1,-1) // not a valid board coordinate
        }
        return Coordinate(cx.toInt(), cy.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        // TODO : animate moves

        super.onDraw(canvas)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 55.0f
            typeface = Typeface.create("", Typeface.BOLD)
            strokeWidth = 4.0f
            alpha = 255
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

                if (currently_selected == Coordinate(x,y)) {
                    paint.color = context.getColor(R.color.square_selected)
                    canvas.drawRect((left+stepSize*x).toFloat(), (top+stepSize*y).toFloat(), (left+stepSize*(x+1)).toFloat(), (top+stepSize*(y+1)).toFloat(), paint)
                    Log.d("onDraw", "currently_selected = "+currently_selected.toString())
                }

                val (piece, player) = board[x][y]
                if (piece != Piece.EMPTY) {
                    val drawable = ResourcesCompat.getDrawable(resources, pieceToDrawable(piece, player), null)!!
                    drawable.setBounds(left+stepSize*x, top+stepSize*y, left+stepSize*(x+1), top+stepSize*(y+1))
                    drawable.draw(canvas)
                }
            }
        }

        legal_moves?.let {
            for (move in it) {
                val x = move.x
                val y = move.y
                paint.color = context.getColor(R.color.legal_move_dot)
                canvas.drawCircle((left+stepSize*(x+0.5)).toFloat(), (top+stepSize*(y+0.5)).toFloat(), stepSize*0.15f,paint)
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