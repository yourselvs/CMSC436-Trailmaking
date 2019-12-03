package course.labs.gestureslab

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View

class HardPrompt : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hardprompt)
    }

    fun startHardTask(view: View) {
        val oldIntent = getIntent()
        val userID = oldIntent.getStringExtra(ID)
        val userDOB = oldIntent.getStringExtra(DOB)
        val handedness = oldIntent.getStringExtra(HAND)
        val difficulty = oldIntent.getStringExtra(DIFFICULTY)

        val intent = Intent(this, TrailMaking2::class.java)
        intent.putExtra(ID, userID)
        intent.putExtra(DOB, userDOB)
        intent.putExtra(HAND, handedness)
        intent.putExtra(DIFFICULTY, difficulty)
        startActivity(intent)
        finish()
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
                finish()
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