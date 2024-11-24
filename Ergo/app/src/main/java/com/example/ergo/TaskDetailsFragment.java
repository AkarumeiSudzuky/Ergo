package com.example.ergo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;


public class TaskDetailsFragment extends Fragment {
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    private Task task;
    private User user;
    private TextView exportButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_display_fragment, container, false);

        exportButton = view.findViewById(R.id.exportButton);

        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable("task");
            user = (User) getArguments().getSerializable("user");
        }

        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable("task");
            displayTaskDetails(view);
        }

        exportButton.setOnClickListener(v -> exportTaskData());

        return view;
    }

    private void displayTaskDetails(View view) {
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

    private void exportTaskData() {
        if (task == null) {
            showToast("No task data available to export.");
            return;
        }

        if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        } else {
            performExport();
        }
    }

    private void requestStoragePermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showPermissionRationaleDialog();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    private void showPermissionRationaleDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Permission Required")
                .setMessage("This app requires access to your storage to export task data. Please grant the permission.")
                .setPositiveButton("OK", (dialog, which) -> {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performExport() {
        TaskExport taskExport = new TaskExport(task);
        Gson gson = new Gson();
        String json = gson.toJson(taskExport);

        String fileName = "task_" + task.getId() + ".json";
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDirectory, fileName);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(json.getBytes());
                fos.flush();
                showToast("Task exported to " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Failed to export task: " + e.getMessage());
        }
    }

    public static class TaskExport {
        private String title;
        private String description;
        private String startDate;
        private String stopDate;
        private int priority;
        private int status;

        public TaskExport(Task task) {
            this.title = task.getTitle();
            this.description = task.getDescription();
            this.startDate = task.getStartDate();
            this.stopDate = task.getStopDate();
            this.priority = task.getPriority();
            this.status = task.getStatus();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performExport();
            } else {
                showToast("Permission denied. Cannot export task.");
            }
        }
    }


    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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