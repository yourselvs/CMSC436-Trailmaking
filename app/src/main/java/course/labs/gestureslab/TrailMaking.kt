package course.labs.gestureslab

import java.util.ArrayList
import java.util.Random
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

import android.app.Activity
import android.content.Context
import android.gesture.Gesture
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.gesture.GestureOverlayView.OnGesturePerformedListener
import android.gesture.Prediction
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.SoundPool.OnLoadCompleteListener
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.FrameLayout
import android.widget.Toast
import android.graphics.Color
import kotlinx.android.synthetic.main.main.*
import android.graphics.drawable.BitmapDrawable
import android.util.DisplayMetrics
import android.widget.TextView

class BubbleActivity : Activity(), OnGesturePerformedListener {

    // The Main view
    private var mFrame: FrameLayout? = null

    // Bubble image's bitmap
    private var mBitmap: Bitmap? = null

    // Display dimensions
    private var mDisplayWidth: Int = 0
    private var mDisplayHeight: Int = 0

    // Sound variables

    // AudioManager
    private var mAudioManager: AudioManager? = null
    // SoundPool
    private var mSoundPool: SoundPool? = null
    // ID for the bubble popping sound
    private var mSoundID: Int = 0
    // Audio volume
    private var mStreamVolume: Float = 0.toFloat()

    // Gesture Detector
    private var mGestureDetector: GestureDetector? = null

    // Gesture Library
    private var mLibrary: GestureLibrary? = null

    val bubblePlacement = arrayListOf(
            Triple(161,2116, 0),Triple(169,719,0),Triple(925,586,0),
            Triple(783,1540,0),Triple(226,1246,0),Triple(1207,2165,0),
            Triple(589,2108,0),Triple(1058,1192,0),Triple(593,734,0),
            Triple(440,1662,0),Triple(1188,872,0),Triple(1051,1749,0),
            Triple(604,1143,0),Triple(1005,273,0),Triple(558,288,0),
            Triple(283,254,0)
    )


    val bubbleTV = ArrayList<TextView>()

