package com.example.tristan.battleship

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.File
import java.util.Random

class SettingsActivity : AppCompatActivity() {
    val rand = Random()
    var placedShip = Array(5, {i -> false})
    var saveName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        saveName = intent.getStringExtra("SaveName")
        val load = intent.getBooleanExtra("Load", false)

        val dm: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        if (dm.heightPixels > dm.widthPixels) {
            preview.layoutParams.width = (dm.heightPixels * 0.4).toInt()
            preview.layoutParams.height = (dm.heightPixels * 0.4).toInt()
        }
        else {
            preview.layoutParams.width = (dm.widthPixels * 0.4).toInt()
            preview.layoutParams.height = (dm.widthPixels * 0.4).toInt()
        }
        var turn = 0
        val player1 = PlayerObject()
        val player2 = PlayerObject()
        var selected = 0
        var vertical = false
        var ai = false
        preview.player = player1
        if(load) {
            val f = File(filesDir.path + "/" + saveName + ".txt")
            for (line in f.readLines()) {
                val parts = line.split(" ")
                if (parts[0] == "AI")
                    ai = true
                if (parts[0] == "Turn") {
                    turn = parts[1].toInt()
                    if (parts[1].toInt() == 2)
                        preview.player = player2
                }
                if (parts[0] == "P1") {
                    var a = 2
                    while (a < parts.size) {
                        player1.boardMatrix[parts[1].toInt()][a - 2] = parts[a].toInt()
                        if (parts[a].toInt() in 1..5 && turn == 1)
                            placedShip[parts[a].toInt() -1] = true
                        a++
                    }
                }
                if (parts[0] == "P2") {
                    var a = 2
                    while (a < parts.size) {
                        player2.boardMatrix[parts[1].toInt()][a - 2] = parts[a].toInt()
                        if (parts[a].toInt() in 1..5 && turn == 2)
                            placedShip[parts[a].toInt() -1] = true
                        a++
                    }
                }
            }
            shipOne.text = "Place Destroyer (2)"
            shipTwo.text = "Place Submarine (3)"
            shipThree.visibility = View.VISIBLE
            shipFour.visibility = View.VISIBLE
            shipFive.visibility = View.VISIBLE
            preview.visibility = View.VISIBLE
            randomPlacement.visibility = View.VISIBLE
            preview.active = true
            preview.inval = 0
        }

        shipOne.setOnClickListener {
            if(turn == 0) {
                intent.putExtra("Load", true)
                turn = 1
                ai = true
                shipOne.text = "Place Destroyer (2)"
                shipTwo.text = "Place Submarine (3)"
                shipThree.visibility = View.VISIBLE
                shipFour.visibility = View.VISIBLE
                shipFive.visibility = View.VISIBLE
                preview.visibility = View.VISIBLE
                randomPlacement.visibility = View.VISIBLE
                preview.active = true
                preview.player = player1
            }
            else if(selected != 1){
                selected = 1
                shipOne.text = "Change Orientation"
                shipTwo.text = "Place Submarine (3)"
                shipThree.text = "Place Cruiser (3)"
                shipFour.text = "Place Battleship (4)"
                shipFive.text = "Place Carrier (5)"
                vertical = false
                wherePlaceable(preview.player, 2, vertical)
            }
            else {
                vertical = !vertical
                shipOne.text = "Change orientation."
                wherePlaceable(preview.player, 2, vertical)
            }
            preview.inval = 0
            save(saveName, player1, player2, turn, ai)
        }

        shipTwo.setOnClickListener {
            if(turn == 0) {
                intent.putExtra("Load", true)
                turn = 1
                shipOne.text = "Place Destroyer (2)"
                shipTwo.text = "Place Submarine (3)"
                shipThree.visibility = View.VISIBLE
                shipFour.visibility = View.VISIBLE
                shipFive.visibility = View.VISIBLE
                preview.visibility = View.VISIBLE
                randomPlacement.visibility = View.VISIBLE
                preview.active = true
            }
            else if(selected != 2){
                selected = 2
                vertical = false
                shipOne.text = "Place Destroyer (2)"
                shipTwo.text = "Change orientation."
                shipThree.text = "Place Cruiser (3)"
                shipFour.text = "Place Battleship (4)"
                shipFive.text = "Place Carrier (5)"
                wherePlaceable(preview.player, 3, vertical)
            }
            else {
                vertical = !vertical
                shipTwo.text = "Change orientation."
                wherePlaceable(preview.player, 3, vertical)
            }
            preview.inval = 0
            save(saveName, player1, player2, turn, ai)
        }

