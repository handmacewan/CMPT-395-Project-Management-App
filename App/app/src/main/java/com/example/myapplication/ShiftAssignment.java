package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.ArrayList;

public class ShiftAssignment extends AppCompatActivity {

    TextView dateLabel, mornLabel, afternoonLabel, allDayLabel;
    String date, dayOfWeek, formatDate;
    Spinner mornShift1, mornShift2, mornShift3, aftShift1, aftShift2, aftShift3, allDayShift1, allDayShift2, allDayShift3;
    ImageButton busyBtn, busyOffBtn;
    ImageView morniv, aftiv;
    ArrayList<String> weekendNames = new ArrayList<>();
    ArrayList<String> weekendNames2;
    ArrayList<String> weekendNames3;
    ArrayList<String> mornNames = new ArrayList<>();
    ArrayList<String> mornNames2;
    ArrayList<String> mornNames3;
    ArrayList<String> aftNames = new ArrayList<>();
    ArrayList<String> aftNames2;
    ArrayList<String> aftNames3;
    String weekendPrev = "";
    String weekendPrev2 = "";
    String weekendPrev3 = "";
    String mornPrev = "";
    String mornPrev2 = "";
    String mornPrev3 = "";
    String aftPrev = "";
    String aftPrev2 = "";
    String aftPrev3 = "";
    ArrayAdapter<String> weekendSpinnerArrayAdapter;
    ArrayAdapter<String> weekendSpinnerArrayAdapter2;
    ArrayAdapter<String> weekendSpinnerArrayAdapter3;
    ArrayAdapter<String> mornSpinnerArrayAdapter;
    ArrayAdapter<String> mornSpinnerArrayAdapter2;
    ArrayAdapter<String> mornSpinnerArrayAdapter3;
    ArrayAdapter<String> aftSpinnerArrayAdapter;
    ArrayAdapter<String> aftSpinnerArrayAdapter2;
    ArrayAdapter<String> aftSpinnerArrayAdapter3;
    int busy = 0;
    ArrayList<String> shiftList;
    int filledMorning = 0;
    int filledAft = 0;
    int filledWeekend = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shift_assignment);

        //set date title
        dateLabel = findViewById(R.id.dateLabel);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                date = null;
                formatDate = null;
                dayOfWeek = null;
                shiftList = null;
            } else {
                date = extras.getString("date");
                formatDate = extras.getString("formatDate");
                dayOfWeek = extras.getString("dayOfWeek");
                shiftList = extras.getStringArrayList("shifts");

            }
        } else {
            date = savedInstanceState.getString("date");
            formatDate = savedInstanceState.getString("formatDate");
            dayOfWeek = savedInstanceState.getString("dayOfWeek");
            shiftList = savedInstanceState.getStringArrayList("shifts");
        }

        dateLabel.setText(formatDate);

        //populate drop down menus
        mornShift1 = findViewById(R.id.mornShift1);
        mornShift2 = findViewById(R.id.mornShift2);
        mornShift3 = findViewById(R.id.mornShift3);
        aftShift1 = findViewById(R.id.afternoonShift1);
        aftShift2 = findViewById(R.id.afternoonShift2);
        aftShift3 = findViewById(R.id.afternoonShift3);
        allDayShift1 = findViewById(R.id.allDayShift1);
        allDayShift2 = findViewById(R.id.allDayShift2);
        allDayShift3 = findViewById(R.id.allDayShift3);
        mornLabel = findViewById(R.id.mornShiftLabel);
        afternoonLabel = findViewById(R.id.afternoonShiftLabel);
        allDayLabel = findViewById(R.id.allDayLabel);
        morniv = findViewById(R.id.morniv);
        aftiv = findViewById(R.id.aftiv);

        DBHandler db = new DBHandler(getApplicationContext());
        dayOfWeek = dayOfWeek.toLowerCase();

        ArrayList<Employees> allDayData = db.getAvailEmployee(dayOfWeek, "All day");

        //weekend all day shift only
        if (dayOfWeek.equalsIgnoreCase("Saturday") ||
                dayOfWeek.equalsIgnoreCase("Sunday")) {
            mornShift1.setVisibility(View.INVISIBLE);
            mornShift2.setVisibility(View.INVISIBLE);
            aftShift1.setVisibility(View.INVISIBLE);
            aftShift2.setVisibility(View.INVISIBLE);
            mornLabel.setVisibility(View.INVISIBLE);
            afternoonLabel.setVisibility(View.INVISIBLE);
            morniv.setVisibility(View.INVISIBLE);
            aftiv.setVisibility(View.INVISIBLE);

            allDayShift1.setVisibility(View.VISIBLE);
            allDayShift2.setVisibility(View.VISIBLE);
            allDayLabel.setVisibility(View.VISIBLE);

            weekendNames.add("");
            for (Employees e : allDayData) {
                weekendNames.add(e.getFName() + " " + e.getLName() + " - " +
                        checkQualify(e, "all day"));
            }

            weekendSpinnerArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, weekendNames);
            allDayShift1.setAdapter(weekendSpinnerArrayAdapter);

            weekendNames2 = (ArrayList<String>) weekendNames.clone();
            weekendSpinnerArrayAdapter2 = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, weekendNames2);
            allDayShift2.setAdapter(weekendSpinnerArrayAdapter2);

            weekendNames3 = (ArrayList<String>) weekendNames.clone();
            weekendSpinnerArrayAdapter3 = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, weekendNames3);
            allDayShift3.setAdapter(weekendSpinnerArrayAdapter3);

            //remove selected names
            checkSelected(allDayShift1, weekendNames2, weekendNames3, 1, 0);
            checkSelected(allDayShift2, weekendNames, weekendNames3, 2, 0);
            checkSelected(allDayShift3, weekendNames, weekendNames2, 3, 0);


        }


        //for weekdays
        else {

            //morning data
            ArrayList<Employees> mornData = db.getAvailEmployee(dayOfWeek, "Morning");
            mornNames = new ArrayList<>();
            mornNames.add("");
            for (Employees e : mornData) {
                mornNames.add(e.getFName() + " " + e.getLName() + " - " +
                        checkQualify(e, "morning"));
            }

            //afternoon data
            ArrayList<Employees> aftData = db.getAvailEmployee(dayOfWeek, "Afternoon");
            aftNames = new ArrayList<>();
            aftNames.add("");
            for (Employees e : aftData) {
                aftNames.add(e.getFName() + " " + e.getLName() + " - " +
                        checkQualify(e, "afternoon"));
            }

            //all day data
            for (Employees e : allDayData) {
                mornNames.add(e.getFName() + " " + e.getLName() + " - " +
                        checkQualify(e, "morning"));
                aftNames.add(e.getFName() + " " + e.getLName() + " - " +
                        checkQualify(e, "afternoon"));
            }

            mornNames2 = (ArrayList<String>) mornNames.clone();
            mornNames3 = (ArrayList<String>) mornNames.clone();
            aftNames2 = (ArrayList<String>) aftNames.clone();
            aftNames3 = (ArrayList<String>) aftNames.clone();

            mornSpinnerArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, mornNames);
            mornSpinnerArrayAdapter2 = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, mornNames2);
            mornSpinnerArrayAdapter3 = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, mornNames3);
            mornShift1.setAdapter(mornSpinnerArrayAdapter);
            mornShift2.setAdapter(mornSpinnerArrayAdapter2);
            mornShift3.setAdapter(mornSpinnerArrayAdapter3);

            aftSpinnerArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, aftNames);
            aftSpinnerArrayAdapter2 = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, aftNames2);
            aftSpinnerArrayAdapter3 = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, aftNames3);
            aftShift1.setAdapter(aftSpinnerArrayAdapter);
            aftShift2.setAdapter(aftSpinnerArrayAdapter2);
            aftShift3.setAdapter(aftSpinnerArrayAdapter3);

            //remove selected names
            checkSelected(mornShift1, mornNames2, mornNames3, 1, 1);
            checkSelected(mornShift2, mornNames, mornNames3, 2, 1);
            checkSelected(aftShift1, aftNames2, aftNames3, 1, 2);
            checkSelected(aftShift2, aftNames, aftNames3, 2, 2);
            checkSelected(mornShift3, mornNames, mornNames2, 3, 1);
            checkSelected(aftShift3, aftNames, aftNames2, 3, 2);

        }

        //busy day
        busyBtn = findViewById(R.id.busyBtn);
        busyOffBtn = findViewById(R.id.busyOffBtn);

        busy = db.checktypeofday(date);
        //if existing shift was busy
        if (busy == 1) {
            busyBtn.setVisibility(View.INVISIBLE);
            busyOffBtn.setVisibility(View.VISIBLE);

            if (dayOfWeek.equalsIgnoreCase("Saturday") ||
                    dayOfWeek.equalsIgnoreCase("Sunday")) {
                allDayShift3.setVisibility(View.VISIBLE);
            }

            else {
                mornShift3.setVisibility(View.VISIBLE);
                aftShift3.setVisibility(View.VISIBLE);
            }
        }

        //turn on busy day
        busyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busy = 1;
                busyBtn.setVisibility(View.INVISIBLE);
                busyOffBtn.setVisibility(View.VISIBLE);
                if (dayOfWeek.equalsIgnoreCase("Saturday") ||
                        dayOfWeek.equalsIgnoreCase("Sunday")) {
                    allDayShift3.setVisibility(View.VISIBLE);
                }

                else {
                    mornShift3.setVisibility(View.VISIBLE);
                    aftShift3.setVisibility(View.VISIBLE);
                }

                Toast.makeText(ShiftAssignment.this,
                        formatDate +
                                " set to busy day.", Toast.LENGTH_SHORT).show();
            }
        });

        //turn off busy day
        busyOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busy = 0;
                busyOffBtn.setVisibility(View.INVISIBLE);
                busyBtn.setVisibility(View.VISIBLE);

                if (dayOfWeek.equalsIgnoreCase("Saturday") ||
                        dayOfWeek.equalsIgnoreCase("Sunday")) {
                    allDayShift3.setVisibility(View.INVISIBLE);
                }

                else {
                    mornShift3.setVisibility(View.INVISIBLE);
                    aftShift3.setVisibility(View.INVISIBLE);
                }

                Toast.makeText(ShiftAssignment.this,
                        formatDate +
                                " set back to regular day.", Toast.LENGTH_SHORT).show();

            }
        });

        //display existing shifts
        for (String shift: shiftList) {
            reselectShift(shift);
        }



        //add shift
        Button assignBtn = findViewById(R.id.assignShiftBtn);
        assignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name;
                String name2;
                String name3;
                String name4;
                String name5;
                String name6;
                String[] split;
                String[] split2;
                String[] split3;
                String[] split4;
                String[] split5;
                String[] split6;
                LocalDate localDate = CalendarUtils.selectedDate;

                if (dayOfWeek.equalsIgnoreCase("Saturday") ||
                        dayOfWeek.equalsIgnoreCase("Sunday")) {

                    //check trained
                    if (busy == 1) {
                        if (checkWeekendTrained(allDayShift1.getSelectedItem().toString(),
                                allDayShift2.getSelectedItem().toString(),
                                allDayShift3.getSelectedItem().toString(), 1) == 0) {
                            errorMessage(0);
                            return;

                        }
                    }
                    else {
                        if (checkWeekendTrained(allDayShift1.getSelectedItem().toString(),
                                allDayShift2.getSelectedItem().toString(),
                                allDayShift3.getSelectedItem().toString(), 0) == 0) {
                            errorMessage(0);
                            return;

                        }
                    }


                    name = allDayShift1.getSelectedItem().toString();
                    name2 = allDayShift2.getSelectedItem().toString();
                    name3 = allDayShift3.getSelectedItem().toString();
                    db.deleteShift(localDate);

                    if (! name.equalsIgnoreCase("")) {
                        split = name.split(" ");
                        db.shiftAssignment(split[0], split[1], "Allday", busy);
                    }
                    if (! name2.equalsIgnoreCase("")) {
                        split2 = name2.split(" ");
                        db.shiftAssignment(split2[0], split2[1], "Allday", busy);
                    }

                    if (busy == 1) {
                        if (! name3.equalsIgnoreCase("")) {
                            split3 = name3.split(" ");
                            db.shiftAssignment(split3[0], split3[1], "Allday", busy);
                        }

                    }

                    finish();
                }
                else if (dayOfWeek.equalsIgnoreCase("Monday") || dayOfWeek.equalsIgnoreCase("Tuesday") ||
                        dayOfWeek.equalsIgnoreCase("Wednesday") || dayOfWeek.equalsIgnoreCase("Thursday") ||
                        dayOfWeek.equalsIgnoreCase("Friday")) {

                    //check trained
                    if (busy == 1) {
                        if (checkTrained(mornShift1.getSelectedItem().toString(),
                                mornShift2.getSelectedItem().toString(),
                                mornShift3.getSelectedItem().toString(), 1, 1) == 0) {
                            errorMessage(1);
                            return;
                        }
                        else if (checkTrained(aftShift1.getSelectedItem().toString(),
                                aftShift2.getSelectedItem().toString(),
                                aftShift3.getSelectedItem().toString(), 2, 1) == 0) {
                            errorMessage(2);
                            return;
                        }
                    }
                    else {
                        if (checkTrained(mornShift1.getSelectedItem().toString(),
                                mornShift2.getSelectedItem().toString(),
                                mornShift3.getSelectedItem().toString(), 1, 0) == 0) {
                            errorMessage(1);
                            return;
                        }
                        else if (checkTrained(aftShift1.getSelectedItem().toString(),
                                aftShift2.getSelectedItem().toString(),
                                aftShift3.getSelectedItem().toString(), 2, 0) == 0) {
                            errorMessage(2);
                            return;
                        }
                    }

                    name = mornShift1.getSelectedItem().toString();
                    name2 = mornShift2.getSelectedItem().toString();
                    name3 = aftShift1.getSelectedItem().toString();
                    name4 = aftShift2.getSelectedItem().toString();

                    name5 = mornShift3.getSelectedItem().toString();
                    name6 = aftShift3.getSelectedItem().toString();
                    db.deleteShift(localDate);

                    //morning
                    split = name.split(" ");
                    split2 = name2.split(" ");
                    //afternoon
                    split3 = name3.split(" ");
                    split4 = name4.split(" ");

                    split5 = name5.split(" "); //morning busy
                    split6 = name6.split(" "); //afternoon busy

                    //morning
                    if (!name.equalsIgnoreCase("")) {
                        db.shiftAssignment(split[0], split[1], "Opening", busy);

                    }
                    if (!name2.equalsIgnoreCase("")) {
                        db.shiftAssignment(split2[0], split2[1], "Opening", busy);

                    }
                    if (busy == 1) {
                        if (!name5.equalsIgnoreCase("")) {
                            db.shiftAssignment(split5[0], split5[1], "Opening", busy);

                        }
                    }

                    //afternoon
                    if (!name3.equalsIgnoreCase("")) {
                        db.shiftAssignment(split3[0], split3[1], "Closing", busy);

                    }
                    if (!name4.equalsIgnoreCase("")) {
                        db.shiftAssignment(split4[0], split4[1], "Closing", busy);

                    }
                    if (busy == 1) {
                        if (!name6.equalsIgnoreCase("")) {
                            db.shiftAssignment(split6[0], split6[1], "Closing", busy);

                        }
                    }


                    finish();

                }
                db.close();

            }

        });

    }


    private String checkQualify(Employees aEmployee, String time) {
        if (time.equalsIgnoreCase("morning")) {
            if (aEmployee.getOpen().equalsIgnoreCase("Trained") &&
                    aEmployee.getClose().equalsIgnoreCase("Trained")) {
                return "Trained Opener & Closer";
            }
            else if (aEmployee.getOpen().equalsIgnoreCase("Trained")) {
                return "Trained Opener";
            }
            else if (aEmployee.getClose().equalsIgnoreCase("Trained")) {
                return "Trained Closer";
            }
            else {
                return "Not Trained";
            }
        }

        else if (time.equalsIgnoreCase("afternoon")){
            if (aEmployee.getOpen().equalsIgnoreCase("Trained") &&
                    aEmployee.getClose().equalsIgnoreCase("Trained")) {
                return "Trained Opener & Closer";
            }
            else if (aEmployee.getOpen().equalsIgnoreCase("Trained")) {
                return "Trained Opener";
            }
            else if (aEmployee.getClose().equalsIgnoreCase("Trained")) {
                return "Trained Closer";
            }

            else {
                return "Not Trained";
            }
        }

        else {
            if (aEmployee.getOpen().equalsIgnoreCase("Trained") &&
                    aEmployee.getClose().equalsIgnoreCase("Trained")) {
                return "Trained Opener & Closer";
            }

            else if (aEmployee.getOpen().equalsIgnoreCase("Trained")) {
                return "Trained Opener";
            }

            else if (aEmployee.getClose().equalsIgnoreCase("Trained")) {
                return "Trained Closer";
            }

            else {
                return "Not Trained";
            }
        }

    }

    private void checkSelected(Spinner shift, ArrayList<String> names2, ArrayList<String> names3,
                               int prev, int time) {

        shift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                String selectedName = shift.getSelectedItem().toString();

                if (!shift.getSelectedItem().toString().equalsIgnoreCase("")) {
                    names2.remove(selectedName);
                    names3.remove(selectedName);
                }

                //all day
                if (time == 0) {
                    if (prev == 1 && ! weekendPrev.equalsIgnoreCase("") && !
                            weekendPrev.equalsIgnoreCase(shift.getSelectedItem().toString())) {
                        names2.add(weekendPrev);
                        names3.add(weekendPrev);

                        weekendSpinnerArrayAdapter3.notifyDataSetChanged();
                        weekendSpinnerArrayAdapter2.notifyDataSetChanged();

                    }
                    else if (prev == 2 && ! weekendPrev2.equalsIgnoreCase("") && !
                            weekendPrev2.equalsIgnoreCase(shift.getSelectedItem().toString())) {
                        names2.add(weekendPrev2);
                        names3.add(weekendPrev2);

                        weekendSpinnerArrayAdapter.notifyDataSetChanged();
                        weekendSpinnerArrayAdapter3.notifyDataSetChanged();
                    }
                    else if (prev == 3 && ! weekendPrev3.equalsIgnoreCase("") && !
                            weekendPrev3.equalsIgnoreCase(shift.getSelectedItem().toString())) {
                        names2.add(weekendPrev3);
                        names3.add(weekendPrev3);

                        weekendSpinnerArrayAdapter.notifyDataSetChanged();
                        weekendSpinnerArrayAdapter2.notifyDataSetChanged();

                    }

                    if (prev == 1) {
                        weekendSpinnerArrayAdapter3.notifyDataSetChanged();
                        weekendSpinnerArrayAdapter2.notifyDataSetChanged();

                        updatePosition(allDayShift3, weekendPrev3, weekendSpinnerArrayAdapter3);
                        updatePosition(allDayShift2, weekendPrev2, weekendSpinnerArrayAdapter2);

                        weekendPrev = shift.getSelectedItem().toString();
                    }
                    else if (prev == 2) {
                        weekendSpinnerArrayAdapter.notifyDataSetChanged();
                        weekendSpinnerArrayAdapter3.notifyDataSetChanged();

                        updatePosition(allDayShift1, weekendPrev, weekendSpinnerArrayAdapter);
                        updatePosition(allDayShift3, weekendPrev3, weekendSpinnerArrayAdapter3);

                        weekendPrev2 = shift.getSelectedItem().toString();
                    }
                    else if (prev == 3) {
                        weekendSpinnerArrayAdapter.notifyDataSetChanged();
                        weekendSpinnerArrayAdapter2.notifyDataSetChanged();

                        updatePosition(allDayShift1, weekendPrev, weekendSpinnerArrayAdapter);
                        updatePosition(allDayShift2, weekendPrev2, weekendSpinnerArrayAdapter2);

                        weekendPrev3 = shift.getSelectedItem().toString();
                    }

                }

                //morning
                else if (time == 1) {

                    if (prev == 1 && ! mornPrev.equalsIgnoreCase("") && !
                            mornPrev.equalsIgnoreCase(shift.getSelectedItem().toString())) {

                        names2.add(mornPrev);
                        names3.add(mornPrev);

                        mornSpinnerArrayAdapter3.notifyDataSetChanged();
                        mornSpinnerArrayAdapter2.notifyDataSetChanged();

                    }
                    else if (prev == 2 && ! mornPrev2.equalsIgnoreCase("") && !
                            mornPrev2.equalsIgnoreCase(shift.getSelectedItem().toString())) {

                        names2.add(mornPrev2);
                        names3.add(mornPrev2);

                        mornSpinnerArrayAdapter.notifyDataSetChanged();
                        mornSpinnerArrayAdapter3.notifyDataSetChanged();

                    }
                    else if (prev == 3 && ! mornPrev3.equalsIgnoreCase("") && !
                            mornPrev3.equalsIgnoreCase(shift.getSelectedItem().toString())) {

                        names2.add(mornPrev3);
                        names3.add(mornPrev3);

                        mornSpinnerArrayAdapter.notifyDataSetChanged();
                        mornSpinnerArrayAdapter2.notifyDataSetChanged();

                    }

                    if (prev == 1) {
                        mornSpinnerArrayAdapter3.notifyDataSetChanged();
                        mornSpinnerArrayAdapter2.notifyDataSetChanged();

                        updatePosition(mornShift3, mornPrev3, mornSpinnerArrayAdapter3);
                        updatePosition(mornShift2, mornPrev2, mornSpinnerArrayAdapter2);

                        mornPrev = shift.getSelectedItem().toString();
                    }
                    else if (prev == 2) {

                        mornSpinnerArrayAdapter.notifyDataSetChanged();
                        mornSpinnerArrayAdapter3.notifyDataSetChanged();

                        updatePosition(mornShift1, mornPrev, mornSpinnerArrayAdapter);
                        updatePosition(mornShift3, mornPrev3, mornSpinnerArrayAdapter3);

                        mornPrev2 = shift.getSelectedItem().toString();
                    }
                    else if (prev == 3) {
                        mornSpinnerArrayAdapter.notifyDataSetChanged();
                        mornSpinnerArrayAdapter2.notifyDataSetChanged();

                        updatePosition(mornShift1, mornPrev, mornSpinnerArrayAdapter);
                        updatePosition(mornShift2, mornPrev2, mornSpinnerArrayAdapter2);

                        mornPrev3 = shift.getSelectedItem().toString();
                    }

                }

                //afternoon
                else if (time == 2) {
                    if (prev == 1 && ! aftPrev.equalsIgnoreCase("") && !
                            aftPrev.equalsIgnoreCase(shift.getSelectedItem().toString())) {

                        names2.add(aftPrev);
                        names3.add(aftPrev);

                        aftSpinnerArrayAdapter3.notifyDataSetChanged();
                        aftSpinnerArrayAdapter2.notifyDataSetChanged();

                    }
                    else if (prev == 2 && ! aftPrev2.equalsIgnoreCase("") && !
                            aftPrev2.equalsIgnoreCase(shift.getSelectedItem().toString())) {

                        names2.add(aftPrev2);
                        names3.add(aftPrev2);

                        aftSpinnerArrayAdapter.notifyDataSetChanged();
                        aftSpinnerArrayAdapter3.notifyDataSetChanged();

                    }
                    else if (prev == 3 && ! aftPrev3.equalsIgnoreCase("") && !
                            aftPrev3.equalsIgnoreCase(shift.getSelectedItem().toString())) {

                        names2.add(aftPrev3);
                        names3.add(aftPrev3);

                        aftSpinnerArrayAdapter.notifyDataSetChanged();
                        aftSpinnerArrayAdapter2.notifyDataSetChanged();

                    }

                    if (prev == 1) {
                        aftSpinnerArrayAdapter3.notifyDataSetChanged();
                        aftSpinnerArrayAdapter2.notifyDataSetChanged();

                        updatePosition(aftShift2, aftPrev2, aftSpinnerArrayAdapter2);
                        updatePosition(aftShift3, aftPrev3, aftSpinnerArrayAdapter3);

                        aftPrev = shift.getSelectedItem().toString();
                    }
                    else if (prev == 2) {
                        aftSpinnerArrayAdapter3.notifyDataSetChanged();
                        aftSpinnerArrayAdapter.notifyDataSetChanged();

                        updatePosition(aftShift3, aftPrev3, aftSpinnerArrayAdapter3);
                        updatePosition(aftShift1, aftPrev, aftSpinnerArrayAdapter);

                        aftPrev2 = shift.getSelectedItem().toString();
                    }
                    else if (prev == 3) {
                        aftSpinnerArrayAdapter.notifyDataSetChanged();
                        aftSpinnerArrayAdapter2.notifyDataSetChanged();

                        updatePosition(aftShift1, aftPrev, aftSpinnerArrayAdapter);
                        updatePosition(aftShift2, aftPrev2, aftSpinnerArrayAdapter2);

                        aftPrev3 = shift.getSelectedItem().toString();
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }



    private void updatePosition(Spinner shift, String prev, ArrayAdapter spinnerArrayAdapter) {
        //if selection out of bounds
        if (shift.getSelectedItemPosition() >= spinnerArrayAdapter.getCount()) {
            int pos = spinnerArrayAdapter.getPosition(prev);
            shift.setSelection(pos);

        }

        else if (! shift.getSelectedItem().toString().equalsIgnoreCase(prev)) {
            int pos = spinnerArrayAdapter.getPosition(prev);
            shift.setSelection(pos);
        }

    }

    private int checkWeekendTrained(String shift1, String shift2, String shift3, int busy) {
        //ignore blank selection
        if (shift1.equalsIgnoreCase("") || shift2.equalsIgnoreCase("")) {
            return 1;
        }

        if (busy == 1) {
            if (shift3.equalsIgnoreCase("")) {
                return 1;
            }
        }

        if (shift1.contains("Trained Opener & Closer") || shift2.contains("Trained Opener & Closer")
                || shift3.contains("Trained Opener & Closer")) {
            return 1;
        }

        else if (shift1.contains("Trained Opener")) {
            if (shift2.contains("Trained Closer") || shift3.contains("Trained Closer")) {
                return 1;
            }
        }

        else if (shift2.contains("Trained Opener")) {
            if (shift1.contains("Trained Closer") || shift3.contains("Trained Closer")) {
                return 1;
            }
        }

        else if (shift3.contains("Trained Opener")) {
            if (shift1.contains("Trained Closer") || shift2.contains("Trained Closer")) {
                return 1;
            }
        }

        return 0;
    }

    private int checkTrained(String shift1, String shift2, String shift3, int time, int busy) {
        //check for partial assignment
        if (shift1.equalsIgnoreCase("") || shift2.equalsIgnoreCase("")) {
            return 1;
        }

        //check for third shift if busy day
        if (busy == 1) {
            if (shift3.equalsIgnoreCase("")) {
                return 1;
            }
        }


        //morning
        if (time == 1) {
            if (shift1.contains("Trained Opener") || shift2.contains("Trained Opener") ||
                    shift3.contains("Trained Opener")) {
                return 1;
            }
        }

        //afternoon
        else if (time == 2) {
            if (shift1.contains("Trained Closer") || shift2.contains("Trained Closer") ||
                    shift3.contains("Trained Closer")) {
                return 1;
            }
        }

        return 0;
    }

    private void errorMessage(int time) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ShiftAssignment.this);
        alert.setTitle("Require Trained Employee");

        if (time == 0) {
            alert.setMessage("Please select at least one trained opener and one trained closer for shift.");
        }

        else if (time == 1) {
            alert.setMessage("Please select at least one trained employee for morning shift.");
        }

        else {
            alert.setMessage("Please select at least one trained employee for afternoon shift.");
        }

        alert.setCancelable(true);
        alert.setPositiveButton("Return", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void reselectShift(String shift) {
        String[] parse1 = shift.split(":");
        String time = parse1[0];
        String employee = parse1[1].trim();

        if (time.equalsIgnoreCase("Morning")) {
            if (filledMorning == 0) {
                mornShift1.setSelection(mornNames.indexOf(employee), true);
            } else if (filledMorning == 1) {
                mornPrev2 = employee;
            } else {
                mornPrev3 = employee;
            }

            filledMorning++;
        }

        else if (time.equalsIgnoreCase("Afternoon")) {
            if (filledAft == 0) {
                aftShift1.setSelection(aftNames.indexOf(employee), true);
            } else if (filledAft == 1) {
                aftPrev2 = employee;
            } else  {
                aftPrev3 = employee;
            }

            filledAft++;
        }

        else if (time.equalsIgnoreCase("All Day")) {
            if (filledWeekend == 0) {
                allDayShift1.setSelection(weekendNames.indexOf(employee), true);
            } else if (filledWeekend == 1) {
                weekendPrev2 = employee;
            } else {
                weekendPrev3 = employee;
            }

            filledWeekend++;
        }

    }

}
