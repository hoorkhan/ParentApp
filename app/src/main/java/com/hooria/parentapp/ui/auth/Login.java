package com.hooria.parentapp.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hooria.parentapp.R;
import com.hooria.parentapp.ui.dashboard.ParentDashboardActivity;

import android.view.View;

public class Login extends AppCompatActivity {



    private EditText emailEditText, cnicEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private SharedPreferences prefs;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            auth = FirebaseAuth.getInstance();


            db = FirebaseFirestore.getInstance();

            emailEditText = findViewById(R.id.Email);
            cnicEditText = findViewById(R.id.CNIC);
            loginButton = findViewById(R.id.loginButton);
            progressBar = findViewById(R.id.progressBar);
            prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);


            signInAnonymously();

            loginButton.setOnClickListener(v -> {
                String email = emailEditText.getText().toString().trim();
                String cnic = cnicEditText.getText().toString().trim();

                if (email.isEmpty() || cnic.isEmpty()) {
                    Toast.makeText(this, "Please enter email and CNIC", Toast.LENGTH_SHORT).show();
                }
                else if (!cnic.matches("\\d{13}")) {
                    Toast.makeText(this, "CNIC must be exactly 13 digits", Toast.LENGTH_SHORT).show();}
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginButton.setEnabled(false);
                    checkGuardianCredentials(email, cnic);
                }
            });
        }

    private void signInAnonymously() {
        auth.signInAnonymously()
                .addOnSuccessListener(authResult -> {

                    Toast.makeText(this, "Signed in anonymously", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkGuardianCredentials(String email, String cnic) {
        db.collection("guardians")
                .whereEqualTo("Email", email)
                .whereEqualTo("CNIC", cnic)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);

                    if (!querySnapshot.isEmpty()) {

                        prefs.edit().putString("CNIC", cnic).apply();


                        Intent intent = new Intent(this, ParentDashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid email or CNIC", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}













