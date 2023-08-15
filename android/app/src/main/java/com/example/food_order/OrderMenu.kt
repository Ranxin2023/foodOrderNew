package com.example.food_order

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class OrderMenu:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_menu)
        val menuInput=findViewById<EditText>(R.id.enterMenuEditText)
        menuInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENTER) {
                // Perform the action you want when the user presses the "Enter" key
                // For example, you can call a function to process the input.
                if(menuInput.text.toString().lowercase()=="recipe"||menuInput.text.toString().lowercase()=="view recipe"){
                    val intent= Intent(this, ViewRecipe::class.java)
                    startActivity(intent)
                }
                if(menuInput.text.toString().lowercase()=="order"||menuInput.text.toString().lowercase()=="order food"){
                    val intent= Intent(this, OrderFood::class.java)
                    startActivity(intent)
                }
                if(menuInput.text.toString().lowercase()=="menu"||menuInput.text.toString().lowercase()=="view menu"){
                    val intent = Intent(this, ViewMenu::class.java)
                    startActivity(intent)
                }

                true // Return 'true' to indicate that the event has been handled.
            } else {
                false // Return 'false' if the event is not handled.
            }
        }
    }
}