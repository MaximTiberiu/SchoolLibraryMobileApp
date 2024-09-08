package com.example.schoolappmobileapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth:          FirebaseAuth
    private lateinit var db:            FirebaseFirestore
    private lateinit var selectedClass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth    = FirebaseAuth.getInstance()
        db      = FirebaseFirestore.getInstance()

        // add values to classSpinner
        val classSpinner = findViewById<Spinner>(R.id.classSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.class_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            classSpinner.adapter = adapter
        }

        classSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedClass = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedClass = "1" // default value
            }
        }

        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            val firstName   = findViewById<EditText>(R.id.firstNameInput).text.toString()
            val lastName    = findViewById<EditText>(R.id.lastNameInput).text.toString()
            val email       = findViewById<EditText>(R.id.emailInput).text.toString()
            val password    = findViewById<EditText>(R.id.passwordInput).text.toString()
            val phone       = findViewById<EditText>(R.id.phoneInput).text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && phone.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user    = auth.currentUser
                            val userId  = user?.uid

                            // save user's data in Firestore
                            val userData = hashMapOf(
                                "firstName" to firstName,
                                "lastName"  to lastName,
                                "email"     to email,
                                "phone"     to phone,
                                "class"     to selectedClass
                            )

                            userId?.let {
                                db.collection("users").document(it).set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
