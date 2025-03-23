package com.hooria.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.hooria.parentapp.ui.auth.Login;
import com.hooria.parentapp.ui.dashboard.ParentDashboardActivity;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("isFirstTime", true);
        String savedCNIC = prefs.getString("CNIC", null);

        new Handler().postDelayed(() -> {
            if (isFirstTime) {
                startActivity(new Intent(this, Login.class));
                prefs.edit().putBoolean("isFirstTime", false).apply();
            } else if (savedCNIC != null) {
                Intent intent = new Intent(this, ParentDashboardActivity.class);
                startActivity(intent);
            } else {
                startActivity(new Intent(this, Login.class));
            }
            finish();
        }, 1500);
    }
}