package com.piyush.a02_buzzlink_chat_application

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView

class registration : AppCompatActivity() {

    private lateinit var loginButton: TextView
    private lateinit var rgUsername: EditText
    private lateinit var rgEmail: EditText
    private lateinit var rgPassword: EditText
    private lateinit var rgRepassword: EditText
    private lateinit var rgSignup: Button
    private lateinit var rgProfileImg: CircleImageView
    private lateinit var auth: FirebaseAuth
    private var imageURI: Uri? = null
    private var imageUriString: String? = null
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        progressDialog = ProgressDialog(this).apply {
            setMessage("Establishing The Account")
            setCancelable(false)
        }

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        loginButton = findViewById(R.id.loginbut)
        rgUsername = findViewById(R.id.rgusername)
        rgEmail = findViewById(R.id.rgemail)
        rgPassword = findViewById(R.id.rgpassword)
        rgRepassword = findViewById(R.id.rgrepassword)
        rgProfileImg = findViewById(R.id.profilerg0)
        rgSignup = findViewById(R.id.signupbutton)

        loginButton.setOnClickListener {
            val intent = Intent(this@registration, login::class.java)
            startActivity(intent)
            finish()
        }

        rgSignup.setOnClickListener {
            val name = rgUsername.text.toString()
            val email = rgEmail.text.toString()
            val password = rgPassword.text.toString()
            val confirmPassword = rgRepassword.text.toString()
            val status = "Hey I'm Using This Application"

            when {
                TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) -> {
                    progressDialog.dismiss()
                    Toast.makeText(this@registration, "Please Enter Valid Information", Toast.LENGTH_SHORT).show()
                }
                !email.matches(emailPattern.toRegex()) -> {
                    progressDialog.dismiss()
                    rgEmail.error = "Type A Valid Email Here"
                }
                password.length < 6 -> {
                    progressDialog.dismiss()
                    rgPassword.error = "Password Must Be 6 Characters Or More"
                }
                password != confirmPassword -> {
                    progressDialog.dismiss()
                    rgPassword.error = "The Password Doesn't Match"
                }
                else -> {
                    progressDialog.show()
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val id = task.result?.user?.uid ?: return@addOnCompleteListener
                            val reference = database.getReference("user").child(id)
                            val storageReference = storage.getReference("Upload").child(id)

                            if (imageURI != null) {
                                storageReference.putFile(imageURI!!).addOnCompleteListener { uploadTask ->
                                    if (uploadTask.isSuccessful) {
                                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                                            imageUriString = uri.toString()
                                            val users = Users(id, name, email, password, imageUriString, status)
                                            reference.setValue(users).addOnCompleteListener { userCreationTask ->
                                                progressDialog.dismiss()
                                                if (userCreationTask.isSuccessful) {
                                                    Toast.makeText(this@registration, "User created successfully", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(this@registration, "Error in creating the user", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                val defaultImageUri = "https://firebasestorage.googleapis.com/v0/b/buzzlink-chat-application.appspot.com/o/Upload%2FkACqVcyn3xMtYoK3BCylkv5rT0S2?alt=media&token=86035ff0-f35a-452f-b1b2-aa52f08e856a"
                                val users = Users(id, name, email, password, defaultImageUri, status)
                                reference.setValue(users).addOnCompleteListener { userCreationTask ->
                                    progressDialog.dismiss()
                                    if (userCreationTask.isSuccessful) {
                                        Toast.makeText(this@registration, "User created successfully", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this@registration, "Error in creating the user", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(this@registration, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        rgProfileImg.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"))
        }
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                imageURI = uri
                rgProfileImg.setImageURI(uri)
            }
        }
    }
}
