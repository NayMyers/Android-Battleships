package com.example.tristan.battleship

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_load.*
import java.io.File

class LoadActivity : AppCompatActivity() {

    var del = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)
        var over = false
        var fileNum = 0
        var ai = false
        var dir = true
        val ar = ArrayList<String>()
        val sn = ArrayList<String>()
        if(!filesDir.exists())
            filesDir.mkdir()
        else
            filesDir.walkTopDown().forEach {
                if(dir)
                    dir = false
                else {
                    over = false
                    ai = false
                    var tempString = ""
                    for (line in it.readLines()) {
                        val parts = line.split(" ")
                        if (parts[0] == "Name") {
                            if (fileNum <= parts[1].toInt())
                                fileNum = parts[1].toInt() + 1
                            sn.add(parts[1])
                        }
                        if (parts[0] == "State") {
                            if (parts[2] == "over.")
                                over = true
                            tempString += "Game " + parts[1] + " " + parts[2] + '\n'
                        }
                        else if (parts[0] == "Turn") {
                            if(over)
                                tempString += "Player " + parts[1] + " has won." + '\n'
                            else
                                tempString += "It is Player " + parts[1] + "'s turn." + '\n'
                        }
                        else if (parts[0] == "AI") {
                            ai = true
                            tempString += "Vs AI" + '\n'
                        }
                        else if (parts[0] == "Ship") {
                            tempString += " Player " + parts[1] + " has " + parts[2] + " ships left." + '\n'
                        }
                    }
                    ar.add(tempString)
                }
            }
        val adapt = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ar)
        loadGame.adapter = adapt

        newGame.setOnClickListener {
            val i = Intent(this, SettingsActivity::class.java)
            i.putExtra("SaveName", fileNum.toString())
            startActivity(i)
        }

        deleteGame.setOnClickListener {
            if(del){
                deleteGame.text = "Delete a game"
                del = false
            }
            else {
                deleteGame.text = "Deleting"
                del = true
            }
        }

        loadGame.setOnItemClickListener { adapterView, view, i, l ->
            if(del) {
                val f = File(filesDir.path + "/" + sn[i] + ".txt")
                f.delete()
                val inte = Intent(this, LoadActivity::class.java)
                startActivity(inte)
            }
            else {
                val savename = sn[i]
                val inte = Intent(this, PlayActivity::class.java)
                inte.putExtra("SaveName", savename)
                startActivity(inte)
            }
        }
    }
}
