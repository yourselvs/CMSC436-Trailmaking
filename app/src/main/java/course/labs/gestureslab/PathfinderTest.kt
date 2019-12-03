package course.labs.gestureslab

import android.util.Log
import java.sql.Time



class PathfinderTest internal constructor(private val targets: List<String>) {
    inner class TestEvent internal constructor(val timestamp: Long, val timeSinceStart: Long, val buttonTarget: String, val buttonPressed: String, val correct: String)
    
    private val history: MutableList<TestEvent> = ArrayList()
    private val startMillis: Long = System.currentTimeMillis()
    private val startTime: Time = Time(startMillis)
    private var currentTarget: String = targets[0]
    private var currentTargetNum: Int = 0

    private var numErrors = 0
    private var numMisses = 0
    private var numIncorrect = 0

    var finished = false
        private set

    init {
        Log.i(TAG, "Test initialized at time ${startTime}")
    }

    fun pressButton(buttonPressed: String) {
        if(!finished) {
            val pressTimestamp = System.currentTimeMillis()
            val status = if (buttonPressed == currentTarget) "CORRECT" else if (buttonPressed == "MISS") "MISS" else "INCORRECT"
            val pressEvent = TestEvent(pressTimestamp, pressTimestamp - startMillis, currentTarget, buttonPressed, status)
            history.add(pressEvent)

            Log.i(TAG, "Button pressed with value ${buttonPressed}, target ${currentTarget},  status ${status}, and timestamp ${pressTimestamp - startMillis}")

            if (status == "MISS") {
                numMisses++
                numErrors++
            }
            else if (status == "INCORRECT") {
                numIncorrect++
                numErrors++
            }
            else { // status is correct
                // Increment the current target number
                // If it is the last one, finish the test
                if (++currentTargetNum == targets.size) {
                    finishTest()
                }
                else {
                    currentTarget = targets[currentTargetNum]
                }
            }
        }
        else {
            Log.i(TAG, "Button pressed but test was already finished")
        }
    }

    fun finishTest() {
        finished = true

        val endMillis = System.currentTimeMillis()

        val endTime = Time(endMillis)
        val testDuration = endMillis - startMillis
        val numComplete = currentTargetNum
        val isComplete = numComplete == targets.size
        val numAttempts = history.size

        Log.i(TAG, "Test finished at timestamp ${endTime}")

        // TODO: Record data into firebase
    }

    companion object {
        private val ID = "ID"
        private val DOB = "DOB"
        private val HAND = "HANDEDNESS"
        private val DIFFICULTY = "DIFFICULTY"
        private val TAG = "TrailMaking"
    }
}