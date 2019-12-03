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
        val intent = Intent(this, TrailMaking::class.java)
        startActivity(intent)
    }
}