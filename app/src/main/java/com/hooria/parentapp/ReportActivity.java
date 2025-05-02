package com.hooria.parentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hooria.parentapp.model.PickUpReport;
import com.hooria.parentapp.viewmodel.ReportAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView emptyText;
    private Button backButton;
    private AppDatabase localDb;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        recyclerView = findViewById(R.id.recyclerViewReports);
        progressBar = findViewById(R.id.progressBarReports);
        emptyText = findViewById(R.id.emptyText);
        backButton = findViewById(R.id.backbtn2);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(new ArrayList<>());
        recyclerView.setAdapter(reportAdapter);

        db = FirebaseFirestore.getInstance();

        backButton.setOnClickListener(v -> finish());

        // Directly load reports using guardian UID
        loadReports();
    }

    private void loadReports() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String guardianUID = currentUser.getUid();
        progressBar.setVisibility(View.VISIBLE);

        db.collection("pick_up_activities")
                .whereEqualTo("guardianUID", guardianUID)
                .orderBy("pickUpTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    progressBar.setVisibility(View.GONE);
                    List<PickUpReport> reports = new ArrayList<>();
                    List<PickUpReportEntity> entities = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        PickUpReport report = doc.toObject(PickUpReport.class);
                        if (report != null) {
                            reports.add(report);

                            // Prepare Room entity
                            PickUpReportEntity entity = new PickUpReportEntity();
                            entity.Sname = report.getSname();
                            entity.pickUpTime = report.getPickUpTime();
                            entities.add(entity);
                        }
                    }

                    if (reports.isEmpty()) {
                        showEmptyState("🚫 No reports found");
                    } else {
                        emptyText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        reportAdapter.updateReports(reports);

                        // ✅ Save reports to Room
                        new Thread(() -> {
                            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "guardian_local_db").build();
                            db.pickUpReportDao().insertReports(entities);
                        }).start();
                    }
                })
                .addOnFailureListener(e -> {
                    new Thread(() -> {
                        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "guardian_local_db").build();
                        List<PickUpReportEntity> cached = db.pickUpReportDao().getAllReports();

                        List<PickUpReport> fallbackList = new ArrayList<>();
                        for (PickUpReportEntity r : cached) {
                            PickUpReport report = new PickUpReport();
                            report.setSname(r.Sname);
                            report.setPickUpTime(r.pickUpTime);
                            fallbackList.add(report);
                        }

                        runOnUiThread(() -> {
                            reportAdapter.updateReports(fallbackList);
                            recyclerView.setVisibility(View.VISIBLE);
                            Toast.makeText(this, "Showing offline data", Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                });
    }

    private void showEmptyState(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyText.setText(message);
        emptyText.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}