        shipThree.setOnClickListener {
            if(selected != 3){
                selected = 3
                vertical = false
                shipOne.text = "Place Destroyer (2)"
                shipTwo.text = "Place Submarine (3)"
                shipThree.text = "Change orientation."
                shipFour.text = "Place Battleship (4)"
                shipFive.text = "Place Carrier (5)"
            }
            else {
                vertical = !vertical
                shipThree.text = "Change orientation."
            }
            wherePlaceable(preview.player, 3, vertical)
            preview.inval = 0
            save(saveName, player1, player2, turn, ai)
        }

        shipFour.setOnClickListener {
            if(selected != 4){
                selected = 4
                vertical = false
                shipOne.text = "Place Destroyer (2)"
                shipTwo.text = "Place Submarine (3)"
                shipThree.text = "Place Cruiser (3)"
                shipFour.text = "Change orientation."
                shipFive.text = "Place Carrier (5)"
            }
            else {
                vertical = !vertical
                shipFour.text = "Change orientation."
            }
            wherePlaceable(preview.player, 4, vertical)
            preview.inval = 0
            save(saveName, player1, player2, turn, ai)
        }

        shipFive.setOnClickListener {
            if(selected != 5){
                selected = 5
                vertical = false
                shipOne.text = "Place Destroyer (2)"
                shipTwo.text = "Place Submarine (3)"
                shipThree.text = "Place Cruiser (3)"
                shipFour.text = "Place Battleship (4)"
                shipFive.text = "Change orientation."
            }
            else {
                vertical = !vertical
                shipFive.text = "Change orientation."
            }
            wherePlaceable(preview.player, 5, vertical)
            preview.inval = 0
            save(saveName, player1, player2, turn, ai)
        }

        randomPlacement.setOnClickListener {
            randomShip(preview.player)
            shipOne.text = "Place Destroyer (2)"
            shipTwo.text = "Place Submarine (3)"
            shipThree.text = "Place Cruiser (3)"
            shipFour.text = "Place Battleship (4)"
            shipFive.text = "Place Carrier (5)"
            if (turn == 1) {
                turn = 2
                placedShip = Array(5, {i -> false})
                preview.player = player2
                if(ai) {
                    preview.blackout = true
                    randomShip(preview.player)
                    save(saveName, player1, player2, 1, ai, true)
                    val i = Intent(this, PlayActivity::class.java)
                    i.putExtra("SaveName", saveName)
                    i.putExtra("First", true)
                    startActivity(i)
                }
                else
                    save(saveName, player1, player2, 1, ai, false)
            }
            else {
                preview.blackout = true
                save(saveName, player1, player2, 1, ai, true)
                val i = Intent(this, PlayActivity::class.java)
                i.putExtra("SaveName", saveName)
                i.putExtra("First", true)
                startActivity(i)
            }
            preview.inval = 0
        }

