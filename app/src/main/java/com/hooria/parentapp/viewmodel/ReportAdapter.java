package com.hooria.parentapp.viewmodel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hooria.parentapp.R;
import com.hooria.parentapp.model.PickUpReport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<PickUpReport> reports;

    public ReportAdapter(List<PickUpReport> reports) {
        this.reports = reports;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        holder.bind(reports.get(position));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public void updateReports(List<PickUpReport> newReports) {
        this.reports = newReports;
        notifyDataSetChanged();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, pickUpTime, status, exceptionDetails;

        public ReportViewHolder(View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            pickUpTime = itemView.findViewById(R.id.pickUpTime);

        }

        public void bind(PickUpReport report) {
            studentName.setText("Name: " + report.getSname());

            if (report.getPickUpTime() != null) {
                Date date = report.getPickUpTime(); // FIXED
                DateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
                pickUpTime.setText("Time: " + df.format(date));
            } else {
                pickUpTime.setText("Time: N/A");
            }
        }
    }

}
