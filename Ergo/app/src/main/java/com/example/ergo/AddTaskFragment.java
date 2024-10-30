package com.example.ergo;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ergo.model.Task;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TaskAPI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTaskFragment extends Fragment {

    private EditText titleEditText, startDateEditText, endDateEditText, descriptionEditText;
    private Button saveTaskButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_task_fragment, container, false);
        titleEditText = view.findViewById(R.id.TitleET);
        startDateEditText = view.findViewById(R.id.StartDateET);
        endDateEditText = view.findViewById(R.id.EndDateET);
        descriptionEditText = view.findViewById(R.id.editTextTextMultiLine);
        saveTaskButton = view.findViewById(R.id.SaveTaskButton);


        saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSavingTask();
            }
        });

        return view;
    }

    private void performSavingTask(){
        final String title = titleEditText.getText().toString().trim();
        final String startDate = startDateEditText.getText().toString().trim();
        final String endDate = endDateEditText.getText().toString().trim();
        final String description = descriptionEditText.getText().toString().trim();
        RetrofitService retrofitService = new RetrofitService();
        TaskAPI taskAPI = retrofitService.getRetrofit().create(TaskAPI.class);

        // Check for empty fields
        if(title.isEmpty()|| startDate.isEmpty()||endDate.isEmpty()||description.isEmpty()){
            makeText(getActivity(), "Please fill in all fields", LENGTH_LONG).show();
        }

        saveTaskButton.setOnClickListener(v -> {
            Task task = new Task();
            task.setTitle(title);
            task.setStartDate(parseDate(startDate));
            task.setStopDate(parseDate(endDate));
            task.setDescription(description);
            taskAPI.saveTask(task)

        });


    }

    private Date parseDate(String date){
        // Define the date format that matches the input (e.g., "dd/MM/yyyy" or "yyyy-MM-dd")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);  // Optional: makes parsing strict

        try {
            // Parse the date from the EditText
            return dateFormat.parse(startDateEditText.getText().toString().trim());


        } catch (ParseException e) {
            // Handle invalid date format with a message
            Toast.makeText(getActivity(), "Invalid date format. Please use yyyy-MM-dd.", Toast.LENGTH_LONG).show();
        }
        return null;
    }


}
