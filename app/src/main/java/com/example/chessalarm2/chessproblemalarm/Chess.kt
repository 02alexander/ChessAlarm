package com.example.chessalarm2.chessproblemalarm

import android.util.Log

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

    var board : MutableList<MutableList<Pair<Piece, Player>>> // indexed by board[col][row]
    var cur_player = Player.WHITE
    var castling_rights: Array<Boolean> = arrayOf(false, false, false, false) // (white queenside, white kingside, black queenside, black kingside)
    var threatened_by_black: MutableList<MutableList<Boolean>> // not needed
    var threatened_by_white: MutableList<MutableList<Boolean>> // not needed
    var en_passant_target: Coordinate? = null
    var halfmove_counter: Int = 0
    var fullmove_counter: Int = 0

    init {
        board = create_blank_board(Pair(Piece.EMPTY, Player.BLACK))
        threatened_by_black = create_blank_board(false)
        threatened_by_white = create_blank_board(false)

        //Log.d("Chess() init", parse_UCI("d3d6 f8d8 d6d8 f6d8").toString())
        //parse_FEN("5rk1/1p3ppp/pq3b2/8/8/1P1Q1N2/P4PPP/3R2K1 w - - 2 27")
        //parse_FEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 24")
    }

    fun move_piece(src: Coordinate, dst: Coordinate) {
        this[dst] = this[src]
        this[src] = Pair(Piece.EMPTY, Player.BLACK)
        cur_player = when(cur_player) {
            Player.BLACK -> Player.WHITE
            Player.WHITE -> Player.BLACK
        }
    }

    fun legal_moves(cord: Coordinate): List<Coordinate> {
        val (piece, player) = this[cord]
        return when(piece) {
            Piece.KING -> legal_king_moves(cord, player)
            Piece.KNIGHT -> legal_knight_moves(cord, player)
            Piece.BISHOP -> legal_bishop_moves(cord, player)
            Piece.QUEEN -> legal_queen_moves(cord, player)
            Piece.PAWN -> legal_pawn_moves(cord, player)
            Piece.ROOK -> legal_rook_moves(cord, player)
            Piece.EMPTY -> listOf()
        }
    }

    // the following functions doesn't take into account that a move might be be illegal because the piece is protecting the king.

    fun legal_king_moves(cord: Coordinate, player: Player): List<Coordinate> {
        val directions = listOf(
            Coordinate(1,0),Coordinate(0,1),
            Coordinate(-1,0), Coordinate(0,-1),
            Coordinate(1,1),Coordinate(-1,1),
            Coordinate(-1,-1), Coordinate(1,-1)
        )
        val moves = mutableListOf<Coordinate>()
        for (dir in directions) {
            if (!is_cord_in_board(cord+dir) || (this[cord+dir].first != Piece.EMPTY && this[cord+dir].second == player)) {
                continue
            } else if (this[cord+dir].first != Piece.EMPTY) {
                moves.add(cord+dir)
                continue
            }
            moves.add(cord+dir)
        }
        return moves
    }

    fun legal_rook_moves(cord: Coordinate, player: Player): List<Coordinate> {
        val directions = listOf(Coordinate(1,0),Coordinate(0,1),Coordinate(-1,0), Coordinate(0,-1))
        val moves = mutableListOf<Coordinate>()
        for (dir in directions) {
            for (k in 1 until BOARD_SIZE) {
                if (!is_cord_in_board(cord+dir*k) || (this[cord+dir*k].first != Piece.EMPTY && this[cord+dir*k].second == player)) {
                    break
                } else if (this[cord+dir*k].first != Piece.EMPTY) {
                    moves.add(cord+dir*k)
                    break
                }
                moves.add(cord+dir*k)
            }
        }
        return moves
    }

    fun legal_queen_moves(cord: Coordinate, player: Player): List<Coordinate> {
        return listOf(legal_bishop_moves(cord,player), legal_rook_moves(cord,player)).flatten()
    }

    fun legal_pawn_moves(cord: Coordinate, player: Player): List<Coordinate> {
        // TODO : can't en passant right now

        val moves = mutableListOf<Coordinate>()
        if (player == Player.BLACK) {
            if (this[cord+Coordinate(0,1)].first == Piece.EMPTY) {
                moves.add(cord+ Coordinate(0,1))
                if (cord.y == 1 && this[cord+Coordinate(0,2)].first == Piece.EMPTY) {
                    moves.add(cord+ Coordinate(0,2))
                }
            }
            if (is_cord_in_board(cord+Coordinate(1,1)) &&
                this[cord+Coordinate(1,1)].second == Player.WHITE &&
                this[cord+Coordinate(1,1)].first != Piece.EMPTY) {
                moves.add(cord+Coordinate(1,1))
            }
            if (is_cord_in_board(cord+Coordinate(-1,1)) &&
                this[cord+Coordinate(-1,1)].second == Player.WHITE &&
                this[cord+Coordinate(-1,1)].first != Piece.EMPTY) {
                moves.add(cord+Coordinate(-1,1))
            }
        } else {
            if (this[cord+Coordinate(0,-1)].first == Piece.EMPTY) {
                moves.add(cord+ Coordinate(0,-1))
                if (cord.y == 6 && this[cord+Coordinate(0,-2)].first == Piece.EMPTY) {
                    moves.add(cord+Coordinate(0,-2))
                }
            }
            if (is_cord_in_board(cord+Coordinate(1,-1)) &&
                this[cord+Coordinate(1,-1)].second == Player.BLACK &&
                this[cord+Coordinate(1,-1)].first != Piece.EMPTY) {
                moves.add(cord+Coordinate(1,-1))
            }
            if (is_cord_in_board(cord+Coordinate(-1,-1)) &&
                this[cord+Coordinate(-1,-1)].second == Player.BLACK &&
                this[cord+Coordinate(-1,-1)].first != Piece.EMPTY) {
                moves.add(cord+Coordinate(-1,-1))
            }
        }
        return moves
    }

    fun legal_knight_moves(cord: Coordinate, player: Player): List<Coordinate> {
        val directions = listOf<Coordinate>(
            Coordinate(2,1),Coordinate(2,-1),
            Coordinate(-2,1),Coordinate(-2,-1),
            Coordinate(1, 2), Coordinate(-1, 2),
            Coordinate(1, -2),Coordinate(-1, -2)
        )
        val moves = mutableListOf<Coordinate>()
        for (dir in directions) {
            if (is_cord_in_board(cord+dir) && (this[cord+dir].first == Piece.EMPTY || this[cord+dir].second != player)) {
                moves.add(cord+dir)
            }
        }
        return moves
    }

    fun legal_bishop_moves(cord: Coordinate, player: Player): List<Coordinate> {
        val directions = listOf(Coordinate(1,1),Coordinate(-1,1),Coordinate(-1,-1), Coordinate(1,-1))
        val moves = mutableListOf<Coordinate>()
        for (dir in directions) {
            for (k in 1 until BOARD_SIZE) {
                if (!is_cord_in_board(cord+dir*k) || (this[cord+dir*k].first != Piece.EMPTY && this[cord+dir*k].second == player)) {
                    break
                } else if (this[cord+dir*k].first != Piece.EMPTY) {
                    moves.add(cord+dir*k)
                    break
                }
                moves.add(cord+dir*k)
            }
        }
        return moves
    }

    fun legal_piece_moves(cord: Coordinate, player: Player, piece: Piece): List<Coordinate> {
        return when(piece) {
            Piece.BISHOP -> legal_bishop_moves(cord, player)
            Piece.EMPTY -> listOf()
            Piece.KING -> legal_king_moves(cord,player)
            Piece.KNIGHT -> legal_knight_moves(cord, player)
            Piece.PAWN -> legal_pawn_moves(cord, player)
            Piece.QUEEN -> legal_queen_moves(cord, player)
            Piece.ROOK -> legal_rook_moves(cord, player)
        }
    }

    fun is_in_checkmate(player: Player): Boolean {
        if (!is_in_check(player)) {
            return false
        }
        for (x in 0..7) {
            for (y in 0..7) {
                val cord = Coordinate(x, y)
                if (get(cord).second != player) {
                    continue
                }
                val piece = get(cord).first

                val moves = legal_piece_moves(cord, player, piece)
                for (move in moves) {
                    val cpy = copy()
                    cpy.move_piece(cord, move)
                    if (!cpy.is_in_check(player)) {
                        Log.d("chess", "not checkmate: <$cord, $move>")
                        return false
                    }
                }
            }
        }
        return true
    }

    fun is_in_check(player: Player): Boolean {
        return is_piece_threatened(get_king_location(player)!!, player)
    }

    private fun get_king_location(player: Player): Coordinate? {
        for (x in 0..7) {
            for (y in 0..7) {
                val cord = Coordinate(x, y)
                if (get(cord).first == Piece.KING && get(cord).second == player) {
                    return cord
                }
            }
        }
        return null
    }

    private fun is_piece_threatened(cord: Coordinate, player: Player): Boolean {

        // checks for every piece which results in redundant checking (combine queen, rook, bishop)
        val pieces = listOf<Piece>(Piece.BISHOP, Piece.KING, Piece.KNIGHT, Piece.PAWN, Piece.QUEEN, Piece.ROOK)
        for (piece in pieces) {
            for (move in legal_piece_moves(cord, player, piece)) {
                if (get(move).first == piece && get(move).second != player) {
                    return true
                }
            }
        }
        return false
    }

    fun copy(): Chess {
        val cpy = Chess()
        cpy.board = mutableListOf()
        for (c in board) {
            val l = mutableListOf<Pair<Piece, Player>>()
            l.addAll(c)
            cpy.board.add(l)
        }
        cpy.cur_player = cur_player
        cpy.castling_rights = castling_rights
        cpy.en_passant_target = en_passant_target
        cpy.halfmove_counter = halfmove_counter
        cpy.fullmove_counter = fullmove_counter
        return cpy
    }

    fun is_cord_in_board(cord: Coordinate): Boolean {
        return cord.x in 0 until BOARD_SIZE && cord.y in 0 until BOARD_SIZE
    }

    private fun move(src: Coordinate, dst: Coordinate) {
        this[dst] = this[src]
        this[src] = Pair(Piece.EMPTY, Player.BLACK)
    }

    private fun <T> create_blank_board(v: T): MutableList<MutableList<T>>{
        val result = mutableListOf<MutableList<T>>()
        for (i in 0..BOARD_SIZE) {
            val lst = mutableListOf<T>()
            for (j in 0..BOARD_SIZE) {
                lst.add(v)
            }
            result.add(lst)
        }
        return result
    }

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

        val castling_str = segments[2]
        castling_rights = arrayOf(false, false, false, false)
        for (c in castling_str) {
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

        val en_passant_string = segments[3]
        if (en_passant_string != "-") {
            en_passant_target = Coordinate(en_passant_string)
        }

        val halfmove_string = segments[4]
        halfmove_counter = halfmove_string.toInt()

        val fullmove_string = segments[5]
        fullmove_counter = fullmove_string.toInt()
    }

    operator fun get(index: Int) = board[index]

    operator fun get(cord: Coordinate) = board[cord.x][cord.y]
    operator fun set(cord: Coordinate, v: Pair<Piece, Player>) {
        board[cord.x][cord.y] = v
    }
}