package com.example.myapplication.ui.home;

import static com.example.myapplication.CalendarUtils.daysInMonthArray;
import static com.example.myapplication.CalendarUtils.monthYearFromDate;
import static java.time.LocalDate.now;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.CalendarAdapter;
import com.example.myapplication.Adapter.EmployeeAdapter;
import com.example.myapplication.CalendarUtils;
import com.example.myapplication.ViewEmployee;
import com.example.myapplication.Weekly;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.export.ExportMonthlySchedule;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private FragmentHomeBinding binding;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    CalendarAdapter calendarAdapter;
    private LocalDate selectedDate;
    private Context context;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);



        View root = binding.getRoot();
        initWidgets();
        CalendarUtils.selectedDate = checkDate();
        setMonthView();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonthAction(v);
            }
        });
        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthAction(v);
            }
        });

        binding.weekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { weeklyAction(v);}
        });
        //Export button.. grabs the current month and year to pass into export
        binding.export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Export Confirmation");
                builder.setMessage("Are you sure you want to export the monthly schedule?");

                //Add check box for Qualifications
                final CheckBox checkBox = new CheckBox(getActivity());
                checkBox.setText("Include training?");
                //checkBox.setPadding(100,0,0,0);
                builder.setView(checkBox);

                builder.setPositiveButton("Export", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int month = CalendarUtils.selectedDate.getMonthValue();
                        int year = CalendarUtils.selectedDate.getYear();

                        boolean includeTraining = checkBox.isChecked();

                        ExportMonthlySchedule exportMonthlySchedule = new ExportMonthlySchedule(new Handler(Looper.getMainLooper()));
                        exportMonthlySchedule.exportCalendar(month, year, getActivity().getApplicationContext(), includeTraining);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return root;
    }
    private void initWidgets()
    {
        calendarRecyclerView = binding.calendarRecyclerView;
        monthYearText = binding.monthYearTV;
    }
    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);

        calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    //From button pressed.. Will cycle -1 to selectedDate to show previous month.
    public void previousMonthAction(View view){
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();

    }
    //From button pressed.. Will cycle +1 to selectedDate to show next month.
    public void nextMonthAction(View view){
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();

    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if(date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }
    public LocalDate checkDate() {
        LocalDate date = CalendarUtils.selectedDate;
        if (date == null || date.isBefore(LocalDate.now())) {
            date = LocalDate.now();
        }
        return date;
    }


    //Weekly button pressed will go to activity Weekly.java for weekly views.
    public void weeklyAction(View view){
        Intent i = new Intent(getActivity(), Weekly.class);
        startActivity(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        setMonthView();
    }


}