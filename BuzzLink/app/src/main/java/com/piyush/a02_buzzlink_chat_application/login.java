// package com.piyush.a02_buzzlink_chat_application;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class login extends AppCompatActivity {
//
//    Button button;
//    EditText email, password;
//    FirebaseAuth auth;
//    ProgressDialog progressDialog;
//    TextView logsignup;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_login);
//
//        auth = FirebaseAuth.getInstance();
//
//        // Check if the user is already logged in
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser != null) {
//            navigateToMainActivity();
//            return; // Exit the method to avoid showing the login screen
//        }
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Please Wait...");
//        progressDialog.setCancelable(false);
//        button = findViewById(R.id.logbutton);
//        email = findViewById(R.id.editTexLogEmail);
//        password = findViewById(R.id.editTextLogPassword);
//        logsignup = findViewById(R.id.logsignup);
//
//        logsignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(login.this, registration.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String Email = email.getText().toString();
//                String pass = password.getText().toString();
//                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//
//                if (TextUtils.isEmpty(Email)) {
//                    Toast.makeText(login.this, "Please Enter the Email", Toast.LENGTH_SHORT).show();
//                } else if (TextUtils.isEmpty(pass)) {
//                    Toast.makeText(login.this, "Please Enter the Password", Toast.LENGTH_SHORT).show();
//                } else if (!Email.matches(emailPattern)) {
//                    email.setError("Provide a correct Email Address");
//                } else if (pass.length() < 6) {
//                    password.setError("Enter More than 6 characters for password");
//                    Toast.makeText(login.this, "Password Should Be More Than Six Characters", Toast.LENGTH_SHORT).show();
//                } else {
//                    progressDialog.show();
//                    auth.signInWithEmailAndPassword(Email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            progressDialog.dismiss();
//                            if (task.isSuccessful()) {
//                                navigateToMainActivity();
//                            } else {
//                                Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//            }
//        });
//    }
//
//    private void navigateToMainActivity() {
//        Intent intent = new Intent(login.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
//}