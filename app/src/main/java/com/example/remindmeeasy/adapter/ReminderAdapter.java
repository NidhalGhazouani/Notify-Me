package com.example.remindmeeasy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindmeeasy.R;
import com.example.remindmeeasy.model.reminder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<reminder> remindersList = new ArrayList<>();
    private OnDeleteClickListener deleteClickListener;
    private OnUpdateClickListener updateClickListener;
    private final Context context;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public interface OnUpdateClickListener {
        void onUpdateClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void setOnUpdateClickListener(OnUpdateClickListener listener) {
        this.updateClickListener = listener;
    }

    public ReminderAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        reminder currentReminder = remindersList.get(position);
        holder.reminderName.setText(currentReminder.getName());
        holder.reminderDescription.setText(currentReminder.getDescription());

        Date dateTime = currentReminder.getDateTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
        holder.reminderTime.setText(dateFormat.format(dateTime));

        holder.updateButton.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION && updateClickListener != null) {
                updateClickListener.onUpdateClick(clickedPosition);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION && deleteClickListener != null) {
                deleteClickListener.onDeleteClick(clickedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return remindersList.size();
    }

    public void setReminders(List<reminder> reminders) {
        this.remindersList = reminders;
        notifyDataSetChanged();
    }

    public reminder getReminderAt(int position) {
        return remindersList.get(position);
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView reminderName, reminderDescription, reminderTime;
        ImageButton deleteButton, updateButton;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderName = itemView.findViewById(R.id.Name);
            reminderDescription = itemView.findViewById(R.id.Description);
            reminderTime = itemView.findViewById(R.id.Time);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            updateButton = itemView.findViewById(R.id.updateButton);
        }
    }
}
