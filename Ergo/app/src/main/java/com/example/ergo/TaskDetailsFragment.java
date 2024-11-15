package com.example.ergo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ergo.model.Task;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TaskAPI;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailsFragment extends Fragment {
    private Task task;
    private User user; // Add user field

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_display_fragment, container, false);

        // Retrieve the Task and User objects from the arguments
        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable("task");
            user = (User) getArguments().getSerializable("user"); // Retrieve user
        }

        // Fetch task details if task is null (to ensure we have the latest data)
        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable("task");
            displayTaskDetails(view);
        }

        return view;
    }

    private void displayTaskDetails(View view) {
        // Populate your views with task details
        TextView titleTextView = view.findViewById(R.id.TitleText);
        TextView priorityTextView = view.findViewById(R.id.PrioritySpinnerText);
        TextView statusTextView = view.findViewById(R.id.StatusSpinnerText);
        TextView startDateTextView = view.findViewById(R.id.StartDateTextView);
        TextView endDateTextView = view.findViewById(R.id.EndDateTextView);
        TextView descriptionTextView = view.findViewById(R.id.DescriptionPlaceholder);

        titleTextView.setText(task.getTitle());
        priorityTextView.setText(getPriorityString(task.getPriority()));
        statusTextView.setText(getStatusString(task.getStatus()));
        startDateTextView.setText(formatIsoDate(task.getStartDate()));
        endDateTextView.setText(formatIsoDate(task.getStopDate()));
        descriptionTextView.setText(task.getDescription());
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void populateTaskDetails() {
        // Ensure the view is not null before accessing it
        if (getView() == null) return;

        // Populate your views with task details
        TextView titleTextView = getView().findViewById(R.id.TitleText);
        TextView priorityTextView = getView().findViewById(R.id.PrioritySpinnerText);
        TextView statusTextView = getView().findViewById(R.id.StatusSpinnerText);
        TextView startDateTextView = getView().findViewById(R.id.StartDateTextView);
        TextView endDateTextView = getView().findViewById(R.id.EndDateTextView);
        TextView descriptionTextView = getView().findViewById(R.id.DescriptionPlaceholder);

        titleTextView.setText(task.getTitle());
        priorityTextView.setText(getPriorityString(task.getPriority()));
        statusTextView.setText(getStatusString(task.getStatus()));
        startDateTextView.setText(formatIsoDate(task.getStartDate()));
        endDateTextView.setText(formatIsoDate(task.getStopDate()));
        descriptionTextView.setText(task.getDescription());
    }

    private String getPriorityString(int priority) {
        switch (priority) {
            case 1:
                return "Severe";
            case 2:
                return "Medium";
            case 3:
                return "Light";
            default:
                return "Unknown";
        }
    }

    private String getStatusString(int status) {
        switch (status) {
            case 1:
                return "Not Started";
            case 2:
                return "In Progress";
            case 3:
                return "Completed";
            default:
                return "Unknown";
        }
    }

    private String formatIsoDate(String isoDate) {
        try {
            if (isoDate == null || isoDate.isEmpty()) {
                return "Invalid date";
            }

            LocalDate date = LocalDate.parse(isoDate, DateTimeFormatter.ISO_DATE_TIME);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM dd", Locale.getDefault());
            return date.format(outputFormatter);
        } catch (DateTimeParseException e) {
            Log.e("TaskDetailsFragment", "Failed to parse date: " + isoDate, e);
            return "Invalid date";
        }
    }
}