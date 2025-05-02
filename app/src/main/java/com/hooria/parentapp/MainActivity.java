package com.hooria.parentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hooria.parentapp.ui.auth.Login;
import com.hooria.parentapp.ui.dashboard.ParentDashboardActivity;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private AppDatabase localDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        localDb = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "guardian_local_db")
                .fallbackToDestructiveMigration() // Safe fallback
                .build();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = auth.getCurrentUser();

            if (currentUser != null) {
                String uid = currentUser.getUid();

                // ✅ Check Room data before launching dashboard
                new Thread(() -> {
                    try {
                        UserEntity user = localDb.userDao().getUserById(uid);
                        runOnUiThread(() -> {
                            if (user != null && "guardian".equals(user.role)) {
                                startActivity(new Intent(MainActivity.this, ParentDashboardActivity.class));
                            } else {
                                startActivity(new Intent(MainActivity.this, Login.class));
                            }
                            finish();
                        });
                    } catch (Exception e) {
                        Log.e("MainActivity", "Room load failed: " + e.getMessage());
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Error loading local user", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, Login.class));
                            finish();
                        });
                    }
                }).start();
            } else {
                // Not logged in
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        }, 1500); // 1.5 second splash delay
    }
}