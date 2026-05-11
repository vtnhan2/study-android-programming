package com.example.baitap5_5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class StudentAdapter extends ArrayAdapter<Student> {

    private Context context;
    private ArrayList<Student> studentList;

    public StudentAdapter(@NonNull Context context, ArrayList<Student> studentList) {
        super(context, 0, studentList);
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        }

        Student student = studentList.get(position);

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvMssv = convertView.findViewById(R.id.tvMssv);
        TextView tvFaculty = convertView.findViewById(R.id.tvFaculty);
        TextView tvDepartment = convertView.findViewById(R.id.tvDepartment);

        tvName.setText(student.getName());
        tvMssv.setText(String.valueOf(student.getMssv()));
        tvFaculty.setText(student.getFaculty());
        tvDepartment.setText(student.getDepartment());

        return convertView;
    }
}
