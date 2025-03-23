package com.hooria.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Feedbackactivity extends AppCompatActivity {
    private EditText feedbackInput;
    private Button submitFeedbackButton;
    private FirebaseFirestore db;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbackactivity);
        db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        feedbackInput = findViewById(R.id.feedbackInput);
        submitFeedbackButton = findViewById(R.id.submitFeedbackButton);

        submitFeedbackButton.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String feedbackText = feedbackInput.getText().toString().trim();
        String guardianCNIC = prefs.getString("CNIC", null);

        if (feedbackText.isEmpty()) {
            Toast.makeText(this, "Please enter feedback", Toast.LENGTH_SHORT).show();
            return;
        }
        if (guardianCNIC == null) {
            Toast.makeText(this, "User not identified. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("guardianCNIC", guardianCNIC);
        feedback.put("feedbackText", feedbackText);
        feedback.put("timestamp", FieldValue.serverTimestamp());

        db.collection("feedback")
                .add(feedback)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}