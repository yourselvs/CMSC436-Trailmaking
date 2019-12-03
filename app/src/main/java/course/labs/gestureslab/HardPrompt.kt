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
        val intent = Intent(this, TrailMaking2::class.java)
        startActivity(intent)
    }
}