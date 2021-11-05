package com.example.chessalarm2.chessproblemalarm

class Coordinate(val x: Int, val y: Int) {

    operator fun plus(other: Coordinate): Coordinate {
        return Coordinate(x+other.x, y+other.y)
    }

    operator fun times(k: Int): Coordinate {
        return Coordinate(x*k,y*k)
    }

    operator fun minus(other: Coordinate): Coordinate {
        return Coordinate(x-other.x, y-other.y)
    }

    override fun toString(): String {
        return "<"+x.toString()+", "+y.toString()+">"
    }
}