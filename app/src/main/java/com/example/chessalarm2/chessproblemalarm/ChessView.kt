package com.example.chessalarm2.chessproblemalarm

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import com.example.chessalarm2.R
import java.util.*

class ChessView @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var moveEnabled = true
    val board = Chess()
    private var currently_selected: Coordinate? = null

    private var legal_moves: List<Coordinate>? = null

    private var prompt_promotion: Pair<Coordinate, Coordinate>? = null

    private var promotion_squares: List<Coordinate>? = null

    private var animationValue: Float? = null
    private var moveAnimationQueue: Queue<Triple<Coordinate, Coordinate, Pair<Piece, Player>>> = LinkedList()

    private var onChessMoveListener: (src: Coordinate, dst: Coordinate) -> Unit = ::on_move

    private var isInCooldown: Boolean = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val action = event.action
        val cord = xyToBoardCord(x, y)

        if (isInCooldown) {
            return super.onTouchEvent(event)
        }

        if (action == MotionEvent.ACTION_DOWN && board.is_cord_in_board(cord)) {
            val (piece, player) = board[cord]

            prompt_promotion?.let {
                val promotion_piece = getPromotionPiece(cord)
                promotion_piece?.let {
                    val (old_src, old_dst) = prompt_promotion!!
                    val (src, dst) = promotionMoveToMove(old_src, old_dst, it)
                    onChessMoveListener(src, dst)
                    prompt_promotion = null
                    promotion_squares = null
                }
                if (promotion_piece == null) {
                    promotion_squares = null
                    prompt_promotion = null
                    currently_selected = null
                    legal_moves = null
                }
            }

            legal_moves?.let {
                // removes focus of piece if user clicks on empty square
                if (!(cord in it)) {
                    promotion_squares = null
                    prompt_promotion = null
                    currently_selected = null
                    legal_moves = null
                } else if (board.cur_player == board[currently_selected!!].second) {
                    // on valid move clicked.

                    if (board.resultsInPromotion(currently_selected!!, cord)) {
                        prompt_promotion = Pair(currently_selected!!, cord)
                    } else {
                        onChessMoveListener(currently_selected!!, cord)
                    }
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

    private fun promotionMoveToMove(src: Coordinate, dst: Coordinate, piece: Piece): Pair<Coordinate, Coordinate> {
        return Pair(src, Coordinate(dst.x, Chess.eligiblePromotionPieceToInt(piece)))
    }

    fun setOnChessMoveListener(f: (src: Coordinate, dst: Coordinate) -> Unit) {
        this.onChessMoveListener = f
    }

    fun on_move(src: Coordinate, dst: Coordinate) {

    }

    fun loadFEN(FEN: String) {
        board.loadFEN(FEN)
        invalidate()
    }

    fun play_move(src: Coordinate, dst: Coordinate) {
        board.play_move(src, dst)
        if (!Chess.isPromotion(src, dst)) {
            moveAnimationQueue.add(Triple(src, dst, board[dst.x][dst.y]))
        }
        playNextAnimation()
    }

    fun playNextAnimation() {
        if (moveAnimationQueue.size == 0) {
            return
        }
        val moveValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        moveValueAnimator.duration = 200
        moveValueAnimator.doOnEnd {
            moveAnimationQueue.poll()
            animationValue = null
            playNextAnimation()
        }
        moveValueAnimator.addUpdateListener {
            animationValue = it.animatedValue as Float
            invalidate()
        }
        moveValueAnimator.start()
    }

    fun coolDownBoard(seconds: Int) {
        isInCooldown = true
        invalidate()
        Handler(Looper.getMainLooper()).postDelayed({
            isInCooldown = false
            invalidate()
        }, 1000*seconds.toLong())
    }

    fun indicateWrongMove(src: Coordinate, dst: Coordinate) {
        moveAnimationQueue.add(Triple(src, dst, board[src.x][src.y]))
        moveAnimationQueue.add(Triple(dst, src, board[src.x][src.y]))
        playNextAnimation()
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

    fun boardCordToxy(cord: Coordinate): Pair<Float, Float> {
        val left = 0
        val right = (1-(left/width))*width
        val top = (height-right+left)/2
        val bottom = (height+right-left)/2
        val stepSize = (right-left) / BOARD_SIZE
        return Pair((left+stepSize*cord.x).toFloat(), (top+stepSize*cord.y).toFloat())
    }

    private fun movingPiecexy(src: Coordinate, dst: Coordinate): Pair<Float, Float> {
        val srcxy = boardCordToxy(src)
        val dstxy = boardCordToxy(dst)
        val posx = srcxy.first+animationValue!!*(dstxy.first-srcxy.first)
        val posy = srcxy.second+animationValue!!*(dstxy.second-srcxy.second)
        return Pair(posx, posy)
    }

    // given that the prompt for promotion is open it
    // returns what piece was clicked if the user clicked on cord.
    private fun getPromotionPiece(cord: Coordinate): Piece? {
        promotion_squares?.let {
            for (square in it) {
                if (cord.equals(square)) {
                    val y = if (cord.y <= 3) {
                        cord.y
                    } else {
                        7-cord.y
                    }
                    return Chess.intToEligiblePromotionPiece(-y-1)
                }
            }
        }
        return null
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

                //canvas.drawRect((left+stepSize*x).toFloat(), (top+stepSize*y).toFloat(), (left+stepSize*(x+1)).toFloat(), (top+stepSize*(y+1)).toFloat(), paint)
                val topleft = boardCordToxy(Coordinate(x,y))
                val botright = boardCordToxy(Coordinate(x+1,y+1))
                canvas.drawRect(topleft.first, topleft.second, botright.first, botright.second, paint)

                if (currently_selected == Coordinate(x,y)) {
                    paint.color = context.getColor(R.color.square_selected)
                    //canvas.drawRect((left+stepSize*x).toFloat(), (top+stepSize*y).toFloat(), (left+stepSize*(x+1)).toFloat(), (top+stepSize*(y+1)).toFloat(), paint)
                    canvas.drawRect(topleft.first, topleft.second, botright.first, botright.second, paint)
                }
                if (moveAnimationQueue.size >= 1) {
                    val src = moveAnimationQueue.peek().first
                    val dst = moveAnimationQueue.peek().second
                    if (Coordinate(x, y) == dst || Coordinate(x,y) == src) {
                        continue
                    }
                }
                val (piece, player) = board[x][y]
                if (piece != Piece.EMPTY) {
                    val drawable = ResourcesCompat.getDrawable(resources, pieceToDrawable(piece, player), null)!!
                    //drawable.setBounds(left+stepSize*x, top+stepSize*y, left+stepSize*(x+1), top+stepSize*(y+1))
                    drawable.setBounds(topleft.first.toInt(), topleft.second.toInt(), botright.first.toInt(), botright.second.toInt())
                    drawable.draw(canvas)
                }
            }
        }

        // draws animation
        if (moveAnimationQueue.size >= 1) {
            val src = moveAnimationQueue.peek().first
            val dst = moveAnimationQueue.peek().second
            val (piece, player) = moveAnimationQueue.peek().third
            val mtopleft = movingPiecexy(src, dst)
            val mbotright = movingPiecexy(src+ Coordinate(1,1), dst+Coordinate(1,1))
            val drawable = ResourcesCompat.getDrawable(resources, pieceToDrawable(piece, player), null)!!
            drawable.setBounds(mtopleft.first.toInt(), mtopleft.second.toInt(), mbotright.first.toInt(), mbotright.second.toInt())
            drawable.draw(canvas)
        }

        // draws circles on legal moves.
        legal_moves?.let {
            for (move in it) {
                val x = move.x
                val y = move.y
                paint.color = context.getColor(R.color.legal_move_dot)
                canvas.drawCircle((left+stepSize*(x+0.5)).toFloat(), (top+stepSize*(y+0.5)).toFloat(), stepSize*0.15f,paint)
            }
        }

        // draws prompt for promotion
        prompt_promotion?.let { pair ->
            val (src, cord) = pair
            val player = board[src].second
            val d = if (cord.y == 0) { 1 } else { -1 }
            val psquares = mutableListOf<Coordinate>()
            for (i in 0..3) {
                psquares.add(cord+ Coordinate(0,i*d))
                val topleft = boardCordToxy(cord+ Coordinate(0, i*d))
                val botright = boardCordToxy(cord+Coordinate(1,i*d+1))
                val piece = Chess.intToEligiblePromotionPiece(-i-1)
                val drawable = ResourcesCompat.getDrawable(resources, pieceToDrawable(piece, player), null)!!

                paint.color = context.getColor(R.color.promote_background)
                canvas.drawRect(topleft.first, topleft.second, botright.first, botright.second, paint)

                drawable.setBounds(topleft.first.toInt(), topleft.second.toInt(), botright.first.toInt(), botright.second.toInt())
                drawable.draw(canvas)

            }
            promotion_squares = psquares
        }

        if (isInCooldown) {
            paint.color = context.getColor(R.color.cooldown)
            canvas.drawRect(0.0f,0.0f,height.toFloat(),width.toFloat(), paint)
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