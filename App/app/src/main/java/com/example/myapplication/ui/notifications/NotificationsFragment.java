package com.example.myapplication.ui.notifications;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CalendarUtils;
import com.example.myapplication.DBHandler;
import com.example.myapplication.Employees;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentNotificationsBinding;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    ArrayAdapter arrayAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        populateList();

        ListView notiList = binding.notificationList;
        notiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int n, long l) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Ignore?");
                alert.setMessage("Are you sure you want to ignore this notification?");
                alert.setCancelable(true);
                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        arrayAdapter.remove(arrayAdapter.getItem(n));
                        arrayAdapter.notifyDataSetChanged();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
            }
        });
        binding.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Restore?");
                builder.setMessage("Are you sure you want to restore all notification?");
                builder.setCancelable(true);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        populateList();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList();
    }



    public void populateList(){
        DBHandler db = new DBHandler(getContext());
        ArrayList<String> emp = db.readNames();
        List<String> shiftList = db.readShiftList();
        List<LocalDate> monthList = new ArrayList<>();
        ArrayList<String> noShift = new ArrayList<>();
        ListView noti = binding.notificationList;

        LocalDate today = CalendarUtils.selectedDate;
        //LocalDate today = LocalDate.now();
        Month month = today.getMonth();
        int year = today.getYear();

        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

        LocalDate lastSundayOfPrevMonth = firstDayOfMonth.minusDays(1).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        LocalDate sunday = lastSundayOfPrevMonth;
        LocalDate saturday = firstDayOfMonth.with(TemporalAdjusters.previous(DayOfWeek.SATURDAY));

        while (sunday.isBefore(lastDayOfMonth)) {
            for (int i = 0; i < 7; i++) {
                LocalDate date = sunday.plusDays(i);
                if (date.isBefore(lastSundayOfPrevMonth)) {
                    // print the date from the previous month
                    System.out.println(date);
                } else if (date.isAfter(lastDayOfMonth)) {
                    break;
                } else {
                    // print the date from the current month
                    monthList.add(date);
                }
                if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    saturday = date;
                }
            }
            if (sunday.plusDays(6).isBefore(lastDayOfMonth)) {
                sunday = sunday.plusWeeks(1);
            } else {
                break;
            }
        }

        List<LocalDate> newList = new ArrayList<>();
        for (LocalDate date : monthList) {
            newList.add(date);
            if (date.isEqual(saturday)) {
                break;
            }
        }



        // Parse the date from each entry and add it to a list
        List<LocalDate> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (String entry : shiftList) {
            String[] fields = entry.trim().split("\\s+");
            LocalDate date = LocalDate.parse(fields[2], formatter);
            dates.add(date);
        }

        // Print out the list of dates


        // Iterate over each chunk of 7 days in the days list
        for (int i = 0; i < monthList.size()-6 ; i += 7) {
            List<LocalDate> weekDays = monthList.subList(i, i + 7);

            // Iterate over each unique employee
            for (String employee : emp) {
                boolean hasShift = false;

                // Iterate over each shift
                for (String shift : shiftList) {
                    String[] shiftParts = shift.split(" ");
                    LocalDate shiftDate = LocalDate.parse(shiftParts[2]);
                    String shiftEmployee = shiftParts[0] + " " + shiftParts[1];

                    // Check if the shift is for the current employee and within the current week
                    if (employee.equals(shiftEmployee) && weekDays.contains(shiftDate)) {
                        hasShift = true;
                        break;
                    }
                }
                // If the employee doesn't have a shift for this week, print a message
                if (!hasShift) {
                    noShift.add(employee +" - Missing shift: "+ weekDays.get(0) +" to "+ weekDays.get(6));
                }
            }
        }
        arrayAdapter = new ArrayAdapter(noti.getContext(), R.layout.notification_list, noShift);
        noti.setAdapter(arrayAdapter);
        db.close();

        //end
    }
}