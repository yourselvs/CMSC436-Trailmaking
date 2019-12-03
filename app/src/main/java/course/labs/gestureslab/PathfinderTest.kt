package course.labs.gestureslab

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime


class PathfinderTest internal constructor(
        private val targets: List<String>,
        private val userID: String,
        private val userDOB: String,
        private val userHand: String,
        private val difficulty: String
) {
    inner class TestEvent internal constructor(val timestamp: Long, val timeSinceStart: Long, val buttonTarget: String, val buttonPressed: String, val status: String)

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    private val history: MutableList<TestEvent> = ArrayList()
    private val startMillis: Long = System.currentTimeMillis()
    private val startTime = LocalDateTime.now()
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

        val endTime = LocalDateTime.now()
        val testDuration = endMillis - startMillis
        val numComplete = currentTargetNum
        val isComplete = numComplete == targets.size
        val numAttempts = history.size

        Log.i(TAG, "Test finished at timestamp ${endTime}")

        val data = HashMap<String, Any>()

        data["userID"] = userID
        data["userDOB"] = userDOB
        data["userHand"] = userHand
        data["testDifficulty"] = difficulty
        data["testStartTimestamp"] = startMillis
        data["testEndTimestamp"] = endMillis
        data["testDuration"] = testDuration
        data["testIsComplete"] = isComplete
        data["numComplete"] = numComplete
        data["numAttempts"] = numAttempts
        data["numErrors"] = numErrors
        data["numMisses"] = numMisses
        data["numIncorrect"] = numIncorrect


        (db.collection("tests")
                .add(data)
                .addOnSuccessListener { result ->
                    Log.d(TAG, "DocumentSnapshot added with ID: " + result.id)

                    writeHistory(result.id)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                })
    }

    private fun writeHistory(id: String) {
        history.forEach {
            val event = HashMap<String, Any>()

            event["status"] = it.status
            event["buttonPressed"] = it.buttonPressed
            event["buttonTarget"] = it.buttonTarget
            event["timeSinceStart"] = it.timeSinceStart
            event["timestamp"] = it.timestamp

            db.collection("tests")
                    .document(id)
                    .collection("events")
                    .add(event)
                    .addOnSuccessListener { result ->
                        Log.d(TAG, "DocumentSnapshot event added with ID: " + result.id)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding event document", e)
                    }
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