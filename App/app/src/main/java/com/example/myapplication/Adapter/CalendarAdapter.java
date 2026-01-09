package com.example.myapplication.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CalendarUtils;
import com.example.myapplication.CalendarViewHolder;
import com.example.myapplication.DBHandler;
import com.example.myapplication.R;

import java.time.LocalDate;
import java.util.ArrayList;
//Class is an adapter used for the extension of recyclerview for the calendar display.
public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;



    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }
    //Sets the calendar displayed using the calendar_cell.xml as each day
    //cycling through for the entirety of the month or week
    //the size of days depends on month or week views
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent,false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(days.size() > 15) //month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        else // week view
            layoutParams.height = (int) parent.getHeight();

        return new CalendarViewHolder(view, onItemListener, days);
    }
    //Function to highlight the day selected
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = days.get(position);
        if (date == null)
            holder.dayOfMonth.setText("");
        else {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if (date.equals(CalendarUtils.selectedDate))
                holder.parentView.setBackgroundColor(Color.parseColor("#FFCFB1DE"));
        }
        // Check if there is data for the date in the database
        int numberofEmployee = getCountForDate(date, holder.itemView.getContext());

        //Sets the busy icon for busy days
        if (busyDay(date, holder.itemView.getContext())) {
            holder.busyIcon.setVisibility(View.VISIBLE);
        } else {
            holder.busyIcon.setVisibility(View.INVISIBLE);
        }
        int dayofweek = dayOfWeek(date, holder.itemView.getContext());
        //On a weekday...
        if (dayofweek != 6 && dayofweek != 7) {
            //If the number of employees on a Busy day is 6
            //Or if number of employees on Regular day is 4...
            if ( (numberofEmployee==6 && busyDay(date, holder.itemView.getContext())) ||
                    (numberofEmployee==4 && !busyDay(date, holder.itemView.getContext())) ) {
                holder.cellTextView.setText("    ");
                holder.cellTextView.setBackgroundResource(R.drawable.assign_complete_icon);
            }
            //If on busy day there are at least 1 employee but less than 6...
            else if (numberofEmployee>=1 && numberofEmployee<6 && busyDay(date, holder.itemView.getContext())) {
                holder.cellTextView.setText("    ");
                holder.cellTextView.setBackgroundResource(R.drawable.icon1);
            }
            //If reg day there are at least 1 employee but less than 4...
            else if (numberofEmployee>=1 && numberofEmployee<4 && !busyDay(date, holder.itemView.getContext()) ) {
                holder.cellTextView.setText("    ");
                holder.cellTextView.setBackgroundResource(R.drawable.icon1);
            }
            else {
                holder.cellTextView.setText("");
            }
            //On a weekend..
        } else {
            //If the number of employees on a Busy day is 3
            //Or if number of employees on Regular day is 2...
            if ( (numberofEmployee==3 && busyDay(date, holder.itemView.getContext())) ||
                    (numberofEmployee==2 && !busyDay(date, holder.itemView.getContext())) ) {
                holder.cellTextView.setText("    ");
                holder.cellTextView.setBackgroundResource(R.drawable.assign_complete_icon);
            }
            //If on busy day there are at least 1 employee but less than 3...
            else if (numberofEmployee>=1 && numberofEmployee<3 && busyDay(date, holder.itemView.getContext()) ) {
                holder.cellTextView.setText("    ");
                holder.cellTextView.setBackgroundResource(R.drawable.icon1);
            }
            //If reg day there are at least 1 employee but less than 2...
            else if (numberofEmployee==1 && !busyDay(date, holder.itemView.getContext()) ) {
                holder.cellTextView.setText("    ");
                holder.cellTextView.setBackgroundResource(R.drawable.icon1);
            }
            else {
                holder.cellTextView.setText("");
            }
        }

    }


    private int getCountForDate(LocalDate date, Context context) {
        try {
            DBHandler db = new DBHandler(context);
            SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
            String[] args = new String[] {String.valueOf(date)};
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM ShiftList WHERE ShiftDate = ?", args);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            db.close();
            //System.out.println("Count for date " + date + ": " + count);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private boolean busyDay(LocalDate date, Context context) {
        try {
            DBHandler db = new DBHandler(context);
            int busy = db.checktypeofday(String.valueOf(date));
            db.close();
            return busy==1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private int dayOfWeek(LocalDate date, Context context) {
        DBHandler db = new DBHandler(context);
        db.close();
        return db.dayOfWeek(String.valueOf(date));
    }



    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface OnItemListener
    {
        void onItemClick(int position, LocalDate date);
    }


}
