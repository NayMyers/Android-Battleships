package com.example.tristan.battleship

import android.content.Intent
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_play.*
import java.io.File
import java.util.Random


class PlayActivity : AppCompatActivity() {

    val rand = Random()
    var fileName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        fileName = intent.getStringExtra("SaveName")
        val first = intent.getBooleanExtra("First", false)
        intent.removeExtra("First")
        val dm: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        if (dm.heightPixels > dm.widthPixels) {
            enemyScreen.layoutParams.width = (dm.heightPixels * 0.4).toInt()
            enemyScreen.layoutParams.height = (dm.heightPixels * 0.4).toInt()
            playerScreen.layoutParams.width = (dm.heightPixels * 0.4).toInt()
            playerScreen.layoutParams.height = (dm.heightPixels * 0.4).toInt()
        }
        else {
            enemyScreen.layoutParams.width = (dm.widthPixels * 0.4).toInt()
            enemyScreen.layoutParams.height = (dm.widthPixels * 0.4).toInt()
            playerScreen.layoutParams.width = (dm.widthPixels * 0.4).toInt()
            playerScreen.layoutParams.height = (dm.widthPixels * 0.4).toInt()
        }
        val player1 = PlayerObject()
        val player2 = PlayerObject()
        var ai = false

        var gamestate = "Blackout"
        enemyScreen.blackout = true
        playerScreen.blackout = true
        continueButton.text = "Pass to player 1, then click."

        playerScreen.player = player1
        enemyScreen.player = player2
        var turn = 1

        val f = File(filesDir.path + "/" + fileName + ".txt")
        var board1 = Array<Array<Int>>(10, { i -> Array(10, { j -> 0 }) })
        var board2 = Array<Array<Int>>(10, { i -> Array(10, { j -> 0 }) })
        for (line in f.readLines()) {
            val parts = line.split(" ")
            if (parts[0] == "State")
                if (parts[2] == "up.") {
                    val i = Intent(this, SettingsActivity::class.java)
                    i.putExtra("SaveName", fileName)
                    i.putExtra("Load", true)
                    startActivity(i)
                }
            if (parts[0] == "AI")
                ai = true
            if (parts[0] == "Turn") {
                turn = parts[1].toInt()
                if (turn == 2) {
                    playerScreen.player = player2
                    enemyScreen.player = player1
                    continueButton.text = "Pass to player 2, then click."
                }
            }
            if (parts[0] == "P1") {
                var a = 2
                while (a < parts.size) {
                    board1[parts[1].toInt()][a-2] = parts[a].toInt()
                    a++
                }
            }
            if (parts[0] == "P2") {
                var a = 2
                while (a < parts.size) {
                    board2[parts[1].toInt()][a-2] = parts[a].toInt()
                    a++
                }
            }
            if (parts[0] == "S1") {
                var a = 1
                while (a <= 5) {
                    player1.shipLoc[parts[1].toInt()][a-1] = Point(parts[a*2].toInt(), parts[a*2 + 1].toInt())
                    //Log.e("P1Load x, y", parts[a*2] + ", " + parts[a*2+1])
                    a++
                }
            }
            if (parts[0] == "S2") {
                var a = 1
                while (a <= 5) {
                    player2.shipLoc[parts[1].toInt()][a-1] = Point(parts[a*2].toInt(), parts[a*2 + 1].toInt())
                    //Log.e("P2Load x, y", parts[a*2] + ", " + parts[a*2+1])
                    a++
                }
            }
        }
        player1.boardMatrix = board1
        player2.boardMatrix = board2
        player1.setShipHP()
        player2.setShipHP()
        if(first){
            player1.setShipPos()
            player2.setShipPos()
            save(fileName, player1, player2, turn, ai)
        }

