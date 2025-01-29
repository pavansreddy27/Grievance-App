package com.example.pro156;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
public class AdminComplaintAdapter extends ArrayAdapter<Complaint> {
    private Context context;
    private List<Complaint> complaintList;

    public AdminComplaintAdapter(Context context, List<Complaint> complaintList) {
        super(context, android.R.layout.simple_list_item_1, complaintList);
        this.context = context;
        this.complaintList = complaintList;
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        Complaint currentComplaint = complaintList.get(position);
        TextView textView = listItem.findViewById(android.R.id.text1);
        textView.setText(currentComplaint.getTitle());

        return listItem;
    }
}