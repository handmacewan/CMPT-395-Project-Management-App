package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.DBHandler;
import com.example.myapplication.EmployeeInfo;
import com.example.myapplication.Employees;
import com.example.myapplication.R;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
//This class is the creation of an array adapter based on array class Employees.java
public class EmployeeAdapter extends BaseAdapter {


    Context context;
    ArrayList<Employees> arrayList;

    public EmployeeAdapter(Context context, ArrayList<Employees> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public void setData(ArrayList<Employees> arrayList) {
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Employees getItem(int position) {
        return arrayList.get(position);
    }

    //Converts the list into the listview displaying name and email.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_employee,null);
            TextView Employee_Name = (TextView) convertView.findViewById(R.id.employee_name);
            TextView Employee_Email = (TextView) convertView.findViewById(R.id.employee_email);

            Employees employees = arrayList.get(position);

            Employee_Name.setText(employees.getFName()+" "+employees.getLName());
            Employee_Email.setText(employees.getEmail());

        return convertView;
    }

    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    public void updateData(ArrayList<Employees> arrayList){
        DBHandler db = new DBHandler(context.getApplicationContext());
        arrayList.clear();
        arrayList.addAll(db.readEmployeeList());
        notifyDataSetChanged();

    }


}
