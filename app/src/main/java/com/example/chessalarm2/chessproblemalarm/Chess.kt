package com.example.chessalarm2.chessproblemalarm

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import com.example.chessalarm2.R

const val BOARD_SIZE = 8;

enum class Player {
    WHITE,
    BLACK,
}

enum class Piece {
    PAWN,
    BISHOP,
    KNIGHT,
    KING,
    ROOK,
    QUEEN,
    EMPTY
}

class Chess() {

    var board : MutableList<MutableList<Pair<Piece, Player>>>
    var cur_player = Player.WHITE
    var castling_rights: Array<Boolean> = arrayOf(false, false, false, false) // (white queenside, white kingside, black queenside, black kingside)

    init {
        val result = mutableListOf<MutableList<Pair<Piece, Player>>>()
        for (i in 0..BOARD_SIZE) {
            val lst = mutableListOf<Pair<Piece, Player>>()
            for (j in 0..BOARD_SIZE) {
                lst.add(Pair(Piece.EMPTY, Player.WHITE))
            }
            result.add(lst)
        }
        board = result

        loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b - - 0 24")
        //board[0][0] = Pair(Piece.PAWN, Player.BLACK)
    }

    fun legal_moves(row: Int, col: Int): List<Pair<Int, Int>> {
        val (piece, player) = board[row][col]
        return when(piece) {
            Piece.KING -> legal_king_moves(row, col, player)
            Piece.KNIGHT -> legal_knight_moves(row, col, player)
            Piece.BISHOP -> legal_bishop_moves(row, col, player)
            Piece.QUEEN -> legal_queen_moves(row, col, player)
            Piece.PAWN -> legal_pawn_moves(row, col, player)
            Piece.ROOK -> legal_rook_moves(row, col, player)
            Piece.EMPTY -> listOf()
        }
    }

    fun legal_rook_moves(row: Int, col: Int, player: Player): List<Pair<Int, Int>> {
        return listOf()
    }

    fun legal_king_moves(row: Int, col: Int, player: Player): List<Pair<Int, Int>> {
        return listOf()
    }

    fun legal_queen_moves(row: Int, col: Int, player: Player): List<Pair<Int, Int>> {
        return listOf()
    }

    fun legal_pawn_moves(row: Int, col: Int, player: Player): List<Pair<Int, Int>> {
        return listOf()
    }

    fun legal_knight_moves(row: Int, col: Int, player: Player): List<Pair<Int, Int>> {
        return listOf()
    }

    fun legal_bishop_moves(row: Int, col: Int, player: Player): List<Pair<Int, Int>> {
        return listOf()
    }

    operator fun get(index: Int) = board[index]

    fun loadFEN(FEN: String) {
        val segments = FEN.split(" ")
        val piece_segment = segments[0]
        var cur_row = 0
        var cur_col = 0
        for (c in piece_segment) {
            if (c.isDigit()) {
                cur_col += c.digitToInt()
            } else if (c == '/') {
                cur_col = 0
                cur_row += 1
            } else {
                board[cur_col][cur_row] = when (c) {
                    'r' -> Pair(Piece.ROOK, Player.BLACK)
                    'p' -> Pair(Piece.PAWN, Player.BLACK)
                    'q' -> Pair(Piece.QUEEN, Player.BLACK)
                    'b' -> Pair(Piece.BISHOP, Player.BLACK)
                    'k' -> Pair(Piece.KING, Player.BLACK)
                    'n' -> Pair(Piece.KNIGHT, Player.BLACK)
                    'R' -> Pair(Piece.ROOK, Player.WHITE)
                    'P' -> Pair(Piece.PAWN, Player.WHITE)
                    'Q' -> Pair(Piece.QUEEN, Player.WHITE)
                    'B' -> Pair(Piece.BISHOP, Player.WHITE)
                    'K' -> Pair(Piece.KING, Player.WHITE)
                    'N' -> Pair(Piece.KNIGHT, Player.WHITE)
                    else -> Pair(Piece.KING, Player.BLACK)
                }
                cur_col += 1
            }
        }
        val starting = segments[1]
        if (starting == "w") {
            cur_player = Player.WHITE
        } else {
            cur_player = Player.BLACK
        }

        val s = segments[2]
        castling_rights = arrayOf(false, false, false, false)

        for (c in s) {
            if (c == 'Q') {
                castling_rights[0] = true
            } else if (c == 'q') {
                castling_rights[2] = true
            } else if (c == 'K') {
                castling_rights[1] = true
            } else if (c == 'k') {
                castling_rights[3] = true
            }
        }
    }
}