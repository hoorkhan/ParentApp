package com.hooria.parentapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hooria.parentapp.model.PickUpReport;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<PickUpReport> reports;

    public ReportAdapter(List<PickUpReport> reports) {
        this.reports = reports;
    }

    public void updateReports(List<PickUpReport> updated) {
        this.reports = updated;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        PickUpReport report = reports.get(position);

        holder.studentName.setText(report.getSname());

        if (report.getPickUpTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault());
            String formattedTime = sdf.format(report.getPickUpTime());
            holder.pickUpTime.setText("Pick-up: " + formattedTime);
        } else {
            holder.pickUpTime.setText("Pick-up: N/A");
        }
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, pickUpTime;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            pickUpTime = itemView.findViewById(R.id.pickUpTime);
        }
    }
}

