package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class EditEmployee extends AppCompatActivity {
    TextInputEditText FnameInput, LnameInput;
    TextInputEditText emailInput;
    TextInputEditText phoneInput;
    CheckBox sunBox;
    CheckBox monMornBox;
    CheckBox monAftBox;
    CheckBox tueMornBox;
    CheckBox tueAftBox;
    CheckBox wedMornBox;
    CheckBox wedAftBox;
    CheckBox thuMornBox;
    CheckBox thuAftBox;
    CheckBox friMornBox;
    CheckBox friAftBox;
    CheckBox satBox;
    CheckBox openerBox;
    CheckBox closerBox;

    String first;
    String last;
    String email;
    String phone;
    String sun;
    String mon;
    String tue;
    String wed;
    String thu;
    String fri;
    String sat;
    String opener;
    String closer;
    Button saveBtn;
    //Creates a variable that stores the activity 'ViewEmployee'
    private Class<?> toViewEmployeeInfo = ViewEmployee.class;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_employee_info);

        FnameInput = findViewById(R.id.firstname);
        LnameInput = findViewById(R.id.lastname);
        emailInput = findViewById(R.id.textemail);
        phoneInput = findViewById(R.id.textphone);
        sunBox = findViewById(R.id.cbSun);
        monMornBox = findViewById(R.id.cbMonMorn);
        monAftBox = findViewById(R.id.cbMonAft);
        tueMornBox = findViewById(R.id.cbTueMorn);
        tueAftBox = findViewById(R.id.cbTueAft);
        wedMornBox = findViewById(R.id.cbWedMorn);
        wedAftBox = findViewById(R.id.cbWedAft);
        thuMornBox = findViewById(R.id.cbThuMorn);
        thuAftBox = findViewById(R.id.cbThuAft);
        friMornBox = findViewById(R.id.cbFriMorn);
        friAftBox = findViewById(R.id.cbFriAft);
        satBox = findViewById(R.id.cbSat);
        openerBox = findViewById(R.id.cbOpener);
        closerBox = findViewById(R.id.cbCloser);
        saveBtn = findViewById(R.id.buttonsave);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                first = null;
                last = null;
                email = null;
                phone = null;
                sun = null;
                mon = null;
                tue = null;
                wed = null;
                thu = null;
                fri = null;
                sat = null;
                opener = null;
                closer = null;
            }
            else {
                first = extras.getString("first");
                last = extras.getString("last");
                email = extras.getString("email");
                phone = extras.getString("phone");
                sun = extras.getString("sun");
                mon = extras.getString("mon");
                tue = extras.getString("tue");
                wed = extras.getString("wed");
                thu = extras.getString("thu");
                fri = extras.getString("fri");
                sat = extras.getString("sat");
                opener = extras.getString("opener");
                closer = extras.getString("closer");
            }
        }
        else {
            first = savedInstanceState.getString("first");
            last = savedInstanceState.getString("last");
            email = savedInstanceState.getString("email");
            phone = savedInstanceState.getString("phone");
            sun = savedInstanceState.getString("sun");
            mon = savedInstanceState.getString("mon");
            tue = savedInstanceState.getString("tue");
            wed = savedInstanceState.getString("wed");
            thu = savedInstanceState.getString("thu");
            fri = savedInstanceState.getString("fri");
            sat = savedInstanceState.getString("sat");
            opener = savedInstanceState.getString("opener");
            closer = savedInstanceState.getString("closer");
        }

        //display filled in information
        FnameInput.setText(first);
        LnameInput.setText(last);
        emailInput.setText(email);
        phoneInput.setText(phone);
        weekEndCheck(sunBox, sun);
        weekDayCheck(monMornBox, monAftBox, mon);
        weekDayCheck(tueMornBox, tueAftBox, tue);
        weekDayCheck(wedMornBox, wedAftBox, wed);
        weekDayCheck(thuMornBox, thuAftBox, thu);
        weekDayCheck(friMornBox, friAftBox, fri);
        weekEndCheck(satBox, sat);
        qualifyCheck(openerBox, closerBox, opener, closer);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHandler db = new DBHandler(getApplicationContext());
                AlertDialog.Builder alert = new AlertDialog.Builder(EditEmployee.this);

                //get inputted info
                String newMon = checkAvailability(monMornBox.isChecked(), monAftBox.isChecked());
                String newTue = checkAvailability(tueMornBox.isChecked(), tueAftBox.isChecked());
                String newWed = checkAvailability(wedMornBox.isChecked(), wedAftBox.isChecked());
                String newThu = checkAvailability(thuMornBox.isChecked(), thuAftBox.isChecked());
                String newFri = checkAvailability(friMornBox.isChecked(), friAftBox.isChecked());
                String newSat = checkWeekend(satBox.isChecked());
                String newSun = checkWeekend(sunBox.isChecked());

                opener = ((openerBox.isChecked()) ? "Trained" : "Training");
                closer = ((closerBox.isChecked()) ? "Trained" : "Training");

                String newFName = FnameInput.getText().toString().trim();
                String newLName = LnameInput.getText().toString().trim();
                String newemail = emailInput.getText().toString();
                String newphone = phoneInput.getText().toString();

                //If name changed...
                if (!newFName.equals(first) || !newLName.equals(last)) {
                    Log.d("name did change", "0");

                    //If first, last, and email is empty
                    if (newFName.isEmpty() && newLName.isEmpty() && newemail.isEmpty()) {
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
                    else if (newFName.isEmpty() && newLName.isEmpty()) {
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
                    else if (newFName.isEmpty() && newemail.isEmpty()) {
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
                    else if (newLName.isEmpty() && newemail.isEmpty()) {
                        alert.setMessage("Please fill in a last name and email");
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
                    else if (newFName.isEmpty()) {
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
                    else if (newLName.isEmpty()) {
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
                    else if (newemail.isEmpty()) {
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
                    else if (!newemail.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
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
                    else if (!newphone.matches("\\d{10}|\\d{0}")) {
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
                    else if (db.searchEmployee(newFName, newLName)) {
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
                    else if (!db.searchEmployee(newFName, newLName)) {
                        db.editExistingEmployee(first, last, newFName, newLName, newMon, newTue, newWed, newThu, newFri, newSat,
                                newSun, opener, closer, newemail, newphone);

                        Intent i = new Intent(getApplicationContext(), ViewEmployee.class);
                        i.putExtra("first", newFName);
                        i.putExtra("last", newLName);
                        i.putExtra("email", newemail);
                        i.putExtra("phone", newphone);
                        i.putExtra("sun", newSun);
                        i.putExtra("mon", newMon);
                        i.putExtra("tue", newTue);
                        i.putExtra("wed", newWed);
                        i.putExtra("thu", newThu);
                        i.putExtra("fri", newFri);
                        i.putExtra("sat", newSat);
                        i.putExtra("opener", opener);
                        i.putExtra("closer", closer);

                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        finish();

                        startActivity(i);

                        Toast.makeText(EditEmployee.this, "Saved changes for " + newFName + " " + newLName +
                                ".", Toast.LENGTH_SHORT).show();

                    }
                }
                //else name did not change...
                else if (newFName.equals(first) && newLName.equals(last)) {
                    //if email is empty
                    if (newemail.isEmpty()) {
                        Log.d("name did not change", "1");

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
                    else if (!newemail.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
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
                    else if (!newphone.matches("\\d{9}|\\d{10}|\\d{0}")) {
                        alert.setMessage("Please enter a valid 9 or 10 digit phone.");
                        alert.setTitle("Invalid Phone");
                        alert.setCancelable(false);
                        alert.setNegativeButton("Return", (DialogInterface.OnClickListener)
                                (dialog, which) -> dialog.cancel());
                        alert.setPositiveButton("Exit", (DialogInterface.OnClickListener)
                                (dialog, which) -> finish());
                        AlertDialog dialog = alert.create();
                        dialog.show();
                    }
                    else {
                        db.editExistingEmployee(first, last, newFName, newLName, newMon, newTue, newWed, newThu, newFri, newSat,
                                newSun, opener, closer, newemail, newphone);

                        Intent i = new Intent(getApplicationContext(), ViewEmployee.class);
                        i.putExtra("first", newFName);
                        i.putExtra("last", newLName);
                        i.putExtra("email", newemail);
                        i.putExtra("phone", newphone);
                        i.putExtra("sun", newSun);
                        i.putExtra("mon", newMon);
                        i.putExtra("tue", newTue);
                        i.putExtra("wed", newWed);
                        i.putExtra("thu", newThu);
                        i.putExtra("fri", newFri);
                        i.putExtra("sat", newSat);
                        i.putExtra("opener", opener);
                        i.putExtra("closer", closer);

                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        finish();

                        startActivity(i);

                        Toast.makeText(EditEmployee.this, "Saved changes for " + newFName + " " + newLName +
                                ".", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    private void weekDayCheck(CheckBox mornBox, CheckBox aftBox, String availability) {
        if (availability.equalsIgnoreCase("Afternoon")) {
            aftBox.setChecked(true);
        }

        else if (availability.equalsIgnoreCase("Morning")) {
            mornBox.setChecked(true);
        }

        else if (availability.equalsIgnoreCase("All day")) {
            mornBox.setChecked(true);
            aftBox.setChecked(true);
        }

    }

    private void weekEndCheck(CheckBox dayBox, String availability) {
        if (availability.equalsIgnoreCase("All day")) {
            dayBox.setChecked(true);
        }
    }

    private void qualifyCheck(CheckBox openerBox, CheckBox closerBox, String opener, String closer){
        if (opener.equalsIgnoreCase("Trained")) {
            openerBox.setChecked(true);
        }

        if (closer.equalsIgnoreCase("Trained")) {
            closerBox.setChecked(true);
        }
    }

    private String checkAvailability(Boolean morning, Boolean afternoon){
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

    private String checkWeekend(Boolean day) {
        if (day) {
            return "All day";
        }

        else {
            return "NULL";
        }
    }

    @Override
    public boolean onSupportNavigateUp() { //take back to view employee
        finish();
        return true;
    }

}
