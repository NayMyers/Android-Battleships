package com.example.tristan.battleship

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by Tristan on 11/3/2017.
 */

class PlayerScreen : View {
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
    var blackout = false
    var active = false

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
                    else if (b == 0)
                        paint.color = Color.LTGRAY
                    else if (b in 1..5)
                        paint.color = Color.DKGRAY
                    else if (b == 6)
                        paint.color = Color.BLUE
                    else if (b == 7)
                        paint.color = Color.RED
                    else if (b == 8)
                        paint.color = Color.parseColor("#D00000")
                    else
                        paint.color = Color.YELLOW
                    canvas.drawCircle(width.toFloat() / 10 * x + width.toFloat() / 20, height.toFloat() / 10 * y + height.toFloat() / 20, width.toFloat() / 20, paint)
                    y += 1
                }
                x += 1
            }
        }
    }
}