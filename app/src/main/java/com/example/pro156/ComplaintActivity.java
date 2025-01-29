package com.example.pro156;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ComplaintActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextInputEditText titleEditText, descriptionEditText;
    private TextInputLayout titleTextInputLayout, descriptionTextInputLayout;
    private Spinner typeSpinner;
    public MaterialButton submitComplaintButton, viewComplaintsButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    public String[] complaintTypes = {"Transport", "Classroom", "Hostel", "Other"};
    public List<Map<String, Object>> userComplaints = new ArrayList<>(); // Stores user's complaints

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        titleTextInputLayout = findViewById(R.id.titleTextInputLayout);
        descriptionTextInputLayout = findViewById(R.id.descriptionTextInputLayout);
        typeSpinner = findViewById(R.id.typeSpinner);
        submitComplaintButton = findViewById(R.id.submitComplaintButton);
        viewComplaintsButton = findViewById(R.id.viewComplaintsButton); // Add button view ID

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("complaints");

        // Set up the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, complaintTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(this);

        submitComplaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = Objects.requireNonNull(titleEditText.getText()).toString();
                String description = Objects.requireNonNull(descriptionEditText.getText()).toString();
                String type = typeSpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(title)) {
                    titleTextInputLayout.setError("Please enter title");
                    return;
                } else {
                    titleTextInputLayout.setError(null);
                }

                if (TextUtils.isEmpty(description)) {
                    descriptionTextInputLayout.setError("Please enter description");
                    return;
                } else {
                    descriptionTextInputLayout.setError(null);
                }

                String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                Map<String, Object> complaint = new HashMap<>();
                complaint.put("userId", userId);
                complaint.put("title", title);
                complaint.put("description", description);
                complaint.put("type", type);
                complaint.put("status", "Pending"); // Initial status

                databaseReference.push().setValue(complaint)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ComplaintActivity.this, "Complaint submitted successfully!", Toast.LENGTH_SHORT).show();
                                    // Clear input fields
                                    titleEditText.setText("");
                                    descriptionEditText.setText("");
                                } else {
                                    // Handle the failure case
                                    Exception exception = task.getException();
                                    if (exception != null) {
                                        Log.e("ComplaintActivity", "Failed to submit complaint: " + exception.getMessage());
                                    }
                                    Toast.makeText(ComplaintActivity.this, "Failed to submit complaint.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        viewComplaintsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                Query userComplaintsQuery = databaseReference.orderByChild("userId").equalTo(userId);

                userComplaintsQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userComplaints.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Map<String, Object> complaint = (Map<String, Object>) snapshot.getValue();
                            userComplaints.add(complaint);
                        }

                        if (userComplaints.isEmpty()) {
                            Toast.makeText(ComplaintActivity.this, "No complaints found.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(ComplaintActivity.this, UserComplaintsActivity.class);
                            intent.putExtra("userComplaints", (ArrayList<Map<String, Object>>) userComplaints);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("ComplaintActivity", "Error fetching user complaints: " + databaseError.getMessage());
                        Toast.makeText(ComplaintActivity.this, "Failed to fetch complaints.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Handle spinner selection if needed (optional)
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle spinner selection if needed (optional)
    }
}