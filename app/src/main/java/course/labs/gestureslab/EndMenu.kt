package course.labs.gestureslab

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.content.Intent

class EndMenu : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.easyprompt)
    }

    fun backToBeginning(view: View) {
        val intent = Intent(this, MainMenu::class.java)
        startActivity(intent)
    }
}