package com.example.myapplication.ui.dashboard;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.example.myapplication.Adapter.EmployeeAdapter;
import com.example.myapplication.DBHandler;
import com.example.myapplication.EmployeeInfo;
import com.example.myapplication.Employees;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ViewEmployee;
import com.example.myapplication.databinding.FragmentDashboardBinding;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {
    //Initializing variables for use..
    private FragmentDashboardBinding binding;
    ArrayList<Employees> arrayList;
    EmployeeAdapter employeeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Assign the listview into EmployeeList
        ListView EmployeeList = binding.EmployeeList;

        DBHandler db = new DBHandler(getActivity().getApplicationContext());

        // Below reads the database from a created Array...
        // ... and performs a custom adapter for values wanted
        arrayList = db.readEmployeeList();
        employeeAdapter = new EmployeeAdapter(getActivity().getApplicationContext(), arrayList);
        EmployeeList.setAdapter(employeeAdapter);
        //employeeAdapter.notifyDataSetChanged();

        refreshData();


        // Below Sets the AdapterView into clickable parameters...
        EmployeeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), ViewEmployee.class);

                String fname = employeeAdapter.getItem(position).getFName();
                String lname = employeeAdapter.getItem(position).getLName();
                String email = employeeAdapter.getItem(position).getEmail();
                String phoneNum = employeeAdapter.getItem(position).getPhone();
                String sun = employeeAdapter.getItem(position).getSun();
                String mon = employeeAdapter.getItem(position).getMon();
                String tue = employeeAdapter.getItem(position).getTue();
                String wed = employeeAdapter.getItem(position).getWed();
                String thu = employeeAdapter.getItem(position).getThu();
                String fri = employeeAdapter.getItem(position).getFri();
                String sat = employeeAdapter.getItem(position).getSat();
                String opener = employeeAdapter.getItem(position).getOpen();
                String closer = employeeAdapter.getItem(position).getClose();

                i.putExtra("first", fname);
                i.putExtra("last", lname);
                i.putExtra("email", email);
                i.putExtra("phone", phoneNum);
                i.putExtra("sun", sun);
                i.putExtra("mon", mon);
                i.putExtra("tue", tue);
                i.putExtra("wed", wed);
                i.putExtra("thu", thu);
                i.putExtra("fri", fri);
                i.putExtra("sat", sat);
                i.putExtra("opener", opener);
                i.putExtra("closer", closer);

                startActivity(i);
            }
        });

        return root;
    }
    //Used to make the listview of Employee list to be refreshed every time this page is resumed.
    private void refreshData() {
        DBHandler db = new DBHandler(getActivity().getApplicationContext());
        arrayList = db.readEmployeeList();
        employeeAdapter.setData(arrayList);
    }
    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}