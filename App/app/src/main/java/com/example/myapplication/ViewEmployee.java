package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ui.dashboard.DashboardFragment;
import com.google.android.material.textfield.TextInputEditText;

public class ViewEmployee extends AppCompatActivity {

    TextView nameText;
    TextView emailText;
    TextView phoneText;
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
    Button deleteBtn;
    Button editBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_employee_info);

        String fname, lname;
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
        nameText = findViewById(R.id.nameLabel);
        emailText = findViewById(R.id.empEmail);
        phoneText = findViewById(R.id.empPhone);
        sunBox = findViewById(R.id.cbSun4);
        monMornBox = findViewById(R.id.cbMonMorn4);
        monAftBox = findViewById(R.id.cbMonAft4);
        tueMornBox = findViewById(R.id.cbTueMorn3);
        tueAftBox = findViewById(R.id.cbTueAft3);
        wedMornBox = findViewById(R.id.cbWedMorn3);
        wedAftBox = findViewById(R.id.cbWedAft3);
        thuMornBox = findViewById(R.id.cbThuMorn3);
        thuAftBox = findViewById(R.id.cbThuAft3);
        friMornBox = findViewById(R.id.cbFriMorn3);
        friAftBox = findViewById(R.id.cbFriAft3);
        satBox = findViewById(R.id.cbSat3);
        openerBox = findViewById(R.id.cbOpener2);
        closerBox = findViewById(R.id.cbCloser2);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                fname = null;
                lname = null;
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
                fname = extras.getString("first");
                lname = extras.getString("last");
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
            fname = savedInstanceState.getString("first");
            lname = savedInstanceState.getString("last");
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

        //If there's a number...
        if (!phone.isEmpty()) {
            //Formats phone number to Canadian number (###) ###-####
            String phoneformatCA = String.format("(%s) %s-%s", phone.substring(0, 3), phone.substring(3, 6), phone.substring(6));
            phoneText.setText(phoneformatCA);

        } else {
            phoneText.setText(phone);
        }

        //display info
        nameText.setText(fname + " " + lname);
        emailText.setText(email);
        //phoneText.setText(phone);
        weekEndCheck(sunBox, sun);
        weekDayCheck(monMornBox, monAftBox, mon);
        weekDayCheck(tueMornBox, tueAftBox, tue);
        weekDayCheck(wedMornBox, wedAftBox, wed);
        weekDayCheck(thuMornBox, thuAftBox, thu);
        weekDayCheck(friMornBox, friAftBox, fri);
        weekEndCheck(satBox, sat);
        qualifyCheck(openerBox, closerBox, opener, closer);

        DBHandler db = new DBHandler(getApplicationContext());

        //delete button action
        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewEmployee.this);
                builder.setCancelable(true);
                builder.setTitle("Delete Confirmation");
                builder.setMessage("Are you sure you want to delete " + fname + " " + lname + "?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.deleteEmployee(fname, lname);
                        finish();
                        Toast.makeText(ViewEmployee.this, fname + " " + lname + " was deleted.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        //edit button function
        editBtn = findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditEmployee.class);

                intent.putExtra("first", fname);
                intent.putExtra("last", lname);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                intent.putExtra("sun", sun);
                intent.putExtra("mon", mon);
                intent.putExtra("tue", tue);
                intent.putExtra("wed", wed);
                intent.putExtra("thu", thu);
                intent.putExtra("fri", fri);
                intent.putExtra("sat", sat);
                intent.putExtra("opener", opener);
                intent.putExtra("closer", closer);

                //startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });

    }

    private void weekDayCheck(CheckBox mornBox, CheckBox aftBox, String availability) {
        if (availability.equalsIgnoreCase("Afternoon")) {
            aftBox.setChecked(true);
            mornBox.setChecked(false);
        }

        else if (availability.equalsIgnoreCase("Morning")) {
            mornBox.setChecked(true);
            aftBox.setChecked(false);
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

        else {
            dayBox.setChecked(false);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            onBackPressed();
        }
    }
}
