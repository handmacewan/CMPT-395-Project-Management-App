package com.example.myapplication;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarUtils {

    public static LocalDate selectedDate;
    //Changes the format for the date
    public static String formattedDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return date.format(formatter);
    }
    //Looks at the current time (not used yet...)
    public static String formattedTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        return time.format(formatter);
    }
    //Changes the format for date display only month and year, used as header for calendar display in Home view.
    public static String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }
    //Changes the LocalDate to dates that fill a 6x7 grid for 42 days..
    //returning arraylist LocalDate with all days in that month..
    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {daysInMonthArray.add(null);}
            else { daysInMonthArray.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek)); }
        }
        return  daysInMonthArray;
    }

    public static ArrayList<LocalDate> actualDaysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonth = new ArrayList<LocalDate>();
        int numDays = date.getMonth().length(date.isLeapYear());
        LocalDate firstDay = LocalDate.of(date.getYear(), date.getMonth(), 1);

        for (int i = 0; i < numDays; i++) {
            daysInMonth.add(firstDay.plusDays(i));
        }

        return daysInMonth;
    }

    //Looks at Sunday for the first date in the week that is selected for weekly views..
    //Displays the entire week for that day selected (starting on Sunday)..
    //Returns the ArrayList Days
    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = sundayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while (current.isBefore(endDate))
        {
            days.add(current);
            current = current.plusDays(1);
        }
        return days;
    }
    //Sets the first day of the week to be sunday
    private static LocalDate sundayForDate(LocalDate current){
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while (current.isAfter(oneWeekAgo)){
            if(current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;
            current = current.minusDays(1);
        }
        return null;
    }
}
