package com.example.myapplication.export;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.DBHandler;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//This creates exportation method for Employee History as text file
public class ExportEmployeeHistory {
    public static void exportHistory(Context context) {
        try {
            //Grabs the database...
            DBHandler db = new DBHandler(context);
            SQLiteDatabase dbread = db.getReadableDatabase();

            //Grabs current date to be used for txt file name..
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String date = LocalDateTime.now().format(formatter);
            String txtfilename = "Employee_History_(YYYYMMDD)" + date + ".txt";

            String first="";
            String last="";
            Cursor cursor = dbread.rawQuery("SELECT el.FirstName, el.LastName, el.StartDay, el.EndDay," +
                    "COUNT(CASE WHEN sl.OpenOrClose = 'Allday' AND DATE(sl.ShiftDate) <= CURDATE() THEN 2 " +
                    "WHEN sl.OpenOrClose != 'Allday' AND DATE(sl.ShiftDate) <= CURDATE() THEN 1 " +
                    "ELSE 0 END) AS WORKED_SHIFT," +
                    "COUNT(CASE WHEN sl.OpenOrClose = 'Allday' AND DATE(sl.ShiftDate) > CURDATE() THEN 2 " +
                    "WHEN sl.OpenOrClose != 'Allday' AND DATE(sl.ShiftDate) > CURDATE() THEN 1 " +
                    "ELSE 0 END) AS PENDING_SHIFT " +
                    "FROM EmployeeList el " +
                    "LEFT JOIN ShiftList sl ON el.ID = sl.EmpID " +
                    "WHERE el.FirstName = ? AND el.LastName = ?", new String[] {first, last});


            //Writes the selected from database into txt file..
            FileWriter writer = new FileWriter(txtfilename);
            while (cursor.moveToNext()) {
                String fname = cursor.getString(cursor.getColumnIndexOrThrow("FirstName"));
                String lname = cursor.getString(cursor.getColumnIndexOrThrow("LastName"));
                String start = cursor.getString(cursor.getColumnIndexOrThrow("StartDay"));
                String end = cursor.getString(cursor.getColumnIndexOrThrow("EndDay"));
                int workedShift = cursor.getInt(cursor.getColumnIndexOrThrow("WORKED_SHIFT"));
                int pendingShift = cursor.getInt(cursor.getColumnIndexOrThrow("PENDING_SHIFT"));
                //If there's no end date, make it display "N/A"
                if (end == null || end.isEmpty()) {
                    end = "N/A";
                }

                //Displays how it is exported
                //Formats as "| FNAME_COL LNAME_COL | Start: START_COL | Terminated: END_COL | Shifts Worked: # | Pending Shifts: # |"
                String row ="| " + fname + " " + lname + " | Start: " + start + " | Terminated: " + end + " | Shifts worked: " + workedShift + " | Pending Shifts: " + pendingShift + " |\n";

                writer.write(row);
            }

            //Close the following to prevent memory leaks
            writer.close();
            cursor.close();
            dbread.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

