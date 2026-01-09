package com.example.myapplication.export;

import static com.itextpdf.text.html.WebColors.getRGBColor;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.myapplication.CalendarUtils;
import com.example.myapplication.DBHandler;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.commons.io.IOUtils;

//Creates method to export the monthly schedule as pdf
public class ExportMonthlySchedule {
    private Handler handler;

    public ExportMonthlySchedule(Handler handler) {
        this.handler = handler;
    }

    public void exportCalendar(int month, int year, Context context, boolean optionForQualification) {
        try {
            DBHandler db = new DBHandler(context);

            //Sets the download path to the downloads directory
            String downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            String filePath = downloadsPath + "/" + getMonthName(month) + year + "_calendar.pdf";

            //Prevent from overwrite
            int fileIndex = 1;
            while (new File(filePath).exists()) {
                filePath = downloadsPath + "/" + getMonthName(month) + year + "_calendar(" + fileIndex + ").pdf";
                fileIndex++;
            }

            //Changes file path name for qualification if checked
           if (optionForQualification) {
                //Changes file name to match Qualification selection
                filePath = downloadsPath + "/" + getMonthName(month) + year + "_calendar_qualification.pdf";



                //Prevent from overwrite
                int fileIndexQual = 1;
                while (new File(filePath).exists()) {
                    filePath = downloadsPath + "/" + getMonthName(month) + year + "_calendar_qualification(" + fileIndexQual + ").pdf";
                    fileIndexQual++;
                }
            }



            String directoryName = filePath;
            //Removes the string until the last '/' and +1 to skip that '/'.
            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);

            //Creates PDF document
            //Document document = new Document(PageSize.A4, 15, 15, 50, 50);
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            /*
            //Grabs images from assets
            AssetManager assetManager = context.getAssets();
            InputStream inputStreamAllDay =assetManager.open("images/allday.jpg");
            Image imgallday = Image.getInstance(IOUtils.toByteArray(inputStreamAllDay));
            inputStreamAllDay.close();

             */

            //Creates the header at top for Month and Year
            Font mmyyheaderFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
            Paragraph header = new Paragraph();
            header.setFont(mmyyheaderFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.add(getMonthName(month) + " " + year);
            document.add(header);

            //Add spacing between the header and the days of the week
            document.add(new Paragraph(" "));

            //Creates the 7 days of the week for columns
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);


            //The days of the week header
            String [] daysofWeek = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
            Font weekFonts = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            for (String day: daysofWeek) {
                PdfPCell headerCells = new PdfPCell(new Paragraph(day, weekFonts));
                headerCells.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(headerCells);
            }

            //Gets the first day of that month
            LocalDate firstOfMonth = LocalDate.of(year, month, 1);
            DayOfWeek firstDayOfWeek = firstOfMonth.getDayOfWeek();
            //Total days in that month
            ArrayList<LocalDate> daysInMonthArray = CalendarUtils.actualDaysInMonthArray(firstOfMonth);
            int numDays = daysInMonthArray.size();

            //Can modify below to include previous month's data to fill calendar
            //Creates empty cells before first day.. so first day isn't on first column/row
            for (int i = 1; i <= firstDayOfWeek.getValue(); i++) {
                PdfPCell blankCell = new PdfPCell();
                table.addCell(blankCell);
            }
            //Creates the day cells based on total days in that month
            Font dayFonts = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            for (int dayNum = 1; dayNum <= numDays; dayNum++) {
                PdfPCell dayCells = new PdfPCell(new Paragraph(String.valueOf(dayNum), dayFonts));
                dayCells.setHorizontalAlignment(Element.ALIGN_LEFT);
                dayCells.setFixedHeight(120f);

                //Creates the date to pass into shiftsbydate to query database(ShiftList)
                Calendar calendarquery = Calendar.getInstance();
                calendarquery.set(year, month - 1, dayNum);
                LocalDate date = LocalDate.of(calendarquery.get(Calendar.YEAR), calendarquery.get(Calendar.MONTH) + 1, calendarquery.get(Calendar.DAY_OF_MONTH));

                Cursor cursor = db.shiftsbydate(date);
                List<String> employeeNames = new ArrayList<>();
                String daytype = "";
                //Creates the background/color on names
                Map<String, BaseColor> positionColorMap = new HashMap<>();
                positionColorMap.put("Opening", getRGBColor("#FFB533"));
                positionColorMap.put("Closing", getRGBColor("#239BFF"));
                positionColorMap.put("Allday", getRGBColor("#FFFF00"));

                Paragraph employees = new Paragraph();
                Font employeeFont = new Font(Font.FontFamily.TIMES_ROMAN, 12);

                while (cursor.moveToNext()) {
                    String first = cursor.getString(1);
                    String last = cursor.getString(2);
                    String position = cursor.getString(7);
                    String typeofday = cursor.getString(8);
                    employeeNames.add(first + " " + last);
                    daytype = typeofday.equals("1") ? "BUSY" : "";

                    if (optionForQualification) { //If Checkbox for Training? is checked...

                        Cursor c = db.getEmployee(first, last);
                        while (c.moveToNext()) {

                            String open = c.getString(10);
                            String close = c.getString(11);

                            if (open.equals("Trained") && close.equals("Trained")) {
                                //Based on position's string value, from the map above, sets the color.. for background
                                BaseColor backgroundColor = positionColorMap.getOrDefault(position, getRGBColor("#FFFFFF"));
                                Chunk chunk = new Chunk(first + " " + last + " (O/C)" + "\n", employeeFont);
                                chunk.setBackground(backgroundColor);

                                employees.add(chunk);
                            } else if (open.equals("Training") && close.equals("Trained"))  {
                                //Based on position's string value, from the map above, sets the color.. for background
                                BaseColor backgroundColor = positionColorMap.getOrDefault(position, getRGBColor("#FFFFFF"));
                                Chunk chunk = new Chunk(first + " " + last + " (C)" +"\n", employeeFont);
                                chunk.setBackground(backgroundColor);

                                employees.add(chunk);
                            } else if (open.equals("Trained") && close.equals("Training"))  {
                                //Based on position's string value, from the map above, sets the color.. for background
                                BaseColor backgroundColor = positionColorMap.getOrDefault(position, getRGBColor("#FFFFFF"));
                                Chunk chunk = new Chunk(first + " " + last + " (O)" +"\n", employeeFont);
                                chunk.setBackground(backgroundColor);

                                employees.add(chunk);
                            } else { //No qualifications
                                //Based on position's string value, from the map above, sets the color.. for background
                                BaseColor backgroundColor = positionColorMap.getOrDefault(position, getRGBColor("#FFFFFF"));
                                Chunk chunk = new Chunk(first + " " + last +"\n", employeeFont);
                                chunk.setBackground(backgroundColor);

                                employees.add(chunk);
                            }
                        }
                        c.close();

                    }else { //If no other options picked..

                        //Based on position's string value, from the map above, sets the color.. for background
                        BaseColor backgroundColor = positionColorMap.getOrDefault(position, getRGBColor("#FFFFFF"));
                        Chunk chunk = new Chunk(first + " " + last + "\n", employeeFont);
                        chunk.setBackground(backgroundColor);

                        employees.add(chunk);

                    }

                }
                cursor.close();

                PdfPCell employeesCell = new PdfPCell(employees);
                employeesCell.setVerticalAlignment(Element.ALIGN_TOP);
                employeesCell.setPadding(5);

                PdfPCell daytypeP = new PdfPCell(new Paragraph(daytype, new Font(Font.FontFamily.TIMES_ROMAN, 8)));
                daytypeP.setBorder(Rectangle.NO_BORDER);

                //Creates the column for the day number and type of day (eg busy)
                PdfPTable columnfordaycells = new PdfPTable(1);
                dayCells.setBorder(Rectangle.NO_BORDER);
                columnfordaycells.addCell(dayCells);
                columnfordaycells.addCell(daytypeP);

                employeesCell.setBorder(Rectangle.NO_BORDER);

                //Offsets the daycell and employeescell 1:5
                //And makes the employee's list appear on the day's cell..
                PdfPTable rowTable = new PdfPTable(new float[]{1, 5});
                rowTable.setWidthPercentage(100);
                rowTable.addCell(columnfordaycells);
                rowTable.addCell(employeesCell);

                table.addCell(rowTable);

                //If next sunday, cells go to next row
                if (firstDayOfWeek == DayOfWeek.SUNDAY) {
                    table.completeRow();
                }
            }
            //Writes the rest of the last week.
            DayOfWeek lastDayOfWeek = daysInMonthArray.get(numDays - 1).getDayOfWeek();
            for (int i = lastDayOfWeek.getValue() + 1; i <= 7; i++) {
                PdfPCell blankCell = new PdfPCell();
                table.addCell(blankCell);
            }

            //Handler to display toast message on main UI
              handler.post(new Runnable() {
                @Override
                public void run() {
                    // Toast message displays with directory and file name
                    //Toast.makeText(context, "Exported " + directoryName, Toast.LENGTH_SHORT).show();

                    // Toast message displays file name
                    Toast.makeText(context, "Exported " + fileName, Toast.LENGTH_SHORT).show();

                }
            });

            //Writes to PDF
            document.add(table);
            db.close();
            document.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Function to grab month name based on int value
    public String getMonthName(int month) {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return months[month-1];
    }
}

