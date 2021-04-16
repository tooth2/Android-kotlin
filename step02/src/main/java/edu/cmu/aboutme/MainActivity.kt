package edu.cmu.aboutme

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import edu.cmu.aboutme.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Binding object for MainActivity.
    // Name of the object is derived from the name of the activity or fragment.
    private lateinit var binding: ActivityMainBinding

    // Instance of MyName data class.
    private val myName: MyName = MyName("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //findViewById<Button>(R.id.done_button).setOnClickListener {
        //    addNickname(it)
       // }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Set the value of the myName variable that is declared and used in the layout file.


        // Click listener for the Done button.
        binding.doneButton.setOnClickListener {
            addNickname(it)
        }
        binding.myName = myName

    }

    private fun addNickname(view: View) {
     /**   val editText = findViewById<EditText>(R.id.nickname_edit)
        val nicknameTextView = findViewById<TextView>(R.id.nickname_text)

        nicknameTextView.text = editText.text
        editText.visibility = View.GONE
        view.visibility = View.GONE
        nicknameTextView.visibility = View.VISIBLE

        // Hide the keyboard.
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
     **/
        binding.apply {
            // Set the text for nicknameText to the value in nicknameEdit.
            myName?.nickname = nicknameEdit.text.toString()
            // Invalidate all binding expressions and request a new rebind to refresh UI
            invalidateAll()
            // Change which views are visible.
            // Remove the EditText and the Button.
            // With GONE they are invisible and do not occupy space.
            myName = MyName(nicknameEdit.text.toString())
            nicknameEdit.visibility = View.GONE
            doneButton.visibility = View.GONE


            // Make the TexView with the nickname visible.
            nicknameText.visibility = View.VISIBLE
        }
        // Hide the keyboard.
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}