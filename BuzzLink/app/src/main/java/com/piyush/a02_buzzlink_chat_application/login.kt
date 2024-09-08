package com.piyush.a02_buzzlink_chat_application
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class  login : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var logsignup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            navigateToMainActivity()
            return // Exit the method to avoid showing the login screen
        }

        progressDialog = ProgressDialog(this).apply {
            setMessage("Please Wait...")
            setCancelable(false)
        }

        button = findViewById(R.id.logbutton)
        email = findViewById(R.id.editTexLogEmail)
        password = findViewById(R.id.editTextLogPassword)
        logsignup = findViewById(R.id.logsignup)

        logsignup.setOnClickListener {
            val intent = Intent(this@login, registration::class.java)
            startActivity(intent)
            finish()
        }

        button.setOnClickListener {
            val Email = email.text.toString()
            val pass = password.text.toString()
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

            when {
                TextUtils.isEmpty(Email) -> Toast.makeText(this@login, "Please Enter the Email", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(pass) -> Toast.makeText(this@login, "Please Enter the Password", Toast.LENGTH_SHORT).show()
                !Email.matches(emailPattern.toRegex()) -> email.error = "Provide a correct Email Address"
                pass.length < 6 -> {
                    password.error = "Enter More than 6 characters for password"
                    Toast.makeText(this@login, "Password Should Be More Than Six Characters", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    progressDialog.show()
                    auth.signInWithEmailAndPassword(Email, pass).addOnCompleteListener { task ->
                        progressDialog.dismiss()
                        if (task.isSuccessful) {
                            navigateToMainActivity()
                        } else {
                            Toast.makeText(this@login, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@login, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