        preview.setOnTouchListener { _ : View, m : MotionEvent ->
            val x = m.x.toInt() / (preview.width / 10)
            val y = m.y.toInt() / (preview.height / 10)
            if( x > 9 || y > 9)
                return@setOnTouchListener false
            val l = preview.player.boardMatrix[x][y]
            var s = selected
            if (s < 3)
                s += 1
            if (l == 0 && selected != 0){
                if(placeable(preview.player, s, x, y, vertical)) {
                    resetPlaceable(preview.player, selected)
                    placeShip(preview.player, s, x, y, vertical, selected)
                    placedShip[selected-1] = true
                }
            }
            preview.inval = 0
            save(saveName, player1, player2, turn, ai)
            true
        }
    }

    fun save(fileName: String, player1: PlayerObject, player2: PlayerObject, turn: Int, ai: Boolean, done: Boolean = false) {
        val file = File(filesDir.path + "/" + fileName + ".txt")
        file.writeText("Name " + fileName + '\n')
        if(done)
            file.appendText("State in progress." + '\n')
        else
            file.appendText("State setting up." + '\n')
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
    }

    fun placeShip(player: PlayerObject, length : Int, x : Int, y : Int, v : Boolean, s : Int) : Boolean {
        if(placeable(player, length, x, y, v)) {
            var i = 0
            if(v){
                while (i < length) {
                    player.boardMatrix[x][y+i] = s
                    i++
                }
            }
            else {
                while (i < length) {
                    player.boardMatrix[x+i][y] = s
                    i++
                }
            }
            return true
        }
        return false
    }

    fun randomShip(player : PlayerObject) {
        resetPlaceable(player, -1)
        var x = 0
        var y = 0
        var placed = false
        if(!placedShip[4]) {
            while (!placed) {
                if (rand.nextInt(2) == 0) {
                    x = rand.nextInt(6)
                    y = rand.nextInt(10)
                    placed = placeShip(player, 5, x, y, false, 5)
                } else {
                    x = rand.nextInt(10)
                    y = rand.nextInt(6)
                    placed = placeShip(player, 5, x, y, true, 5)
                }
            }
        }
        if(!placedShip[3]) {
            placed = false
            while (!placed) {
                if (rand.nextInt(2) == 0) {
                    x = rand.nextInt(7)
                    y = rand.nextInt(10)
                    placed = placeShip(player, 4, x, y, false, 4)
                }
                else {
                    x = rand.nextInt(10)
                    y = rand.nextInt(7)
                    placed = placeShip(player, 4, x, y, true, 4)
                }
            }
        }
        if(!placedShip[2]) {
            placed = false
            while(!placed) {
                if (rand.nextInt(2) == 0) {
                    x = rand.nextInt(8)
                    y = rand.nextInt(10)
                    placed = placeShip(player, 3, x, y, false, 3)
                }
                else {
                    x = rand.nextInt(10)
                    y = rand.nextInt(8)
                    placed = placeShip(player, 3, x, y, true, 3)
                }
            }
        }
        if(!placedShip[1]) {
            placed = false
            while(!placed) {
                if (rand.nextInt(2) == 0) {
                    x = rand.nextInt(8)
                    y = rand.nextInt(10)
                    placed = placeShip(player, 3, x, y, false, 2)
                } else {
                    x = rand.nextInt(10)
                    y = rand.nextInt(8)
                    placed = placeShip(player, 3, x, y, true, 2)
                }
            }
        }
        if(!placedShip[0]) {
            placed = false
            while (!placed) {
                if (rand.nextInt(2) == 0) {
                    x = rand.nextInt(9)
                    y = rand.nextInt(10)
                    placed = placeShip(player, 2, x, y, false, 1)
                }
                else {
                    x = rand.nextInt(10)
                    y = rand.nextInt(9)
                    placed = placeShip(player, 2, x, y, true, 1)
                }
            }
        }
    }

    fun resetPlaceable(player: PlayerObject, remove : Int) {
        var i = 0
        var j = 0
        while (i < 10){
            j = 0
            while(j < 10){
                if (player.boardMatrix[i][j] == remove) {
                    player.boardMatrix[i][j] = 0
                }
                j++
            }
            i++
        }
    }

    fun wherePlaceable(player: PlayerObject, ship : Int, v : Boolean) {
        resetPlaceable(player, -1)
        var x = 0
        var y : Int
        for (i in player.boardMatrix) {
            y = 0
            for (j in i) {
                if (!placeable(player, ship, x, y, v))
                    if(player.boardMatrix[x][y] == 0)
                        player.boardMatrix[x][y] = -1
                y++
            }
            x++
        }
    }

    fun placeable(player: PlayerObject, ship : Int, x : Int, y : Int, v : Boolean) : Boolean {
        var i = 0
        if (v) {
            while (i < ship) {
                if (x > 9 || y+i > 9 || player.boardMatrix[x][y+i] !in -1..0)
                    return false
                i += 1
            }
        }
        else {
            while (i < ship) {
                if (x + i > 9 || y > 9 || player.boardMatrix[x+i][y] !in -1..0)
                    return false
                i += 1
            }
        }
        return true
    }
}
