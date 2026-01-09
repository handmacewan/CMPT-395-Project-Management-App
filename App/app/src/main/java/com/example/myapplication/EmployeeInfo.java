package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Adapter.EmployeeAdapter;
import com.example.myapplication.ui.notifications.NotificationsFragment;

import java.util.ArrayList;

public class  EmployeeInfo extends AppCompatActivity {

    //Initialize variables from inputs (names, buttons, and checkboxes)
    EditText editTextFName, editTextLName, editTextphone, editTextemail;
    Button savebutton;
    CheckBox Sun, MonMorn, MonAft, TueMorn, TueAft, WedMorn, WedAft,
            ThuMorn, ThuAft, FriMorn, FriAft, Sat, Opener, Closer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_employee_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        savebutton = findViewById(R.id.buttonsave);
        AlertDialog.Builder alert = new AlertDialog.Builder(EmployeeInfo.this);

        //Finding value from checkbox and storing into variables
        Sun = findViewById(R.id.cbSun);
        MonMorn = findViewById(R.id.cbMonMorn);
        MonAft = findViewById(R.id.cbMonAft);
        TueMorn = findViewById(R.id.cbTueMorn);
        TueAft = findViewById(R.id.cbTueAft);
        WedMorn = findViewById(R.id.cbWedMorn);
        WedAft = findViewById(R.id.cbWedAft);
        ThuMorn = findViewById(R.id.cbThuMorn);
        ThuAft = findViewById(R.id.cbThuAft);
        FriMorn = findViewById(R.id.cbFriMorn);
        FriAft = findViewById(R.id.cbFriAft);
        Sat = findViewById(R.id.cbSat);
        Opener = findViewById(R.id.cbOpener);
        Closer = findViewById(R.id.cbCloser);

        //Stores text from 'Name' and 'Additional Notes' into respective variables
        editTextFName = findViewById(R.id.firstname);
        editTextLName = findViewById(R.id.lastname);
        editTextphone = findViewById(R.id.textphone);
        editTextemail = findViewById(R.id.textemail);

        //Add new instance of database
        DBHandler db = new DBHandler(EmployeeInfo.this);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Gets the value from checkboxes as Boolean
                boolean checkSun = Sun.isChecked(), checkMonMorn = MonMorn.isChecked(),
                        checkMonAft = MonAft.isChecked(), checkTueMorn = TueMorn.isChecked(),
                        checkTueAft = TueAft.isChecked(), checkWedMorn = WedMorn.isChecked(),
                        checkWedAft = WedAft.isChecked(), checkThuMorn = ThuMorn.isChecked(),
                        checkThuAft = ThuAft.isChecked(), checkFriMorn = FriMorn.isChecked(),
                        checkFriAft = FriAft.isChecked(), checkSat = Sat.isChecked();

                //create sat/sun strings for availability
                String sat, sun;

                if (checkSun) {
                     sun = "All day";
                }else{
                     sun = "NULL";
                }
                if (checkSat) {
                    sat = "All day";
                }else{
                    sat = "NULL";
                }

                //Create string holders for days and open/close qualification
                String mon, tues, wed, thur, fri;
                String open, close;

                //check availability for each day
                mon = checkAvailability(checkMonMorn, checkMonAft);
                tues = checkAvailability(checkTueMorn, checkTueAft);
                wed = checkAvailability(checkWedMorn, checkWedAft);
                thur = checkAvailability(checkThuMorn, checkThuAft);
                fri = checkAvailability(checkFriMorn, checkFriAft);

                //Single condition check statement to create string values of Opener or Closer to Database
                open = ((Opener.isChecked()) ? "Trained" : "Training");
                close = ((Closer.isChecked()) ? "Trained" : "Training");

                //Take text fields and convert to strings
                String FName = editTextFName.getText().toString();
                String LName = editTextLName.getText().toString();
                String email = editTextemail.getText().toString();
                String phone = editTextphone.getText().toString();


