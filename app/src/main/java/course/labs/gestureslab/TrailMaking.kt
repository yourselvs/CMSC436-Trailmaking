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
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape

class BubbleActivity : Activity() {

    // The Main view
    private var mFrame: FrameLayout? = null

    // Bubble image's bitmap
    private var mBitmap: Bitmap? = null

    // Display dimensions
    private var mDisplayWidth: Int = 0
    private var mDisplayHeight: Int = 0

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
    var numberOn = 1

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

        gestureOverlay.setOnTouchListener { v, event -> mGestureDetector!!.onTouchEvent(event) }

        // Loads the gesture library
        if (!mLibrary!!.load()) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
                setupGestureDetector()
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
                        var i = 0
                        while(i < mFrame!!.childCount){
                            if(mFrame?.getChildAt(i) is BubbleView){
                                var bview = mFrame?.getChildAt(i) as BubbleView

                                //First determine if click happens at a bubble
                                if(bview.intersects(event.x,event.y)){
                                    //next determine if proper button is being clicked
                                    if(bview.getNum() == numberOn){
                                        //TODO - Firebase stuff goes here

                                        bubbleTV.get(numberOn-1).setTextColor(Color.GREEN)
                                        numberOn++
                                        //TODO draw lines
                                        previousx = (bview.getmPosx()+128).toInt()
                                        previousy = (bview.getmPosy()+128).toInt()

                                        //TODO start next intent with 1-a-2-b-3-c
                                        if(numberOn == 16){

                                        }
                                    }
                                    //TODO firebase stuff
                                   //otherwise record it as not being at a bubble
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



    // BubbleView is a View that displays a bubble.
    // This class handles animating, drawing, and popping amongst other
    // actions.
    // A new BubbleView is created for each bubble on the display

    inner class BubbleView internal constructor(context: Context, x: Float, y: Float) : View(context) {
        private val mPainter = Paint()
        private val BITMAP_SIZE = 64

        // location of the bubble
        private var mXPos: Float = 0.toFloat()
        private var mYPos: Float = 0.toFloat()
        private val mRadius: Float
        private val mRadiusSquared: Float

        //what number is the bubbleview
        private var number = 0

        init {
            Log.i(TAG, "Creating Bubble at: x:$x y:$y")


            // Radius
            mRadius = (BITMAP_SIZE * 2).toFloat()
            mRadiusSquared = mRadius * mRadius

            // Adjust position to center the bubble under user's finger
            mXPos = x - mRadius
            mYPos = y - mRadius

            mPainter.isAntiAlias = true
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

        // Returns true if the BubbleView intersects position (x,y)
        @Synchronized
        fun intersects(x: Float, y: Float): Boolean {
            // Return true if the BubbleView intersects position (x,y)
            val xDist = x - (mXPos + mRadius)
            val yDist = y - (mYPos + mRadius)
            return xDist * xDist + yDist * yDist <= mRadiusSquared
        }

        // Draw the Bubble at its current location
        @Synchronized
        override fun onDraw(canvas: Canvas) {
            // save the canvas
            canvas.save()

            // Draw the bitmap at it's new location
            mPainter.color = Color.rgb(0,128,128)
            canvas.drawCircle(mXPos + mRadius,mYPos + mRadius,mRadius,mPainter)

            // Restore the canvas
            canvas.restore()
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
            R.id.quit -> {
                super.onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val TAG = "TrailMaking"
    }
}
