package com.example.tristan.battleship

import android.graphics.Point
import android.util.Log

/**
 * Created by Tristan on 11/3/2017.
 */
class PlayerObject(board: Array<Array<Int>>) {
    var boardMatrix : Array<Array<Int>> = board
    constructor() : this(Array(10, {i -> Array(10, {j -> 0})}))
    var shipHP : Array<Int> = arrayOf(0, 0, 0, 0, 0)
    var shipLoc : Array<Array<Point>> = Array(5, {i -> Array(5, {j -> Point(-1,-1)})})

    fun setBoard(board: Array<Array<Int>>) {
        boardMatrix = board
        setShipHP()
    }

    fun setShipHP() {
        shipHP = arrayOf(0, 0, 0, 0, 0)
        for (a in boardMatrix) {
            for (b in a) {
                if (b in 1..5)
                    shipHP[b-1] += 1
            }
        }
    }

    fun setShipPos() {
        var x = 0
        var y: Int
        var ships = Array(5, {i -> 0})
        for (a in boardMatrix) {
            y = 0
            for (b in a) {
                if (b in 1..5) {
                    shipLoc[b - 1][ships[b-1]] = Point(x, y)
                    ships[b-1] += 1
                }
                y += 1
            }
            x += 1
        }
    }


    fun aiTouch(x: Int, y: Int) {
        val s = boardMatrix[x][y]
        if (s == 0)
            boardMatrix[x][y] = 6
        else if (s in 1..5) {
            boardMatrix[x][y] = 7
            setShipHP()
            if (shipHP[s-1] == 0){
                for (i in shipLoc[s-1])
                    if (i.x != -1)
                        boardMatrix[i.x][i.y] = 8
            }
        }
        setShipHP()
    }
}