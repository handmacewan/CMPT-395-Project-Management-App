package com.example.myapplication;
import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper {

    //Attributes for database
    private static final String DB_NAME = "EmployeeDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "EmployeeList";

    //Attributes for the database columns
    private static final String ID_COL = "ID";
    private static final String FNAME_COL = "FirstName";
    private static final String LNAME_COL = "LastName";
    private static final String MON_COL = "Monday";
    private static final String TUES_COL = "Tuesday";
    private static final String WED_COL = "Wednesday";
    private static final String THUR_COL = "Thursday";
    private static final String FRI_COL = "Friday";
    private static final String SAT_COL = "Saturday";
    private static final String SUN_COL = "Sunday";

    private static final String OPEN_COL = "OpenQualification";

    private static final String CLOSE_COL = "CloseQualification";

    private static final String EMAIL_COL = "Email";
    private static final String PHONE_COL = "Phone";
    private static final String START_COL = "StartDay";
    private static final String END_COL = "EndDay";


    private static final String PRIMARY_COL = "PID";
    private static final String EMPLOYEE_ID_COL = "EmpID";
    private static final String DATE_COL = "ShiftDate";
    private static final String POSITION_COL = "OpenOrClose";
    private static final String BUSY_COL = "BusyDay";
    private static final String TABLE_NAME2 = "ShiftList";



    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    //Creation of database table..
    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FNAME_COL + " TEXT,"
                + LNAME_COL + " TEXT,"
                + MON_COL + " TEXT,"
                + TUES_COL + " TEXT,"
                + WED_COL + " TEXT,"
                + THUR_COL + " TEXT,"
                + FRI_COL + " TEXT,"
                + SAT_COL + " TEXT,"
                + SUN_COL + " TEXT,"
                + OPEN_COL + " TEXT,"
                + CLOSE_COL + " TEXT,"
                + EMAIL_COL + " TEXT,"
                + PHONE_COL + " TEXT,"
                + START_COL + " TEXT,"
                + END_COL + " TEXT)";
        db.execSQL(query);

        String query2 = "CREATE TABLE " + TABLE_NAME2 + " ("
                + PRIMARY_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FNAME_COL + " TEXT,"
                + LNAME_COL + " TEXT,"
                + DATE_COL + " TEXT,"
                + EMPLOYEE_ID_COL + " INTEGER,"
                + OPEN_COL + " TEXT,"
                + CLOSE_COL + " TEXT,"
                + POSITION_COL + " TEXT,"
                + BUSY_COL + " TEXT)";
        db.execSQL(query2);

    }

    //Adding new entry into database
    public void addNewEmployee(String firstName, String lastname, String mon, String tues, String wed, String thur,
                               String fri, String sat, String sun, String open, String close, String email, String phone) {
        //create a writable database
        SQLiteDatabase db = this.getWritableDatabase();
        //create new instance of ContentValues, that will assign column to variable
        ContentValues values = new ContentValues();
        //Grabs current date to store into DB signaling start date of employment
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
        String date = dateFormat.format(currentDate);

        values.put(FNAME_COL, firstName);
        values.put(LNAME_COL, lastname);
        values.put(MON_COL, mon);
        values.put(TUES_COL, tues);
        values.put(WED_COL, wed);
        values.put(THUR_COL, thur);
        values.put(FRI_COL, fri);
        values.put(SAT_COL, sat);
        values.put(SUN_COL, sun);
        values.put(OPEN_COL, open);
        values.put(CLOSE_COL, close);
        values.put(EMAIL_COL, email);
        values.put(PHONE_COL, phone);
        values.put(START_COL, date);
        //insert the values into database
        db.insert(TABLE_NAME, null, values);
        //close database
        db.close();
    }

    //Function to Search for Employee with same name...
    public boolean searchEmployee(String First, String Last) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from EmployeeList where FirstName=? AND LastName=?", new String[]{First, Last});
        if (c.getCount() == 0) {
            c.close();
            return false;
        }
        while (c.moveToNext()) {
            c.close();
            return true;
        }
        c.close();
        db.close();

        return false;
    }

    //Function to Search and return that Employee's info...
    //To use.. Cursor c = db.getEmployee("First", "Last");
    //      .. if (c != null && c.movetoFirst()) {
    //      .. String fname = c.getString(c.getColumnIndex("FirstName"));
    public Cursor getEmployee(String First, String Last) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from EmployeeList where FirstName=? AND LastName=?", new String[]{First, Last});
        if (c.getCount() == 0) {
            c.close();
            return null;
        }
        db.close();
        return c;
    }

    //get list of employees with corresponding availability
    public ArrayList<Employees> getAvailEmployee(String day, String time) {
        ArrayList<Employees> availEmployees = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from EmployeeList where " + day + "=?", new String[]{time});
        if (c.moveToFirst()) {
            do {
                availEmployees.add(new Employees(c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5),
                        c.getString(6),
                        c.getString(7),
                        c.getString(8),
                        c.getString(9),
                        c.getString(10),
                        c.getString(11),
                        c.getString(12),
                        c.getString(13)));

            } while (c.moveToNext());
        }
        c.close();
        db.close();

        return availEmployees;
    }


    // An Array to to help view the Database
    public ArrayList<Employees> readEmployeeList(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM EmployeeList WHERE " + END_COL + " IS NULL OR " + END_COL + " = ''", null);
        ArrayList<Employees> EmployeeArrayList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                EmployeeArrayList.add(new Employees(c.getString(1),
                                                    c.getString(2),
                                                    c.getString(3),
                                                    c.getString(4),
                                                    c.getString(5),
                                                    c.getString(6),
                                                    c.getString(7),
                                                    c.getString(8),
                                                    c.getString(9),
                                                    c.getString(10),
                                                    c.getString(11),
                                                    c.getString(12),
                                                    c.getString(13)));

            } while (c.moveToNext());
        }
        c.close();
        db.close();

        return EmployeeArrayList;
    }

    //Function to delete employee..
    //Changed to add date as termination
    public void deleteEmployee(String First, String Last){
        SQLiteDatabase db = this.getWritableDatabase();
        //Below grabs current date to store into DB for date of termination
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
        String date = dateFormat.format(currentDate);

        ContentValues values = new ContentValues();
        //Stored as string "yyyy-mm-dd"
        values.put(END_COL, date);
        //No longer will delete, but add the end date for employee.
        db.update(TABLE_NAME, values, FNAME_COL + "=? AND " + LNAME_COL + " =? ", new String[] {First, Last});
        //Looks at table2 where first and last names are to delete from further scheduling
        db.delete(TABLE_NAME2, FNAME_COL + " =? AND " + LNAME_COL + " =? ", new String[]{First, Last});
        db.close();
    }

    //Function to edit values from DB with new values
    public void editEmployee(String originalName, String firstName, String lastName, String mon, String tues,
                             String wed, String thur, String fri, String sat, String sun,
                             String open, String close, String email, String phone) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //Content values for first table
        values.put(FNAME_COL, firstName);
        values.put(LNAME_COL, lastName);
        values.put(MON_COL, mon);
        values.put(TUES_COL, tues);
        values.put(WED_COL, wed);
        values.put(THUR_COL, thur);
        values.put(FRI_COL, fri);
        values.put(SAT_COL, sat);
        values.put(SUN_COL, sun);
        values.put(OPEN_COL, open);
        values.put(CLOSE_COL, close);
        values.put(EMAIL_COL, email);
        values.put(PHONE_COL, phone);
        //Updates first table (employee history)
        db.update(TABLE_NAME, values, FNAME_COL + "=?", new String[] {originalName});

        //Content values for second table
        ContentValues values2 = new ContentValues();
        values2.put(OPEN_COL, open);
        values2.put(CLOSE_COL, close);

        //Updates second table (shifts) with new training qualifications (open/close)
        db.update(TABLE_NAME2, values2, FNAME_COL + "=?", new String[] {originalName});


        db.close();
    }

    //New editemployee function...
    public void editExistingEmployee(String originalFirst, String originalLast, String firstName, String lastName, String mon, String tues,
                             String wed, String thur, String fri, String sat, String sun,
                             String open, String close, String email, String phone) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Content values for first table
        values.put(FNAME_COL, firstName);
        values.put(LNAME_COL, lastName);
        values.put(MON_COL, mon);
        values.put(TUES_COL, tues);
        values.put(WED_COL, wed);
        values.put(THUR_COL, thur);
        values.put(FRI_COL, fri);
        values.put(SAT_COL, sat);
        values.put(SUN_COL, sun);
        values.put(OPEN_COL, open);
        values.put(CLOSE_COL, close);
        values.put(EMAIL_COL, email);
        values.put(PHONE_COL, phone);

        //Searches for employee with same first and last name
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + FNAME_COL + " = ? AND " + LNAME_COL + " = ?", new String[]{originalFirst, originalLast});
        if (cursor.moveToFirst()) {
            //Updates that employee's information
            db.update(TABLE_NAME, values, FNAME_COL + "=? AND " + LNAME_COL + "=?", new String[] {originalFirst, originalLast});

            //Content values for second table
            ContentValues values2 = new ContentValues();
            values2.put(OPEN_COL, open);
            values2.put(CLOSE_COL, close);

            //Updates second table (shifts) with new training qualifications (open/close)
            db.update(TABLE_NAME2, values2, FNAME_COL + "=? AND " + LNAME_COL + "=?", new String[] {originalFirst, originalLast});
        }
        cursor.close();
        db.close();
    }

    public void addShift(int id, String firstname, String lastname, String date, String open,
                         String close, String position, int busyDay){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EMPLOYEE_ID_COL, id);
        values.put(DATE_COL, date );
        values.put(POSITION_COL, position);
        values.put(FNAME_COL, firstname);
        values.put(LNAME_COL, lastname);
        values.put(OPEN_COL, open);
        values.put(CLOSE_COL, close);
        values.put(BUSY_COL, busyDay);
        db.insert(TABLE_NAME2, null, values);
        db.close();
    }


    public void shiftAssignment(String firstName, String lastName, String position, int busyDay){
        SQLiteDatabase db = this.getReadableDatabase();
        LocalDate date = CalendarUtils.selectedDate;

        Date utilDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(utilDate);

        Cursor c = db.rawQuery("select * from EmployeeList where FirstName=? AND LastName=?", new String[]{firstName, lastName});

        if (c.moveToFirst()) {
            do {
                int empID = c.getInt(0);
                addShift(empID, firstName, lastName, formattedDate,
                        c.getString(10), c.getString(11), position, busyDay);
            } while (c.moveToNext());
        }
        //Added to prevent memory leaks
        c.close();
        db.close();
    }

    //Looking at the date, grabs the first and last name with position and returns cursor
    public Cursor shiftsbydate(LocalDate date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ShiftList WHERE ShiftDate = ?", new String[]{String.valueOf(date)});
        return cursor;
    }

    public ArrayList<String> readShiftList(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT FirstName, LastName, ShiftDate, OpenOrClose, OpenQualification, CloseQualification, BusyDay" +
                " FROM ShiftList", null);
        ArrayList<String> ShiftList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                ShiftList.add(c.getString(0) + " " + c.getString(1) + " "
                            + c.getString(2) + " " + c.getString(3) + " "
                            + c.getString(4) + " " + c.getString(5));

            } while (c.moveToNext());
        }
        //Added to prevent memory leaks
        c.close();
        db.close();
        return ShiftList;
    }

    public void deleteShift(LocalDate date){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME2, DATE_COL+ " =? ", new String[]{String.valueOf(date)} );
        db.close();
    }

    //Function to return type of day ..ie string stored in BusyDay
    public int checktypeofday(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT BusyDay FROM ShiftList WHERE ShiftDate = ?", new String[]{date});
        int busy = 0;
        if (cursor.moveToFirst()) {
            busy = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return busy;
    }
    //Function to grab day of week and return 1-7 (Sunday- Saturday)
    public int dayOfWeek(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ShiftDate FROM ShiftList WHERE ShiftDate = ?", new String[]{date});
        try {
            if (cursor.moveToFirst()) {
                String dateString = cursor.getString(0);
                //Parse the string date into a date
                LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()));
                DayOfWeek dayOfWeek = localDate.getDayOfWeek();
                int day = dayOfWeek.getValue();
                return day;
            }
            return 0;
        } finally {
            //Close to prevent memory leaks
            cursor.close();
            db.close();
        }
    }

    public ArrayList<String> readNames(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT FirstName, LastName FROM EmployeeList WHERE " + END_COL + " IS NULL OR " + END_COL + " = ''", null);
        ArrayList<String> EmployeeArrayList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                EmployeeArrayList.add((c.getString(0)+ " "+ c.getString(1)));
            } while (c.moveToNext());
        }
        c.close();
        db.close();

        return EmployeeArrayList;
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + " S");
        onCreate(db);
    }
}