package com.example.pro156;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserComplaintsActivity extends AppCompatActivity {

    private ListView complaintListView;
    private List<Map<String, Object>> userComplaints = new ArrayList<>();
    private ComplaintAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_complaints);

        complaintListView = findViewById(R.id.complaintListView);
        databaseReference = FirebaseDatabase.getInstance().getReference("complaints");

        // Fetch user complaints from Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId != null) {
            Query userComplaintsQuery = databaseReference.orderByChild("userId").equalTo(userId);
            userComplaintsQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userComplaints.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getValue() instanceof Map) {
                            userComplaints.add((Map<String, Object>) snapshot.getValue());
                        }
                    }

                    // Update the adapter with the new data
                    if (adapter == null) {
                        adapter = new ComplaintAdapter(UserComplaintsActivity.this, userComplaints);
                        complaintListView.setAdapter(adapter);
                    } else {
                        // Notify the adapter to refresh the view
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }

    public static class ComplaintAdapter extends ArrayAdapter<Map<String, Object>> {

        private Context context;
        private List<Map<String, Object>> complaints;

        public ComplaintAdapter(Context context, List<Map<String, Object>> complaints) {
            super(context, R.layout.complaint_list_item, complaints);
            this.context = context;
            this.complaints = complaints;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(context).inflate(R.layout.complaint_list_item, parent, false);
            }

            Map<String, Object> complaint = complaints.get(position);
            TextView titleTextView = listItem.findViewById(R.id.complaintTitle);
            TextView descriptionTextView = listItem.findViewById(R.id.complaintDescription);
            TextView statusTextView = listItem.findViewById(R.id.complaintStatus);

            titleTextView.setText((String) complaint.get("title"));
            descriptionTextView.setText((String) complaint.get("description"));
            statusTextView.setText((String) complaint.get("status")); // Update this line

            return listItem;
        }
    }
}