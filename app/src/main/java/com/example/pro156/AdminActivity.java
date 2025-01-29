package com.example.pro156;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private ListView complaintListView;
    private DatabaseReference databaseReference;
    private List<Complaint> complaintList;
    private AdminComplaintAdapter adapter;
    private EditText commentEditText;
    private Button updateStatusButton, addCommentButton;
    private Spinner statusSpinner;
    private String[] statusOptions = {"Pending", "In Progress", "Resolved", "Completed"};
    private String selectedStatus = "Pending";
    private Complaint selectedComplaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize views
        complaintListView = findViewById(R.id.complaintListView);
        commentEditText = findViewById(R.id.commentEditText);
        updateStatusButton = findViewById(R.id.updateStatusButton);
        addCommentButton = findViewById(R.id.addCommentButton);
        statusSpinner = findViewById(R.id.statusSpinner);

        databaseReference = FirebaseDatabase.getInstance().getReference("complaints");
        complaintList = new ArrayList<>();
        adapter = new AdminComplaintAdapter(this, complaintList);
        complaintListView.setAdapter(adapter);

        // Set up the status spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = statusOptions[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Fetch complaints from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                complaintList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Complaint complaint = snapshot.getValue(Complaint.class);
                    if (complaint != null) {
                        complaint.setId(snapshot.getKey()); // Set the ID using Firebase key
                        complaintList.add(complaint);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AdminActivity", "Error reading complaints: " + databaseError.getMessage());
            }
        });

        // Handle item selection in the ListView
        complaintListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedComplaint = complaintList.get(position);
            Log.d("AdminActivity", "Selected Complaint: " + selectedComplaint.getTitle());
        });

        // Update status button
        updateStatusButton.setOnClickListener(v -> {
            if (selectedComplaint == null) {
                Toast.makeText(AdminActivity.this, "Please select a complaint.", Toast.LENGTH_SHORT).show();
                return;
            }

            String complaintId = selectedComplaint.getId();
            if (TextUtils.isEmpty(complaintId)) {
                Toast.makeText(AdminActivity.this, "Complaint ID is missing.", Toast.LENGTH_SHORT).show();
                return;
            }

            databaseReference.child(complaintId).child("status").setValue(selectedStatus)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            selectedComplaint.setStatus(selectedStatus);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(AdminActivity.this, "Status updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdminActivity.this, "Failed to update status.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Add comment button
        addCommentButton.setOnClickListener(v -> {
            if (selectedComplaint == null) {
                Toast.makeText(AdminActivity.this, "Please select a complaint.", Toast.LENGTH_SHORT).show();
                return;
            }

            String complaintId = selectedComplaint.getId();
            if (TextUtils.isEmpty(complaintId)) {
                Toast.makeText(AdminActivity.this, "Complaint ID is missing.", Toast.LENGTH_SHORT).show();
                return;
            }

            String comment = commentEditText.getText().toString();
            if (!TextUtils.isEmpty(comment)) {
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("comment", comment);
                commentMap.put("timestamp", System.currentTimeMillis());
                databaseReference.child(complaintId).child("comments").push().setValue(commentMap)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                commentEditText.setText("");
                                Toast.makeText(AdminActivity.this, "Comment added successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AdminActivity.this, "Failed to add comment.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(AdminActivity.this, "Please enter a comment.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
