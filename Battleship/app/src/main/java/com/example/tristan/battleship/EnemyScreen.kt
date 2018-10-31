package com.example.tristan.battleship

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * Created by Tristan on 11/3/2017.x
 */

class EnemyScreen : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    var paint = Paint()
    var player = PlayerObject()
    var inval = 0
    set(value) {
        field = value
        invalidate()
    }
    var active = true
    var blackout = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event !is MotionEvent) {return false}
        if (!active) {return false}
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x.toInt() / (width / 10)
                val y = event.y.toInt() / (height / 10)
                val s = player.boardMatrix[x][y]
                if (s == 0)
                    player.boardMatrix[x][y] = 6
                else if (s in 1..5) {
                    player.boardMatrix[x][y] = 7
                    player.setShipHP()
                    if (player.shipHP[s-1] == 0){
                        for (i in player.shipLoc[s-1])
                            if (i.x != -1)
                                player.boardMatrix[i.x][i.y] = 8
                    }
                }
                else
                    return false
                inval = 0
                return true
            }
        }
        return false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if(canvas !is Canvas) { return }

        if(blackout) {
            paint.color = Color.BLACK
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
        else {
            var x = 0
            var y: Int
            for (a in player.boardMatrix) {
                y = 0
                for (b in a) {
                    if (b == -1)
                        paint.color = Color.BLACK
                    else if (b in 0..5)
                        paint.color = Color.LTGRAY
                    else if (b == 6)
                        paint.color = Color.BLUE
                    else if (b == 7)
                        paint.color = Color.RED
                    else if (b == 8)
                        paint.color = Color.parseColor("#A00000")
                    canvas.drawCircle(width.toFloat() / 10 * x + width.toFloat() / 20, height.toFloat() / 10 * y + height.toFloat() / 20, width.toFloat() / 20, paint)
                    y += 1
                }
                x += 1
            }
        }
    }
}
