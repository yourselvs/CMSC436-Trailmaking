package course.labs.gestureslab

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener


class MainMenu : Activity() {

    lateinit var userID: String
    lateinit var userDOB: String
    lateinit var handedness: String
    lateinit var difficulty: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner)

        val handSpinner = findViewById<Spinner>(R.id.handSpinner)
        val difficultySpinner = findViewById<Spinner>(R.id.difficultySpinner)

        val handAdapter = ArrayAdapter.createFromResource(
                this, R.array.handedness, R.layout.dropdown_item
        )

        val difficultyAdapter = ArrayAdapter.createFromResource(
                this, R.array.difficulties, R.layout.dropdown_item
        )

        handSpinner.adapter = handAdapter
        difficultySpinner.adapter = difficultyAdapter

        handSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>, view: View,
                    pos: Int, id: Long
            ) {
                (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                handedness = handSpinner.getItemAtPosition(pos).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        difficultySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>, view: View,
                    pos: Int, id: Long
            ) {
                (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                difficulty = difficultySpinner.getItemAtPosition(pos).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun collectUserInfo(view: View) {
        var mETid = findViewById<EditText>(R.id.idEdit)
        var mETdob = findViewById<EditText>(R.id.dobEdit)

        userID = mETid.getText().toString()
        userDOB = mETdob.getText().toString()

        if (userID.isEmpty() || userDOB.isEmpty()) {
            Toast.makeText(applicationContext, "Please enter ID and Date of Birth", Toast.LENGTH_LONG).show()
            return
        } else {
            if (difficulty == "Easy") {
                val intent = Intent(this, EasyPrompt::class.java)
                intent.putExtra(ID, userID)
                intent.putExtra(DOB, userDOB)
                intent.putExtra(HAND, handedness)
                intent.putExtra(DIFFICULTY, difficulty)
                startActivity(intent)
                finish()
            } else if (difficulty == "Hard") {
                val intent = Intent(this, HardPrompt::class.java)
                intent.putExtra(ID, userID)
                intent.putExtra(DOB, userDOB)
                intent.putExtra(HAND, handedness)
                intent.putExtra(DIFFICULTY, difficulty)
                startActivity(intent)
                finish()
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
