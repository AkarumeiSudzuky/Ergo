package com.example.ergo;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.TimeZone;

public class AddNewGroupFragment extends Fragment {
    private Button StartDateButton;
    private Button EndDateButton;
    private Button SaveGroupButton;
    private DatePickerDialog datePickerDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_group_fragment, container, false);
        StartDateButton = view.findViewById(R.id.StartDateButton);
        StartDateButton.setText(getTodayDate());
        EndDateButton = view.findViewById(R.id.EndDateButton);
        EndDateButton.setText(getTodayDate());
        SaveGroupButton = view.findViewById(R.id.SaveGroupButton);
        initDatePicker();

        StartDateButton.setOnClickListener(v -> openDatePicker(view));
        EndDateButton.setOnClickListener(v -> openDatePicker(view));

        return view;
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(currentDay, currentMonth, currentYear);
    }

    private void initDatePicker() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(requireActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date = makeDateString(dayOfMonth, month, year);
                        StartDateButton.setText(date);
                    }
                }, currentYear, currentMonth, currentDay);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }


    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + ", " + year;
    }

    private String getMonthFormat(int month) {
        String[] List = {"January", "February", "March", "April",
                "May", "June", "July", "August", "September", "October",
                "November", "December"};
            return List[month - 1];
        }

    }


