package com.hooria.parentapp.ui.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hooria.parentapp.AppDatabase;
import com.hooria.parentapp.R;
import com.hooria.parentapp.UserEntity;
import com.hooria.parentapp.ui.dashboard.ParentDashboardActivity;

import android.view.View;

public class Login extends AppCompatActivity {



    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private AppDatabase localDb;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        localDb = AppDatabase.getInstance(getApplicationContext());

        emailEditText = findViewById(R.id.Email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!isValidInput(email, password)) return;

            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);

            signInGuardian(email, password);
        });
    }

    private boolean isValidInput(String email, String password) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!email.matches(emailPattern)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void signInGuardian(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) {
                        handleError("Unexpected login failure");
                        return;
                    }

                    String uid = user.getUid();


                    db.collection("users").document(uid)
                            .get()
                            .addOnSuccessListener(userDoc -> {
                                if (userDoc.exists()) {
                                    String role = userDoc.getString("role");
                                    String schoolId = userDoc.getString("schoolId");
                                    String userEmail = userDoc.getString("email");


                                    UserEntity userEntity = new UserEntity();
                                    userEntity.uid = uid;
                                    userEntity.email = userEmail;
                                    userEntity.role = role;
                                    userEntity.schoolId = schoolId;

                                    new Thread(() -> localDb.userDao().insert(userEntity)).start();

                                    if ("guardian".equals(role)) {
                                        fetchGuardianProfile(uid);
                                    } else {
                                        handleError("Unauthorized: Not a guardian");
                                        auth.signOut();
                                    }

                                } else {
                                    handleError("User record not found");
                                    auth.signOut();
                                }
                            })
                            .addOnFailureListener(e -> {

                                new Thread(() -> {
                                    UserEntity cached = localDb.userDao().getUserById(uid);
                                    runOnUiThread(() -> {
                                        if (cached != null && "guardian".equals(cached.role)) {
                                            fetchGuardianProfile(uid);
                                        } else {
                                            handleError("Offline: No saved guardian user found");
                                            auth.signOut();
                                        }
                                    });
                                }).start();
                            });

                })
                .addOnFailureListener(e -> {
                    handleError("Login failed: " + e.getMessage());
                });
    }

    private void fetchGuardianProfile(String uid) {
        db.collection("guardians").document(uid)
                .get()
                .addOnSuccessListener(guardianDoc -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);

                    if (guardianDoc.exists()) {
                        Intent intent = new Intent(this, ParentDashboardActivity.class);
                        intent.putExtra("guardianDocId", uid);
                        startActivity(intent);
                        finish();
                    } else {
                        handleError("Guardian profile not found");
                        auth.signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    handleError("Failed to load guardian info: " + e.getMessage());
                    auth.signOut();
                });
    }

    private void handleError(String message) {
        progressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}









