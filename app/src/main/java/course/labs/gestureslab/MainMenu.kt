package course.labs.gestureslab

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import android.content.Intent

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
                handedness = handSpinner.getItemAtPosition(pos).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        difficultySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>, view: View,
                    pos: Int, id: Long
            ) {
                difficulty = difficultySpinner.getItemAtPosition(pos).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun collectUserInfo(view: View) {
        userID = R.id.idEdit.toString()
        userDOB = R.id.dobEdit.toString()

        // TODO idk why but it's not checking to see if the data entry is empty or not
        if (userID == " " || userDOB == " ") {
            Toast.makeText(applicationContext, "Missing information!", Toast.LENGTH_LONG).show()
            return
        } else {
            if (difficulty == "Easy") {
                val intent = Intent(this, EasyPrompt::class.java)
                startActivity(intent)
            } else if (difficulty == "Hard") {
                val intent = Intent(this, HardPrompt::class.java)
                startActivity(intent)
            }
        }
    }
}