        continueButton.setOnClickListener {
            if (gamestate == "Attacked" && ai){
                for (x in player1.shipHP)
                    if (x != 0) {
                        gamestate = "Attacking"
                        enemyScreen.active = true
                        playerScreen.inval = 0
                        enemyScreen.inval = 0
                        continueButton.text = "-------"
                        break
                    }
                    else {
                        gamestate = "Ended"
                        continueButton.text = "Game Over! Player " + turn.toString() + " Wins!"
                    }
                if(gamestate == "Ended") {
                    turn = 2
                    playerScreen.player = player2
                    enemyScreen.player = player1
                    playerScreen.inval = 0
                    enemyScreen.inval = 0
                    save(fileName, player1, player2, turn, ai, true)
                }
                else
                    save(fileName, player1, player2, turn, ai)
            }
            else if (gamestate == "Attacked"){
                gamestate = "Blackout"
                enemyScreen.blackout = true
                playerScreen.blackout = true
                if (turn == 2) {
                    playerScreen.player = player2
                    enemyScreen.player = player1
                    continueButton.text = "Pass to player 2, then click."
                }
                else{
                    playerScreen.player = player1
                    enemyScreen.player = player2
                    continueButton.text = "Pass to player 1, then click."
                }
                playerScreen.inval = 0
                enemyScreen.inval = 0
            }
            else if (gamestate == "Blackout") {
                gamestate = "Attacking"
                enemyScreen.blackout = false
                enemyScreen.active = true
                playerScreen.blackout = false
                playerScreen.inval = 0
                enemyScreen.inval = 0
                continueButton.text = "-------"
            }
        }
        enemyScreen.setOnTouchListener { v : View, m : MotionEvent ->
            if (v.onTouchEvent(m)) {
                for (x in enemyScreen.player.shipHP)
                    if (x != 0) {
                        gamestate = "Attacked"
                        continueButton.text = "Continue."
                        Log.e("AI", ai.toString())
                        if(turn == 1 && !ai)
                            turn = 2
                        else
                            turn = 1
                        break
                    }
                    else {
                        gamestate = "Ended"
                        continueButton.text = "Game Over! Player " + turn.toString() + " Wins!"
                    }
                enemyScreen.active = false
                if(ai)
                    ai(player1)
            }
            if(gamestate == "Ended")
                save(fileName, player1, player2, turn, ai, true)
            else
                save(fileName, player1, player2, turn, ai)
            true
        }
    }

    fun save(fileName: String, player1: PlayerObject, player2: PlayerObject, turn: Int, ai: Boolean, over: Boolean = false) {
        val file = File(filesDir.path + "/" + fileName + ".txt")
        file.writeText("Name " + fileName + '\n')
        if (over)
            file.appendText("State is over." + '\n')
        else
            file.appendText("State in progress." + '\n')
        file.appendText("Turn " + turn.toString() + '\n')
        if (ai) {
            file.appendText("AI" + '\n')
        }
        var b1 = ""
        var b2 = ""
        var i = 0
        var j = 0
        while (i < 10){
            b1 = "P1 " + i.toString()
            b2 = "P2 " + i.toString()
            j = 0
            while (j < 10) {
                b1 += " " + player1.boardMatrix[i][j]
                b2 += " " + player2.boardMatrix[i][j]
                j++
            }
            file.appendText(b1 + '\n')
            file.appendText(b2 + '\n')
            i++
        }
        var p1 = 0
        var p2 = 0
        for(a in player1.shipHP)
            if(a != 0)
                p1++
        for(a in player2.shipHP)
            if(a != 0)
                p2++
        file.appendText("Ship 1 " + p1.toString() + '\n')
        file.appendText("Ship 2 " + p2.toString() + '\n')
        i = 0
        j = 0
        while (i < 5) {
            b1 = "S1 " + i.toString()
            b2 = "S2 " + i.toString()
            j = 0
            while (j < 5) {
                b1 += " " + player1.shipLoc[i][j].x.toString() + " " + player1.shipLoc[i][j].y.toString()
                b2 += " " + player2.shipLoc[i][j].x.toString() + " " + player2.shipLoc[i][j].y.toString()
                //Log.e("P1Save x, y", player1.shipLoc[i][j].x.toString() + ", " + player1.shipLoc[i][j].y.toString())
                //Log.e("P2Save x, y", player2.shipLoc[i][j].x.toString() + ", " + player2.shipLoc[i][j].y.toString())
                j++
            }
            file.appendText(b1 + '\n')
            file.appendText(b2 + '\n')
            i++
        }
    }

    fun ai(player1: PlayerObject) {
        var hashit = false
        var possibleMoves = ArrayList<Point>()
        var hits = ArrayList<Point>()
        var x = 0
        var y = 0
        while(x < 10 && !hashit){
            y = 0
            while(y < 10 && !hashit) {
                if (player1.boardMatrix[x][y] == 7) {
                    hashit = true
                    hits = arrayListOf(Point(x,y))
                    possibleMoves = arrayListOf()
                }
                else if (player1.boardMatrix[x][y] in 0..5 && (x+y)%2 == 1) {
                    possibleMoves.add(Point(x, y))
                }
                y++
            }
            x++
        }
        while(hashit){
            val temp = hits[0]
            hashit = false
            hits = arrayListOf()
            if(temp.x+1 < 10) {
                if (player1.boardMatrix[temp.x + 1][temp.y] in 0..5)
                    possibleMoves.add(Point(temp.x+1, temp.y))
                else if (player1.boardMatrix[temp.x + 1][temp.y] == 7) {
                    hits.add(Point(temp.x+1, temp.y))
                    hashit = true
                }
            }
            if(temp.x-1 >= 0) {
                if (player1.boardMatrix[temp.x - 1][temp.y] in 0..5)
                    possibleMoves.add(Point(temp.x-1, temp.y))
            }
            if(temp.y+1 < 10) {
                if (player1.boardMatrix[temp.x][temp.y + 1] in 0..5)
                    possibleMoves.add(Point(temp.x, temp.y+1))
                else if (player1.boardMatrix[temp.x][temp.y+1] == 7) {
                    hits.add(Point(temp.x, temp.y+1))
                    hashit = true
                }
            }
            if(temp.y-1 >= 0) {
                if (player1.boardMatrix[temp.x][temp.y - 1] in 0..5)
                    possibleMoves.add(Point(temp.x, temp.y-1))
            }
        }
        val target = rand.nextInt(possibleMoves.size)
        player1.aiTouch(possibleMoves[target].x, possibleMoves[target].y)
    }
}
