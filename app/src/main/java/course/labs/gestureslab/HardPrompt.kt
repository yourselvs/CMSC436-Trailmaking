package course.labs.gestureslab

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View

class HardPrompt : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hardprompt)
    }

    fun startHardTask(view: View) {
        val oldIntent = getIntent()
        val userID = oldIntent.getStringExtra("ID")
        val userDOB = oldIntent.getStringExtra("DOB")
        val handedness = oldIntent.getStringExtra("HANDEDNESS")
        val difficulty = oldIntent.getStringExtra("DIFFICULTY")

        val intent = Intent(this, TrailMaking2::class.java)
        intent.putExtra("ID", userID)
        intent.putExtra("DOB", userDOB)
        intent.putExtra("HANDEDNESS", handedness)
        intent.putExtra("DIFFICULTY", difficulty)
        startActivity(intent)
    }
}