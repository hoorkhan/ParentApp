package com.hooria.parentapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hooria.parentapp.R;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.time.Instant;

public class QrCodeActivity extends AppCompatActivity {
    private ImageView qrCodeImage;
    private Button backButton;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        qrCodeImage = findViewById(R.id.qrCodeImageView);
        backButton = findViewById(R.id.backButton);
        db = FirebaseFirestore.getInstance();

        fetchQRCode();

        backButton.setOnClickListener(v -> finish());
    }

    private void fetchQRCode() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String guardianCNIC = prefs.getString("CNIC", null);

        if (guardianCNIC == null) {
            Toast.makeText(this, "Error: CNIC not found. Please log in again.", Toast.LENGTH_LONG).show();
            Log.e("QRCodeActivity", "SharedPreferences CNIC is null");
            return;
        }

        db.collection("guardians").whereEqualTo("CNIC", guardianCNIC)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        String qrData = documentSnapshot.getString("QRcodeData");
                        if (qrData != null) {
                            generateQRCode(qrData);
                        } else {
                            Toast.makeText(this, "QR Code not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Guardian not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching QR Code", e));
    }

    private void generateQRCode(String qrData) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 250, 250);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(bitMatrix);
            qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.e("QR Code", "Error generating QR Code", e);
        }
    }
}