    var previousx: Int = 0
    var previousy: Int = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        // Set up user interface
        mFrame = findViewById<View>(R.id.frame) as FrameLayout
        // Load basic bubble Bitmap
        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.b64)


        bubblePlacement.shuffle()

        //bubbleplacement
        for(i in 0 .. bubblePlacement.size - 1){
            var bView = BubbleView(mFrame!!.context, bubblePlacement.get(i).first.toFloat(),
                    bubblePlacement.get(i).second.toFloat())
            bView.setNum(i+1)
            mFrame!!.addView(bView)

            val tv_dynamic = TextView(this)
            tv_dynamic.x = bubblePlacement.get(i).first.toFloat() - 50
            tv_dynamic.y = bubblePlacement.get(i).second.toFloat() - 50
            tv_dynamic.textSize = 30f
            tv_dynamic.setTextColor(Color.BLACK)
            tv_dynamic.text = (i + 1).toString()
            bubbleTV.add(tv_dynamic)
            mFrame!!.addView(tv_dynamic)
        }


    // TODO - Fetch GestureLibrary from raw
        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures)

        val gestureOverlay = findViewById<View>(R.id.gestures_overlay) as GestureOverlayView

        gestureOverlay.addOnGesturePerformedListener(this)

        gestureOverlay.setOnTouchListener { v, event -> mGestureDetector!!.onTouchEvent(event) }

        // Loads the gesture library
        if (!mLibrary!!.load()) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        // Manage bubble popping sound
        // Use AudioManager.STREAM_MUSIC as stream type

        val musicAttribute = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        mSoundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(musicAttribute)
                .build()

        mSoundID = mSoundPool!!.load(this, R.raw.bubble_pop, 1)

        mSoundPool?.apply{
            setOnLoadCompleteListener { _, _, status ->
                setupGestureDetector()
            }
        }


    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {

            // Get the size of the display so this View knows where borders are
            mDisplayWidth = mFrame!!.width
            mDisplayHeight = mFrame!!.height

        }
    }

    // Set up GestureDetector
    private fun setupGestureDetector() {

        mGestureDetector = GestureDetector(this,
                object : GestureDetector.SimpleOnGestureListener() {

                    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                        var bool = false
                        var i = 0

                        while(i < mFrame!!.childCount){
                            if(mFrame?.getChildAt(i) is BubbleView){
                                var bview = mFrame?.getChildAt(i) as BubbleView
                                //TODO - Firebase stuff goes here
                                //TODO just need to finish with drawing lines and changing the color
                                if(bview.intersects(event.x,event.y)){
                                    var j:Int=0
                                    for(k in 0 .. bubblePlacement.size - 1){
                                        if(bview.getmPosx()+128 == bubblePlacement.get(k).first.toFloat()){
                                            var b = 0
                                            while(b < mFrame!!.childCount){
                                                var prevbview = mFrame?.getChildAt(i) as BubbleView
                                                if(prevbview.getNum() + 1 == bview.getNum()){
                                                    bubbleTV.get(k).setTextColor(Color.GREEN)
                                                }
                                            }

                                            previousx = (bview.getmPosx()+128).toInt()
                                            previousy = (bview.getmPosy()+128).toInt()






                                        }
                                    }



                                }else{

                                }
                            }
                            i++
                        }
                        return true
                    }

                    // Good practice to override this method because all
                    // gestures start with a ACTION_DOWN event
                    override fun onDown(event: MotionEvent): Boolean {
                        return true
                    }
                })
    }

    override fun onPause() {

        // Release all SoundPool resources

        mSoundPool!!.unload(mSoundID)
        mSoundPool!!.release()
        mSoundPool = null

        super.onPause()
    }

    override fun onGesturePerformed(overlay: GestureOverlayView, gesture: Gesture) {

        val predictions: ArrayList<Prediction>? = mLibrary?.recognize(gesture)

        if (predictions!!.size > 0) {

            // Get highest-ranked prediction
            val prediction = predictions[0]


            if (prediction.score > MIN_PRED_SCORE) {
                when (prediction.name) {
                    "openMenu"-> {
                        openOptionsMenu()
                    }
                    "addTen" -> {
                        for(i in 0 .. 9){
                            val r = Random()
                            val x = r.nextInt(mDisplayWidth - mBitmap!!.width).toFloat()
                            val y = r.nextInt(mDisplayHeight - mBitmap!!.height).toFloat()
                            val bubbleView = BubbleView(applicationContext, x, y)
                            mFrame!!.addView(bubbleView)
                            bubbleView.start()
                        }
                    }

                }
                Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
                        .show()
            } else {
                Toast.makeText(this, "No prediction", Toast.LENGTH_SHORT)
                        .show()
            }
        } else {
            Toast.makeText(this, "No prediction", Toast.LENGTH_SHORT)
                    .show()
        }

    }
    // BubbleView is a View that displays a bubble.
    // This class handles animating, drawing, and popping amongst other
    // actions.
    // A new BubbleView is created for each bubble on the display

    inner class BubbleView internal constructor(context: Context, x: Float, y: Float) : View(context) {
        private val mPainter = Paint()
        private var mMoverFuture: ScheduledFuture<*>? = null
        private var mScaledBitmapWidth: Int = 0
        private var mScaledBitmap: Bitmap? = null
        private val BITMAP_SIZE = 64
        private val REFRESH_RATE = 40
        // location, speed and direction of the bubble
        private var mXPos: Float = 0.toFloat()
        private var mYPos: Float = 0.toFloat()
        private var mDx: Float = 0.toFloat()
        private var mDy: Float = 0.toFloat()
        private val mRadius: Float
        private val mRadiusSquared: Float
        private var mRotate: Long = 0
        private var mDRotate: Long = 0

        //what number is theis bubbleview, and has it been visited
        private var number = 0
        private var bool = false

        // Return true if the BubbleView is not on the screen after the move
        // operation
        private val isOutOfView: Boolean
            get() = (mXPos < 0 - mScaledBitmapWidth || mXPos > mDisplayWidth
                    || mYPos < 0 - mScaledBitmapWidth || mYPos > mDisplayHeight)

        init {
            Log.i(TAG, "Creating Bubble at: x:$x y:$y")

            // Create a new random number generator to
            // randomize size, rotation, speed and direction
            val r = Random()

            // Creates the bubble bitmap for this BubbleView
            createScaledBitmap(r)

            // Radius of the Bitmap
            mRadius = (mScaledBitmapWidth / 2).toFloat()
            mRadiusSquared = mRadius * mRadius


            // Adjust position to center the bubble under user's finger
            mXPos = x - mRadius
            mYPos = y - mRadius

            // Set the BubbleView's speed and direction
            setSpeedAndDirection(r)



            mPainter.isAntiAlias = true

        }

        private fun setSpeedAndDirection(r: Random) {

                    // No speed
                    mDx = 0f
                    mDy = 0f
        }

        private fun createScaledBitmap(r: Random) {


                mScaledBitmapWidth = BITMAP_SIZE * 4


            // Create the scaled bitmap using size set above
            mScaledBitmap = Bitmap.createScaledBitmap(mBitmap!!,
                    mScaledBitmapWidth, mScaledBitmapWidth, false)
        }

        fun getmPosx():Float{
            return mXPos
        }
        fun getmPosy():Float{
            return mYPos
        }

        fun setNum(i:Int){
            number = i
        }
        fun getNum():Int{
           return number
        }

        fun visit(){
            bool = true
        }
        fun beenvisited():Boolean{
            return bool
        }

        // Start moving the BubbleView & updating the display
        fun start() {

            // Creates a WorkerThread
            val executor = Executors
                    .newScheduledThreadPool(1)

            // Execute the run() in Worker Thread every REFRESH_RATE
            // milliseconds
            // Save reference to this job in mMoverFuture
            mMoverFuture = executor.scheduleWithFixedDelay({
                // Implement movement logic.
                // Each time this method is run the BubbleView should
                // move one step. If the BubbleView exits the display,
                // stop the BubbleView's Worker Thread.
                // Otherwise, request that the BubbleView be redrawn.

                if (moveWhileOnScreen()) {
                    postInvalidate()
                } else
                    stop(false)
            }, 0, REFRESH_RATE.toLong(), TimeUnit.MILLISECONDS)
        }

        // Returns true if the BubbleView intersects position (x,y)
        @Synchronized
        fun intersects(x: Float, y: Float): Boolean {

            // Return true if the BubbleView intersects position (x,y)

            val xDist = x - (mXPos + mRadius)
            val yDist = y - (mYPos + mRadius)

            return xDist * xDist + yDist * yDist <= mRadiusSquared

        }

        // Cancel the Bubble's movement
        // Remove Bubble from mFrame
        // Play pop sound if the BubbleView was popped

        fun stop(wasPopped: Boolean) {

            if (null != mMoverFuture) {

                if (!mMoverFuture!!.isDone) {
                    mMoverFuture!!.cancel(true)
                }

                // This work will be performed on the UI Thread
                mFrame!!.post {
                    // Remove the BubbleView from mFrame
                    mFrame!!.removeView(this@BubbleView)

                    // If the bubble was popped by user,
                    // play the popping sound
                    if (wasPopped) {
                        mSoundPool!!.play(mSoundID, mStreamVolume,
                                mStreamVolume, 1, 0, 1.0f)
                    }
                }
            }
        }

        // Change the Bubble's speed and direction
        @Synchronized
        fun deflect(velocityX: Float, velocityY: Float) {
            mDx = velocityX / REFRESH_RATE
            mDy = velocityY / REFRESH_RATE
        }

        // Draw the Bubble at its current location
        @Synchronized
        override fun onDraw(canvas: Canvas) {

            // save the canvas
            canvas.save()

            // Increase the rotation of the original image by mDRotate
            mRotate += mDRotate

            // Rotate the canvas by current rotation
            // Hint - Rotate around the bubble's center, not its position

            canvas.rotate(mRotate.toFloat(), mXPos + mScaledBitmapWidth / 2, mYPos + mScaledBitmapWidth / 2)

            // Draw the bitmap at it's new location
            canvas.drawBitmap(mScaledBitmap!!, mXPos, mYPos, mPainter)

            // Restore the canvas
            canvas.restore()

        }

        // Returns true if the BubbleView is still on the screen after the move
        // operation
        @Synchronized
        private fun moveWhileOnScreen(): Boolean {

            // Move the BubbleView

            mXPos += mDx
            mXPos = mXPos
            mYPos += mDy
            mYPos = mYPos

            return !isOutOfView

        }
    }

    override fun onBackPressed() {
        openOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_still_mode -> {
                speedMode = STILL
                return true
            }
            R.id.menu_single_speed -> {
                speedMode = SINGLE
                return true
            }
            R.id.menu_random_mode -> {
                speedMode = RANDOM
                return true
            }
            R.id.quit -> {
                exitRequested()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun exitRequested() {
        super.onBackPressed()
    }


    companion object {

        private val MIN_PRED_SCORE = 3.0
        // These variables are for testing purposes, do not modify
        private val RANDOM = 0
        private val SINGLE = 1
        private val STILL = 2
        var speedMode = RANDOM

        private val TAG = "Lab-Gestures"
    }
}
