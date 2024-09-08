package com.example.schoolappmobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.schoolappmobileapp.ui.theme.SchoolAppMobileAppTheme
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailInput      = findViewById<EditText>(R.id.emailInput)
        val passwordInput   = findViewById<EditText>(R.id.passwordInput)
        val loginButton     = findViewById<Button>(R.id.loginButton)
        val registerLink    = findViewById<TextView>(R.id.registerLink)

        loginButton.setOnClickListener {
            val email       = emailInput.text.toString()
            val password    = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // login successful
                            auth.currentUser

                            // navigate to the main activity
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            // if sign in fails, display a error message to the user
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        registerLink.setOnClickListener {
            // navigate to the registration activity
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Demo() {
    SchoolAppMobileAppTheme {
    }
}