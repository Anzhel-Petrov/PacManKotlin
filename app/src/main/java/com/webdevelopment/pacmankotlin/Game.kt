package com.webdevelopment.pacmankotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


/**
 *
 * This class should contain all your game logic
 */

class Game(private var context: Context, view: TextView, view2: TextView) {

    private var pointsView: TextView = view
    private var timeLeftView: TextView = view2
    private var points: Int = 0

    //bitmap of the pacman
    var pacBitmap: Bitmap
    var coinBitmap: Bitmap
    var inkyBitmap: Bitmap
    var pacx: Int = 0
    var pacy: Int = 0
    var running = false
    var levelTime = 60
//    var level = -1


    //did we initialize the coins?
    var coinsInitialized = false

    //the list of goldcoins - initially empty
    var coins = ArrayList<GoldCoin>()
    var enemies = ArrayList<Enemy>()

    //a reference to the gameview
    private var gameView: GameView? = null
    private var h: Int = 0
    private var w: Int = 0 //height and width of screen


    //The init code is called when we create a new Game class.
    //it's a good place to initialize our images.
    init {
        pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman)
        coinBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coin)
        inkyBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.inky)
    }

    fun setGameView(view: GameView) {
        this.gameView = view
    }

    //TODO initialize goldcoins also here
    fun initializeGoldcoins() {
        //DO Stuff to initialize the array list with some coins.

        var minX: Int = 0
        var cmaxX: Int = w - coinBitmap.width
        var emaxX: Int = w - inkyBitmap.width
        var minY: Int = 0
        var cmaxY: Int = h - coinBitmap.width
        var emaxY: Int = h - inkyBitmap.width
        val random = Random()

        for (i in 0..5) {
            var randomX: Int = random.nextInt(cmaxX - minX + 1) + minX
            var randomY: Int = random.nextInt(cmaxY - minY + 1) + minY
            coins.add(GoldCoin(randomX, randomY, false))
        }

        for (i in 0..2) {
            var randomX: Int = random.nextInt(emaxX - minX + 1) + minX
            var randomY: Int = random.nextInt(emaxY - minY + 1) + minY
            enemies.add(Enemy(randomX, randomY, true, (1..4).random(), false))
        }
        coinsInitialized = true
    }


    fun newGame() {
        points = 0
        pointsView.text = "${context.resources.getString(R.string.points)} $points"
        pacx = 50
        pacy = 400 //just some starting coordinates - you can change this.
        //reset the points
        coins.clear()
        Log.d("Coins", coins.size.toString())
        enemies.clear()
        levelTime = 60
        timeLeftView.text = "${context.getString(R.string.time_left)} ${levelTime}"
        coinsInitialized = false
        gameView?.invalidate() //redraw screen
    }

    fun setSize(h: Int, w: Int) {
        this.h = h
        this.w = w
    }

    fun moveEnemy(pixels: Int) {
        for (enemy in enemies) {
            when (enemy.enemyDirection) {
                1 -> if (enemy.enemyx + pixels + inkyBitmap.width < w) {
                    enemy.enemyx = enemy.enemyx + pixels
                    gameView!!.invalidate()
                }
                2 -> if (enemy.enemyx > 0) {
                    enemy.enemyx -= pixels
                    gameView!!.invalidate()
                }
                3 -> if (enemy.enemyy > 0) {
                    enemy.enemyy -= pixels
                    gameView!!.invalidate()
                }
                4 -> if (enemy.enemyy + pixels + inkyBitmap.height < h) {
                    enemy.enemyy += pixels
                    gameView!!.invalidate()
                }

            }
        }

    }

    fun movePacmanRight(pixels: Int) {
        //still within our boundaries?
        if (pacx + pixels + pacBitmap.width < w) {
            pacx = pacx + pixels
            doCollisionCheck()
            gameView!!.invalidate()
        }
    }

    fun movePacmanLeft(pixels: Int) {
        //still within our boundaries?
        if (pacx > 0) {
            pacx = pacx - pixels
            doCollisionCheck()
            gameView!!.invalidate()
        }
    }

    fun movePacmanUp(pixels: Int) {
        //still within our boundaries?
        if (pacy > 0) {
            pacy = pacy - pixels
            doCollisionCheck()
            gameView!!.invalidate()
        }
    }

    fun movePacmanDown(pixels: Int) {
        //still within our boundaries?
        if (pacy + pixels + pacBitmap.height < h) {
            pacy = pacy + pixels
            doCollisionCheck()
            gameView!!.invalidate()
        }
    }

    //TODO check if the pacman touches a gold coin
    //and if yes, then update the neccesseary data
    //for the gold coins and the points
    //so you need to go through the arraylist of goldcoins and
    //check each of them for a collision with the pacman

    fun doCollisionCheck() {

        for (coin in coins) {
            if (pacx + pacBitmap.width >= coin.coinx && pacx <= coin.coinx + coinBitmap.width &&
                pacy + pacBitmap.height >= coin.coiny && pacy <= coin.coiny + coinBitmap.height && !coin.taken) {
//                Toast.makeText(this.context,"Got you fucker!",Toast.LENGTH_SHORT).show()
                coin.taken = true;
                if(running){
                    points++
                    pointsView.text = "${context.resources.getString(R.string.points)} $points"
                }

            }
        }

        for (enemy in enemies) {
            if (pacx + pacBitmap.width >= enemy.enemyx && pacx <= enemy.enemyx + inkyBitmap.width &&
                pacy + pacBitmap.height >= enemy.enemyy && pacy <= enemy.enemyy + inkyBitmap.height) {
                if(!running)
                {
                    enemy.isStacked = true
                }
                if(running)
                {
                    running = false
//                    val addDialog = AlertDialog.Builder(context)
//                            .setTitle("You Beat this level")
//                            .setMessage("Do you want to move on to the next level?")
//                            .setPositiveButton("Next Level") { _, _ -> newGame()}
//                    addDialog.show()
                }

//                newGame()
            }
        }

        if (points == coins.size && running) {
//            Toast.makeText(this.context,"This was the last fucking coin bro!",Toast.LENGTH_SHORT).show()
            running = false
//            level++
            val addDialog = AlertDialog.Builder(context)
                .setTitle("You Beat this level")
                .setMessage("Do you want to move on to the next level?")
                .setPositiveButton("Next Level") { _, _ -> newGame()}
            addDialog.show()
        }
    }
}