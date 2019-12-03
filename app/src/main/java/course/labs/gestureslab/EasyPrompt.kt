package course.labs.gestureslab

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.content.Intent

class EasyPrompt : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.easyprompt)
    }

    fun startEasyTask(view: View) {
        val oldIntent = getIntent()
        val userID = oldIntent.getStringExtra(ID)
        val userDOB = oldIntent.getStringExtra(DOB)
        val handedness = oldIntent.getStringExtra(HAND)
        val difficulty = oldIntent.getStringExtra(DIFFICULTY)

        val intent = Intent(this, TrailMaking::class.java)
        intent.putExtra(ID, userID)
        intent.putExtra(DOB, userDOB)
        intent.putExtra(HAND, handedness)
        intent.putExtra(DIFFICULTY, difficulty)
        startActivity(intent)
    }

    companion object {
        private val ID = "ID"
        private val DOB = "DOB"
        private val HAND = "HANDEDNESS"
        private val DIFFICULTY = "DIFFICULTY"
        private val TAG = "TrailMaking"
    }
}