package com.example.chessalarm2.chessproblemalarm

class Coordinate {

    var x: Int = 0
    var y: Int = 0

    constructor(_x: Int, _y: Int) {
        x = _x
        y = _y
    }

    constructor(move_string: String) {
        val c = Coordinate(move_string[0].code-'a'.code, 8-move_string[1].digitToInt())
        x = c.x
        y = c.y
    }

    operator fun plus(other: Coordinate): Coordinate {
        return Coordinate(x+other.x, y+other.y)
    }

    operator fun times(k: Int): Coordinate {
        return Coordinate(x*k,y*k)
    }

    operator fun minus(other: Coordinate): Coordinate {
        return Coordinate(x-other.x, y-other.y)
    }

    override fun equals(other: Any?) = (other is Coordinate) && x==other.x && y==other.y

    override fun toString(): String {
        return "<"+x.toString()+", "+y.toString()+">"
    }

}