package course.labs.gestureslab

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.gesture.GestureOverlayView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.graphics.Color
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.sql.Time


class TrailMaking : Activity() {

    // The Main view
    private var mFrame: FrameLayout? = null

    // circle image's bitmap
    private var mBitmap: Bitmap? = null

    // Display dimensions
    private var mDisplayWidth: Int = 0
    private var mDisplayHeight: Int = 0

    // Gesture Detector
    private var mGestureDetector: GestureDetector? = null

    private val circlePlacement = arrayListOf(
            Pair(161,2116),Pair(169,719),Pair(925,586),
            Pair(783,1540),Pair(226,1246),Pair(1207,2165),
            Pair(589,2108),Pair(1058,1192),Pair(593,734),
            Pair(440,1662),Pair(1188,872),Pair(1051,1749),
            Pair(604,1143),Pair(1005,273),Pair(558,288),
            Pair(283,254)
    )

    private val numbers = arrayListOf("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16")

    private val circleTV = ArrayList<TextView>()

    private var previousx: Int = 0
    private var previousy: Int = 0
    private var numberOn = 1

    //Colors
    private var initTextColor = Color.BLACK
    private var changedTextColor = Color.GREEN
    private var circleColor = Color.rgb(0,128,128)
    private var lineColor = Color.GREEN

    //if you want the text color to change on tap as well as draw line
    private var textColorChange = true

    // Test initialized when the activity starts
    private lateinit var test: PathfinderTest

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        // Set up user interface
        mFrame = findViewById<View>(R.id.frame) as FrameLayout
        // Load basic circle Bitmap
        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.b64)

        circlePlacement.shuffle()

        //circleplacement
        for(i in 0 .. circlePlacement.size - 1){
            //makes the circleview
            var bView = CircleView(mFrame!!.context, circlePlacement.get(i).first.toFloat(),
                    circlePlacement.get(i).second.toFloat())
            bView.setNum(i+1)
            mFrame!!.addView(bView)

            //makes the text views
            val tv_dynamic = TextView(this)
            tv_dynamic.x = circlePlacement.get(i).first.toFloat() - 64
            tv_dynamic.y = circlePlacement.get(i).second.toFloat() - 64
            tv_dynamic.textSize = 30f
            tv_dynamic.setTextColor(initTextColor)
            tv_dynamic.text = (i + 1).toString()
            circleTV.add(tv_dynamic)
            mFrame!!.addView(tv_dynamic)
        }

        val gestureOverlay = findViewById<View>(R.id.gestures_overlay) as GestureOverlayView
        gestureOverlay.setOnTouchListener { v, event -> mGestureDetector!!.onTouchEvent(event) }

        test = PathfinderTest(numbers)
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
                        var hit = false
                        var i = 0
                        //iterate through all views in frame
                        while(i < mFrame!!.childCount){
                            //make sure the view is a circleview
                            if(mFrame?.getChildAt(i) is CircleView){
                                var bview = mFrame?.getChildAt(i) as CircleView

                                //First determine if click happens at a circle
                                if(bview.intersects(event.x,event.y)){
                                    test.pressButton(bview.getNum().toString())
                                    hit = true
                                    //next determine if proper button is being clicked
                                    if(bview.getNum() == numberOn){

                                        //makes sure that lines start being drawn after first tap
                                        if(numberOn ==1){
                                            previousx = (bview.getmPosx()+128).toInt()
                                            previousy = (bview.getmPosy()+128).toInt()
                                        }
                                        //changes text color
                                        if(textColorChange){
                                            circleTV.get(numberOn-1).setTextColor(changedTextColor)
                                        }
                                        //iterates the number in the circle
                                        numberOn++

                                        //draws lines connecting circles
                                        var lView = LineView(mFrame!!.context, previousx.toFloat(), previousy.toFloat(),
                                                bview.getmPosx()+128,bview.getmPosy()+128)
                                        mFrame!!.addView(lView)

                                        //updates previous circle position
                                        previousx = (bview.getmPosx()+128).toInt()
                                        previousy = (bview.getmPosy()+128).toInt()


                                        if(numberOn == circlePlacement.size + 1){
                                            val intent = Intent(applicationContext, EndMenu::class.java)
                                            startActivity(intent)
                                        }
                                    }


                                }
                            }
                            i++
                        }

                        if (!hit) {
                            test.pressButton("MISS")
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



    // circleView is a View that displays a circle.
    // This class handles animating, drawing, and popping amongst other
    // actions.
    // A new circleView is created for each circle on the display

    inner class CircleView internal constructor(context: Context, x: Float, y: Float) : View(context) {
        private val mPainter = Paint()
        private val BITMAP_SIZE = 64

        // location of the circle
        private var mXPos: Float = 0.toFloat()
        private var mYPos: Float = 0.toFloat()
        private val mRadius: Float
        private val mRadiusSquared: Float

        //what number is the circleview
        private var number = 0

        init {
            Log.i(TAG, "Creating circle at: x:$x y:$y")
            // Radius
            mRadius = (BITMAP_SIZE * 2).toFloat()
            mRadiusSquared = mRadius * mRadius

            // Adjust position to center the circle under user's finger
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

        // Returns true if the circleView intersects position (x,y)
        @Synchronized
        fun intersects(x: Float, y: Float): Boolean {
            // Return true if the circleView intersects position (x,y)
            val xDist = x - (mXPos + mRadius)
            val yDist = y - (mYPos + mRadius)
            return xDist * xDist + yDist * yDist <= mRadiusSquared
        }

        // Draw the circle at its current location
        @Synchronized
        override fun onDraw(canvas: Canvas) {
            // save the canvas
            canvas.save()

            // Draw the bitmap at it's new location
            mPainter.color = circleColor
            canvas.drawCircle(mXPos + mRadius,mYPos + mRadius,mRadius,mPainter)

            // Restore the canvas
            canvas.restore()
        }
    }

    inner class LineView internal constructor(context: Context, xstart: Float, ystart: Float, xend:Float, yend: Float) : View(context) {
        private val mPainter = Paint()

        // location
        var x1:Float = 0f
        var x2:Float = 0f
        var y1:Float = 0f
        var y2:Float = 0f

        init {
            x1 = xstart
            x2 = xend
            y1 = ystart
            y2 = yend

            mPainter.isAntiAlias = true
        }

        // Draw the circle at its current location
        @Synchronized
        override fun onDraw(canvas: Canvas) {
            // save the canvas
            canvas.save()
            mPainter.strokeWidth = 10f
            // Draw the bitmap at it's new location
            mPainter.color = lineColor
            canvas.drawLine(x1,y1,x2,y2,mPainter)

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
                val intent = Intent(this, MainMenu::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val ID = "ID"
        private val DOB = "DOB"
        private val HAND = "HANDEDNESS"
        private val DIFFICULTY = "DIFFICULTY"
        private val TAG = "TrailMaking"
    }
}