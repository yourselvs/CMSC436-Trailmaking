package course.labs.gestureslab

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.content.Intent

class EndMenu : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.endprompt)
    }

    fun backToBeginning(view: View) {
        val intent = Intent(this, MainMenu::class.java)
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