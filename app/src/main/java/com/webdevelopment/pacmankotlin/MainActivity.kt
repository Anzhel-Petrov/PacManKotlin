package com.webdevelopment.pacmankotlin

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    //reference to the game class.
    private var game: Game? = null

    private var myTimer: Timer = Timer()
    private var levelTimer: Timer = Timer()
    var counter: Int = 0
    var UP = 1
    var RIGHT = 2
    var DOWN = 3
    var LEFT = 4
    var direction = RIGHT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //makes sure it always runs in portrait mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        continueButton.setOnClickListener(this)
        pauseButton.setOnClickListener(this)
        resetButton.setOnClickListener(this)

        //make a new timer
        game?.running = true //should the game be running?
        //We will call the timer 5 times each second
        myTimer.schedule(object : TimerTask() {
            override fun run() {
                timerMethod()
            }

        }, 0, 50) //0 indicates we start now, 200
        //is the number of miliseconds between each call

        levelTimer.schedule(object : TimerTask() {
            override fun run() {
//              levelTime --
                levelMethod()
            }

        }, 0, 1000) //0 indicates we start now, 200
        //is the number of miliseconds between each call

        game = Game(this, pointsView, timeLeftView)

        //intialize the game view clas and game class
        game?.setGameView(gameView)
        gameView.setGame(game)
        game?.newGame()

        moveRight.setOnClickListener {
            direction = RIGHT
        }

        moveLeft.setOnClickListener {
            direction = LEFT
        }

        moveUp.setOnClickListener {
            direction = UP
        }

        moveDown.setOnClickListener {
            direction = DOWN
        }
    }

    override fun onStop() {
        super.onStop()
        //just to make sure if the app is killed, that we stop the timer.
        myTimer.cancel()
        levelTimer.cancel()
    }

    private fun timerMethod() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //we could do updates here TO GAME LOGIC,
        // but not updates TO ACTUAL UI

        // gameView.move(20)  // BIG NO NO TO DO THIS - WILL CRASH ON OLDER DEVICES!!!!


        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(timerTick)
    }

    private fun levelMethod() {
        this.runOnUiThread(levelTick)
    }

    private val levelTick = Runnable {
        if (game!!.running) {
            game!!.levelTime--
            timeLeftView.text = "${resources.getString(R.string.time_left)} ${game!!.levelTime}"
            for(enemy in game!!.enemies){
                enemy.enemyDirection = (1..4).random()
            }

//            if(game!!.levelTime % 2 == 0)
//            {
//                game!!.enemyDirection = (1..4).random()
//            }
            if (game!!.levelTime == 0) {
                game!!.running = false
                val addDialog = AlertDialog.Builder(this)
                    .setTitle("Time is up - too bad!")
                    .setMessage("Do you want to try again?")
                    .setPositiveButton("Try Again") { _, _ -> game!!.newGame()}
                    .setCancelable(false)
                addDialog.show()
            }
        }
    }

    private val timerTick = Runnable {
        //This method runs in the same thread as the UI.
        // so we can draw
        if (game!!.running) {
            counter++
            //update the counter - notice this is NOT seconds in this example
            //you need TWO counters - one for the timer count down that will
            // run every second and one for the pacman which need to run
            //faster than every second
            timeView.text = "${resources.getString(R.string.time)} $counter"
            game!!.moveEnemy(9)

            if (direction == UP) { // move up
                game!!.movePacmanUp(10)
                //move the pacman - you
                //should call a method on your game class to move
                //the pacman instead of this - you have already made that
            } else if (direction == RIGHT) {
                game!!.movePacmanRight(10)
            } else if (direction == DOWN) {
                game!!.movePacmanDown(10)
            } else if (direction == LEFT) {
                game!!.movePacmanLeft(10)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            Toast.makeText(this, "settings clicked", Toast.LENGTH_LONG).show()
            return true
        } else if (id == R.id.action_newGame) {
            Toast.makeText(this, "New Game clicked", Toast.LENGTH_LONG).show()
            game!!.running = false
            game!!.level = -1
            game?.newGame()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //if anything is pressed - we do the checks here
    override fun onClick(v: View) {
        if (v.id == R.id.continueButton) {
            game!!.running = true
        } else if (v.id == R.id.pauseButton) {
            game!!.running = false
        } else if (v.id == R.id.resetButton) {
            counter = 0
            game!!.newGame() //you should call the newGame method instead of this
            game!!.running = false
            timeView.text = "${resources.getString(R.string.time)} $counter"
            timeLeftView.text = "${resources.getString(R.string.time_left)} ${game!!.levelTime}"

        }
    }

}
