package com.hooria.parentapp.ui.dashboard;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hooria.parentapp.Feedbackactivity;
import com.hooria.parentapp.R;
import com.hooria.parentapp.ReportActivity;
import com.hooria.parentapp.StudentAdapter;
import com.hooria.parentapp.model.Student;
import com.hooria.parentapp.ui.QrCodeActivity;
import com.hooria.parentapp.ui.auth.Login;
import java.util.ArrayList;
import java.util.List;
public class ParentDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private TextView Gname, number;
    private RecyclerView studentList;
    private FloatingActionButton showQRButton;
    private FirebaseFirestore db;
    private List<Student> students;
    private StudentAdapter studentAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences prefs;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);
        db = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.toolbar);
        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Gname = findViewById(R.id.Gname);
        number = findViewById(R.id.number);
        studentList = findViewById(R.id.studentList);
        showQRButton = findViewById(R.id.showQRButton);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        students = new ArrayList<>();
        studentAdapter = new StudentAdapter(students,this);
        studentList.setLayoutManager(new LinearLayoutManager(this));
        studentList.setAdapter(studentAdapter);

        fetchGuardianDetails();
        fetchStudents();

        showQRButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, QrCodeActivity.class);
            startActivity(intent);
        });
    }

    private void fetchGuardianDetails() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String cnic = prefs.getString("CNIC", null);

        if (cnic == null) {
            Toast.makeText(this, "CNIC not found. Please log in again.", Toast.LENGTH_SHORT).show();
            logoutUser();
            return;
        }

        db.collection("guardians")
                .whereEqualTo("CNIC", cnic)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        Gname.setText(documentSnapshot.getString("Gname"));
                        number.setText(documentSnapshot.getString("number"));
                        fetchStudents();
                    } else {
                        Toast.makeText(this, "Guardian not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching guardian", e));
    }

    private void fetchStudents() {
        String guardianCNIC = prefs.getString("CNIC", null);
        if (guardianCNIC == null) {
            Toast.makeText(this, "Guardian CNIC not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("students")
                .whereEqualTo("CNIC", guardianCNIC)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        students.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Student student = document.toObject(Student.class);
                            Log.d("Firestore", "Fetched Student: " + student.getSname() + ", Image URL: " + student.getImage());
                            students.add(student);
                        }
                        studentAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "No students found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );}


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_report) {
            startActivity(new Intent(this, ReportActivity.class));
        } else if (id == R.id.nav_feedback) {
            startActivity(new Intent(this, Feedbackactivity.class));
        }
        else if (id == R.id.nav_logout) {
            logoutUser();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}





