package com.example.myapplication;

import static com.example.myapplication.CalendarUtils.daysInWeekArray;
import static com.example.myapplication.CalendarUtils.monthYearFromDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.CalendarAdapter;
import com.example.myapplication.export.ExportMonthlySchedule;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Weekly extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Button assignShiftBtn;
    private ArrayList<String> arrayList;

    CalendarAdapter calendarAdapter;


    ArrayAdapter arrayAdapter;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);
        initWidgets();
        ArrayList<String> tmp = getList();
        arrayAdapter.notifyDataSetChanged();
        setWeekView();

        //assign shift button action
        assignShiftBtn = findViewById(R.id.assign_shift);


        assignShiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalDate selectedDate = LocalDate.parse(CalendarUtils.selectedDate.toString());
                LocalDate currentDate = LocalDate.now();

                if(selectedDate.isBefore(currentDate)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Weekly.this);
                    builder.setTitle("Editing past dates");
                    builder.setMessage("Are you sure you want to edit a past date?");

                    builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getApplicationContext(), ShiftAssignment.class);
                            i.putExtra("date", CalendarUtils.selectedDate.toString());
                            i.putExtra("formatDate", CalendarUtils.formattedDate(CalendarUtils.selectedDate));
                            i.putExtra("dayOfWeek", CalendarUtils.selectedDate.getDayOfWeek().toString());

                            //pass existing shifts
                            ArrayList<String> shifts = getList();
                            i.putExtra("shifts", shifts);

                            startActivity(i);
                        }
                    });
                    builder.setNegativeButton("Cancel", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {

                    Intent i = new Intent(getApplicationContext(), ShiftAssignment.class);
                    i.putExtra("date", CalendarUtils.selectedDate.toString());
                    i.putExtra("formatDate", CalendarUtils.formattedDate(CalendarUtils.selectedDate));
                    i.putExtra("dayOfWeek", CalendarUtils.selectedDate.getDayOfWeek().toString());

                    //pass existing shifts
                    ArrayList<String> shifts = getList();
                    i.putExtra("shifts", shifts);

                    startActivity(i);
                }
            }
        });
        Button delete = findViewById(R.id.delete_shift);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalDate selectedDate = LocalDate.parse(CalendarUtils.selectedDate.toString());
                LocalDate currentDate = LocalDate.now();

                if(selectedDate.isBefore(currentDate)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Weekly.this);
                    builder.setTitle("Deleting past dates");
                    builder.setMessage("Are you sure you want to delete a past date?");

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBHandler db = new DBHandler(getApplicationContext());
                            LocalDate date = CalendarUtils.selectedDate;
                            db.deleteShift(date);
                            db.close();
                            onResume();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    DBHandler db = new DBHandler(getApplicationContext());
                    LocalDate date = CalendarUtils.selectedDate;
                    db.deleteShift(date);
                    db.close();
                    onResume();
                }
            }

        });

    }

    private void initWidgets(){
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setWeekView(){
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    //From button pressed.. Will cycle -1 to selectedDate to show previous month.
    public void previousWeekAction(View view){
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }
    //From button pressed.. Will cycle +1 to selectedDate to show next week.
    public void nextWeekAction(View view){
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        getList();
        setWeekView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ArrayList<String> tmp = getList();
        arrayAdapter.notifyDataSetChanged();
    }


    public ArrayList<String> getList(){
        DBHandler db = new DBHandler(getApplicationContext());
        ListView assignment = findViewById(R.id.eventListView);
        ArrayList<String> filter = new ArrayList<>();
        ArrayList<String> openClose1 = new ArrayList<>();

        LocalDate date = CalendarUtils.selectedDate;

        arrayList = db.readShiftList();
        for (String entry: arrayList) {
            if (entry.contains(date.toString()) && date.toString().equals(date.toString())) {

                String[] shiftData = entry.split(" ");
                String name = shiftData[0] + " " + shiftData[1];
                String position = "";
                openClose1.add(shiftData[3]);
                String openClose = shiftData[3];
                String qual = " ";
                if (openClose.equals("Opening")) {
                    if (shiftData[4].equalsIgnoreCase("Trained")) qual = "Trained Opener";
                    else qual = "Not Trained";
                } else if (openClose.equals("Closing")) {
                    if (shiftData[5].equalsIgnoreCase("Trained")) qual = "Trained Closer";
                    else qual = "Not Trained";
                } else if (openClose.equals("Allday")) {
                    if (shiftData[4].equalsIgnoreCase("Trained") &&
                            shiftData[5].equalsIgnoreCase("Trained")) {
                        qual = "Trained Opener & Closer";
                    } else if (shiftData[4].equalsIgnoreCase("Trained") &&
                            shiftData[5].equalsIgnoreCase("Training")) {
                        qual = "Trained Opener";
                    } else if (shiftData[4].equalsIgnoreCase("Training") &&
                            shiftData[5].equalsIgnoreCase("Trained")) {
                        qual = "Trained Closer";
                    } else {
                        qual = "Not Trained";
                    }
                    openClose = "All day";
                }
                if (openClose.equalsIgnoreCase ("Opening")) position = "Morning";
                if (openClose.equalsIgnoreCase("Closing")) position = "Afternoon";
                if (openClose.equalsIgnoreCase("All day")) position = "All Day";

                String filterShift = position + ": " + name + " - " + qual;
                filter.add(filterShift);
            }
            db.close();
        }

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, filter);
        assignment.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        return filter;
    }


}