                //If no name is entered do not add to the database
                //displays error message prompting confirmation
                //If first, last, and email is empty
                if (FName.isEmpty() && LName.isEmpty() && email.isEmpty()) {
                    alert.setMessage("Please fill in the required (*).");
                    alert.setTitle("Name and email is required");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Return", (DialogInterface.OnClickListener)
                            (dialog, which) -> dialog.cancel());
                    alert.setPositiveButton("Exit", (DialogInterface.OnClickListener)
                            (dialog, which) -> finish());
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                //if first and last name is empty
                else if (FName.isEmpty() && LName.isEmpty()) {
                    alert.setMessage("Please fill in a first and last name.");
                    alert.setTitle("First and last is required");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Return", (DialogInterface.OnClickListener)
                            (dialog, which) -> dialog.cancel());
                    alert.setPositiveButton("Exit", (DialogInterface.OnClickListener)
                            (dialog, which) -> finish());
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                //if first and email is empty
                else if (FName.isEmpty() && email.isEmpty()) {
                    alert.setMessage("Please fill in a first name and email.");
                    alert.setTitle("First name and email is required");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Return", (DialogInterface.OnClickListener)
                            (dialog, which) -> dialog.cancel());
                    alert.setPositiveButton("Exit", (DialogInterface.OnClickListener)
                            (dialog, which) -> finish());
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                //if last and email is empty
                else if (LName.isEmpty() && email.isEmpty()) {
                    alert.setMessage("Please fill in a last name and email.");
                    alert.setTitle("Last name and email is required");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Return", (DialogInterface.OnClickListener)
                            (dialog, which) -> dialog.cancel());
                    alert.setPositiveButton("Exit", (DialogInterface.OnClickListener)
                            (dialog, which) -> finish());
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                //if first name is empty
                else if (FName.isEmpty()) {
                    alert.setMessage("Please enter a first name.");
                    alert.setTitle("First name is required");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Return", (DialogInterface.OnClickListener)
                            (dialog, which) -> dialog.cancel());
                    alert.setPositiveButton("Exit", (DialogInterface.OnClickListener)
                            (dialog, which) -> finish());
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                //if last name is empty
                else if (LName.isEmpty()) {
                    alert.setMessage("Please enter a last name.");
                    alert.setTitle("Last name is required");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Return", (DialogInterface.OnClickListener)
                            (dialog, which) -> dialog.cancel());
                    alert.setPositiveButton("Exit", (DialogInterface.OnClickListener)
                            (dialog, which) -> finish());
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                //if email is empty
                else if (email.isEmpty()) {
                    alert.setMessage("Please enter an email.");
                    alert.setTitle("Email is required");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Return", (DialogInterface.OnClickListener)
                            (dialog, which) -> dialog.cancel());
                    alert.setPositiveButton("Exit", (DialogInterface.OnClickListener)
                            (dialog, which) -> finish());
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                //else if statement to check string stored in email with the following text format
                //displays error messages in invalid, prompting confirmation
                else if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                    alert.setMessage("Please enter a valid email.");
                    alert.setTitle("Invalid email");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Return", (DialogInterface.OnClickListener)
                            (dialog, which) -> dialog.cancel());
                    alert.setPositiveButton("Exit", (DialogInterface.OnClickListener)
                            (dialog, which) -> finish());
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                //If phone number is filled.. make sure it's valid
                else if (!phone.matches("\\d{10}|\\d{0}")) {
                    alert.setMessage("Please enter a valid 10 digit phone.");
                    alert.setTitle("Invalid Phone");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Return", (DialogInterface.OnClickListener)
                            (dialog, which) -> dialog.cancel());
                    alert.setPositiveButton("Exit", (DialogInterface.OnClickListener)
                            (dialog, which) -> finish());
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                else if (db.searchEmployee(FName, LName)) {
                    alert.setMessage("An employee with the specified name already exists.");
                    alert.setTitle("Duplicate Name Entry");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Return", (DialogInterface.OnClickListener)
                            (dialog, which) -> dialog.cancel());
                    alert.setPositiveButton("Exit", (DialogInterface.OnClickListener)
                            (dialog, which) -> finish());
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                //if all conditions above are met, it will proceed to add employee to database
                else{
                    db.addNewEmployee(FName.trim(), LName.trim(), mon, tues, wed,
                            thur, fri, sat, sun, open, close, email, phone);

                    ArrayList<Employees> ar = db.readEmployeeList();
                    EmployeeAdapter employeeAdapter = new EmployeeAdapter(getApplicationContext(), ar);
                    employeeAdapter.notifyDataSetChanged();
                    finish();
                    Toast.makeText(EmployeeInfo.this, ""+FName+ " " + LName + " Added.", Toast.LENGTH_SHORT).show();
                }

            }

        });

    }
    //merges the 2 boolean morning/afternoon training and returns string value
    public String checkAvailability(Boolean morning, Boolean afternoon){
        String avail;
        if(morning && afternoon){
            avail = "All day";
        }else if (morning){
            avail = "Morning";
        }else if (afternoon){
            avail = "Afternoon";
        }else{
            avail = "NULL";
        }
        return avail;
    }
    //Makes the up button on this activity go back to the previous Fragment that invoked it.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}