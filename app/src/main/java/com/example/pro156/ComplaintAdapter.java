package com.example.pro156;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ComplaintAdapter extends ArrayAdapter<Map<String, Object>> {

    private Context context;
    private List<Map<String, Object>> complaints;

    public ComplaintAdapter(Context context, List<Map<String, Object>> complaints) {
        super(context, R.layout.complaint_list_item, complaints);
        this.context = context;
        this.complaints = complaints;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Reuse or inflate the list item view
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.complaint_list_item, parent, false);
        }

        // Get the complaint at the current position
        Map<String, Object> complaint = complaints.get(position);

        // Get references to the TextViews in complaint_list_item.xml
        TextView titleTextView = listItem.findViewById(R.id.complaintTitle);
        TextView descriptionTextView = listItem.findViewById(R.id.complaintDescription);
        TextView statusTextView = listItem.findViewById(R.id.complaintStatus);

        // Set data to the TextViews
        titleTextView.setText((String) complaint.get("title"));  // Assuming "title" key exists
        descriptionTextView.setText((String) complaint.get("description"));  // Assuming "description" key exists
        statusTextView.setText((String) complaint.get("status"));  // Assuming "status" key exists

        return listItem;
    }
}
